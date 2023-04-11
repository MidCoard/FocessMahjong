package top.focess.mahjong.game;

import com.google.common.collect.Lists;
import top.focess.mahjong.game.data.GameData;
import top.focess.mahjong.game.data.TilesData;
import top.focess.mahjong.game.packet.FetchTilePacket;
import top.focess.mahjong.game.packet.GameSyncPacket;
import top.focess.mahjong.game.packet.GameTileActionPacket;
import top.focess.mahjong.game.remote.RemotePlayer;
import top.focess.mahjong.game.rule.MahjongRule;
import top.focess.mahjong.game.rule.manager.GameManager;
import top.focess.mahjong.game.tile.TileState;
import top.focess.mahjong.terminal.TerminalLauncher;
import top.focess.net.packet.Packet;
import top.focess.net.socket.FocessMultiSocket;
import top.focess.scheduler.FocessScheduler;
import top.focess.scheduler.Task;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

public class LocalGame extends Game {

    private static final FocessScheduler FOCESS_SCHEDULER = new FocessScheduler("GameTicker", true);
    private final FocessMultiSocket serverSocket;
    private Task task;
    private GameManager gameManager;

    private final List<UUID> startPlayers = Lists.newArrayList();

    public LocalGame(final FocessMultiSocket serverSocket, final MahjongRule rule) {
        super(rule);
        this.serverSocket = serverSocket;
    }

    public synchronized boolean join(final Player player) {
        if (player.getGame() == this && this.players.contains(player))
            return true;
        if (!this.getRule().checkPlayerSize(this.players.size() + 1))
            return false;
        if (null != player.getGame() || Player.PlayerState.WAITING != player.getPlayerState())
            return false;
        if (GameState.PLAYING == this.getGameState() && !this.startPlayers.contains(player.getId()))
            return false;
        if (GameState.PLAYING == this.getGameState())
            player.setPlayerState(Player.PlayerState.PLAYING);
        final List<Player> old = Lists.newArrayList(this.players);
        this.players.add(player);
        TerminalLauncher.change("players", this, old, this.players);
        player.setGame(this);
        this.syncOtherPlayer(player);
        return true;
    }

    private void syncOtherPlayer(final Player player) {
        for (final Player p : this.players) {
            if (p != player && p instanceof RemotePlayer) {
                final int clientId = ((RemotePlayer) p).getClientId();
                if (-1 == clientId)
                    throw new IllegalStateException("Remote player " + p.getName() + " has no client id");
                this.serverSocket.getReceiver().sendPacket(clientId, new GameSyncPacket(this.getPartGameData(p)));
            }
        }
    }

    private synchronized GameData getPartGameData(final int player) {
        final TilesData tilesData;
        if (GameState.PLAYING == getGameState() && null != this.gameManager)
            tilesData = this.gameManager.getTilesData(player);
        else tilesData = null;
        return new GameData(this.getId(), this.getRule(), this.getGameState(), this.getStartTime(), this.getGameTime(), this.getCountdown(), tilesData, this.players.stream().map(Player::getPlayerData).toList());
    }

    public synchronized boolean leave(final Player player) {
        if (null == player.getGame() && !this.players.contains(player))
            return true;
        final List<Player> old = Lists.newArrayList(this.players);
        if (player.getGame() != this || !this.players.remove(player))
            return false;
        TerminalLauncher.change("players", this, old, this.players);
        player.setGame(null);
        player.setPlayerState(Player.PlayerState.WAITING);
        if (null != this.task) {
            this.task.cancel();
            this.task = null;
        }
        this.syncOtherPlayer(player);
        return true;
    }

    @Override
    public synchronized boolean ready(final Player player) {
        if (GameState.WAITING != this.getGameState())
            return false;
        if (this.players.contains(player))
            if (Player.PlayerState.WAITING == player.getPlayerState()) {
                player.setPlayerState(Player.PlayerState.READY);
                if (this.players.stream().allMatch(p -> Player.PlayerState.READY == p.getPlayerState())) {
                    this.setStartTime(this.getRule().getReadyTime(this.players.size()));
                    if (null != this.task)
                        this.task.cancel();
                    if (-1 != this.getStartTime())
                        this.task = LocalGame.FOCESS_SCHEDULER.runTimer(this::countdown, Duration.ZERO, Duration.ofSeconds(1));
                }
                this.syncOtherPlayer(player);
                return true;
            } else return Player.PlayerState.READY == player.getPlayerState();
        return false;
    }

