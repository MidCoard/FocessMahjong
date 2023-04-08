package top.focess.mahjong.game;

import com.google.common.collect.Lists;
import top.focess.mahjong.game.data.GameData;
import top.focess.mahjong.game.data.TilesData;
import top.focess.mahjong.game.packet.FetchTilePacket;
import top.focess.mahjong.game.packet.GameSyncPacket;
import top.focess.mahjong.game.remote.RemotePlayer;
import top.focess.mahjong.game.rule.MahjongRule;
import top.focess.mahjong.game.rule.manager.GameManager;
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

    public LocalGame(FocessMultiSocket serverSocket, MahjongRule rule) {
        super(rule);
        this.serverSocket = serverSocket;
    }

    public synchronized boolean join(Player player) {
        if (player.getGame() == this && this.players.contains(player))
            return true;
        if (!this.getRule().checkPlayerSize(players.size() + 1))
            return false;
        if (player.getGame() != null || player.getPlayerState() != Player.PlayerState.WAITING)
            return false;
        if (this.getGameState() == GameState.PLAYING && !startPlayers.contains(player.getId()))
            return false;
        if (this.getGameState() == GameState.PLAYING)
            player.setPlayerState(Player.PlayerState.PLAYING);
        List<Player> old = Lists.newArrayList(this.players);
        this.players.add(player);
        TerminalLauncher.change("players", this, old, this.players);
        player.setGame(this);
        this.syncOtherPlayer(player);
        return true;
    }

    private void syncOtherPlayer(Player player) {
        for (Player p : this.players) {
            if (p != player && p instanceof RemotePlayer) {
                int clientId = ((RemotePlayer) p).getClientId();
                if (clientId == -1)
                    throw new IllegalStateException("Remote player " + p.getName() + " has no client id");
                this.serverSocket.getReceiver().sendPacket(clientId, new GameSyncPacket(this.getPartGameData(p)));
            }
        }
    }

    private synchronized GameData getPartGameData(int player) {
        TilesData tilesData;
        if (getGameState() == GameState.PLAYING && this.gameManager != null)
            tilesData = this.gameManager.getTilesData(player);
        else tilesData = null;
        return new GameData(this.getId(), this.getRule(), this.getGameState(), this.getStartTime(), this.getGameTime(), this.getCountdown(), tilesData, this.players.stream().map(Player::getPlayerData).toList());
    }

    public synchronized boolean leave(Player player) {
        if (player.getGame() == null && !this.players.contains(player))
            return true;
        List<Player> old = Lists.newArrayList(this.players);
        if (player.getGame() != this || !this.players.remove(player))
            return false;
        TerminalLauncher.change("players", this, old, this.players);
        player.setGame(null);
        player.setPlayerState(Player.PlayerState.WAITING);
        if (this.task != null) {
            this.task.cancel();
            this.task = null;
        }
        this.syncOtherPlayer(player);
        return true;
    }

    @Override
    public synchronized boolean ready(Player player) {
        if (this.getGameState() != GameState.WAITING)
            return false;
        if (this.players.contains(player))
            if (player.getPlayerState() == Player.PlayerState.WAITING) {
                player.setPlayerState(Player.PlayerState.READY);
                if (this.players.stream().allMatch(p -> p.getPlayerState() == Player.PlayerState.READY)) {
                    this.setStartTime(this.getRule().getReadyTime(this.players.size()));
                    if (this.task != null)
                        this.task.cancel();
                    if (this.getStartTime() != -1)
                        this.task = FOCESS_SCHEDULER.runTimer(this::countdown, Duration.ZERO, Duration.ofSeconds(1));
                }
                this.syncOtherPlayer(player);
                return true;
            } else return player.getPlayerState() == Player.PlayerState.READY;
        return false;
    }

    @Override
    public synchronized boolean unready(Player player) {
        if (this.getGameState() != GameState.WAITING)
            return false;
        if (this.players.contains(player))
            if (player.getPlayerState() == Player.PlayerState.READY) {
                player.setPlayerState(Player.PlayerState.WAITING);
                if (this.task != null) {
                    this.task.cancel();
                    this.task = null;
                }
                this.syncOtherPlayer(player);
                return true;
            } else return player.getPlayerState() == Player.PlayerState.WAITING;
        return false;
    }

    @Override
    public GameTileState getGameTileState() {
        if (this.gameManager == null || this.getGameState() != GameState.PLAYING)
            return null;
        return this.gameManager.getGameTileState();
    }

    @Override
    public void doTileAction(TileAction tileAction, Player player, Object... objects) {
        if (this.gameManager == null || this.getGameState() != GameState.PLAYING)
            return;
        this.gameManager.doTileAction(tileAction, this.startPlayers.indexOf(player.getId()), objects);
    }

    private synchronized void countdown() {
        if (this.getGameState() != GameState.WAITING)
            return;
        if (this.getStartTime() == 0) {
            this.task.cancel();
            this.task = null;
            this.start();
            return;
        }
        this.countdownStartTime();
        this.syncPlayer();
    }

    public synchronized void start() {
        if (this.getGameState() != GameState.WAITING)
            return;
        if (!this.players.stream().allMatch(player -> player.getPlayerState() == Player.PlayerState.READY))
            return;
        this.setGameState(GameState.PLAYING);
        this.players.forEach(player -> player.setPlayerState(Player.PlayerState.PLAYING));
        this.startPlayers.clear();
        for (Player player : this.players)
            this.startPlayers.add(player.getId());
        this.gameManager = this.getRule().getGameManager(this, this.players.size());
        this.setCountdown(this.gameManager.getCountdown());
        this.syncPlayer();
        this.task = FOCESS_SCHEDULER.runTimer(this::tick, Duration.ZERO, Duration.ofSeconds(1));
    }

    public synchronized void tick() {
        if (this.getGameState() != GameState.PLAYING)
            return;
        this.tickGameTime();
        gameManager.tick();
        this.setCountdown(gameManager.getCountdown());
        this.syncPlayer();
        if (this.gameManager.getGameTileState() == GameTileState.DISCARDING) {
            Player player = this.getPlayer(this.gameManager.getCurrent());
            if (player != null)
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
        if (this.getGameState() != GameState.PLAYING)
            return;
        this.setGameState(GameState.WAITING);
        this.players.forEach(player -> player.setPlayerState(Player.PlayerState.WAITING));
        this.syncPlayer();
        this.startPlayers.clear();
    }

    public GameData getPartGameData(Player player) {
        return this.getPartGameData(this.startPlayers.indexOf(player.getId()));
    }

    public void sendPacket(Packet packet) {
        this.players.forEach(player -> {
            if (player instanceof  RemotePlayer) {
                int clientId = ((RemotePlayer) player).getClientId();
                if (clientId == -1)
                    throw new IllegalStateException("Remote player " + player.getName() + " has no client id");
                this.serverSocket.getReceiver().sendPacket(clientId, packet);
            }
        });
    }

    public Player getPlayer(int index) {
        UUID id = this.startPlayers.get(index);
        for (Player player : this.players)
            if (player.getId().equals(id))
                return player;
        return null;
    }

    public enum TileAction {
        CHANGE_3_TILES

    }

}