    @Override
    public synchronized boolean unready(final Player player) {
        if (GameState.WAITING != this.getGameState())
            return false;
        if (this.players.contains(player))
            if (Player.PlayerState.READY == player.getPlayerState()) {
                player.setPlayerState(Player.PlayerState.WAITING);
                if (null != this.task) {
                    this.task.cancel();
                    this.task = null;
                }
                this.syncOtherPlayer(player);
                return true;
            } else return Player.PlayerState.WAITING == player.getPlayerState();
        return false;
    }

    @Override
    public GameTileState getGameTileState() {
        if (null == this.gameManager || GameState.PLAYING != this.getGameState())
            return null;
        return this.gameManager.getGameTileState();
    }

    @Override
    public void doTileAction(final GameTileActionPacket.TileAction tileAction, final Player player, final TileState... tileStates) {
        if (null == this.gameManager || GameState.PLAYING != this.getGameState())
            return;
        this.gameManager.doTileAction(tileAction, this.startPlayers.indexOf(player.getId()), tileStates);
    }

    @Override
    public void larkSuit(final RemotePlayer player, final TileState.TileStateCategory category) {
        if (null == this.gameManager || GameState.PLAYING != this.getGameState())
            return;
        this.gameManager.larkSuit(this.startPlayers.indexOf(player.getId()), category);
    }

    private synchronized void countdown() {
        if (GameState.WAITING != this.getGameState())
            return;
        if (0 == this.getStartTime()) {
            this.task.cancel();
            this.task = null;
            this.start();
            return;
        }
        this.countdownStartTime();
        this.syncPlayer();
    }

    public synchronized void start() {
        if (GameState.WAITING != this.getGameState())
            return;
        if (!this.players.stream().allMatch(player -> Player.PlayerState.READY == player.getPlayerState()))
            return;
        this.setGameState(GameState.PLAYING);
        this.players.forEach(player -> player.setPlayerState(Player.PlayerState.PLAYING));
        this.startPlayers.clear();
        for (final Player player : this.players)
            this.startPlayers.add(player.getId());
        this.gameManager = this.getRule().getGameManager(this, this.players.size());
        this.setCountdown(this.gameManager.getCountdown());
        this.syncPlayer();
        this.task = LocalGame.FOCESS_SCHEDULER.runTimer(this::tick, Duration.ZERO, Duration.ofSeconds(1));
    }

    public synchronized void tick() {
        if (GameState.PLAYING != this.getGameState())
            return;
        this.tickGameTime();
        this.gameManager.tick();
        this.setCountdown(this.gameManager.getCountdown());
        this.syncPlayer();
        if (GameTileState.DISCARDING == this.gameManager.getGameTileState()) {
            final Player player = this.getPlayer(this.gameManager.getCurrentPlayer());
            if (null != player && null != this.gameManager.getCurrentTileState())
                if (player instanceof LocalPlayer)
                    TerminalLauncher.change("fetchTileState", player, null, this.gameManager.getCurrentTileState());
                else if (player instanceof RemotePlayer)
                    this.serverSocket.getReceiver().sendPacket(((RemotePlayer) player).getClientId(), new FetchTilePacket(player.getId(), this.getId(),  this.gameManager.getCurrentTileState()));
        }
    }

    private void syncPlayer() {
        this.syncOtherPlayer(null);
    }

    public synchronized void end() {
        if (GameState.PLAYING != this.getGameState())
            return;
        this.setGameState(GameState.WAITING);
        this.players.forEach(player -> player.setPlayerState(Player.PlayerState.WAITING));
        this.syncPlayer();
        this.startPlayers.clear();
    }

    public GameData getPartGameData(final Player player) {
        return this.getPartGameData(this.startPlayers.indexOf(player.getId()));
    }

    public void sendPacket(final Packet packet) {
        this.players.forEach(player -> {
            if (player instanceof  RemotePlayer) {
                final int clientId = ((RemotePlayer) player).getClientId();
                if (-1 == clientId)
                    throw new IllegalStateException("Remote player " + player.getName() + " has no client id");
                this.serverSocket.getReceiver().sendPacket(clientId, packet);
            }
        });
    }

    public Player getPlayer(final int index) {
        final UUID id = this.startPlayers.get(index);
        for (final Player player : this.players)
            if (player.getId().equals(id))
                return player;
        return null;
    }

    public UUID getPlayerId(final int player) {
        return this.startPlayers.get(player);
    }

}
