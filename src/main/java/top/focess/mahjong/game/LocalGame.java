package top.focess.mahjong.game;

import com.google.common.collect.Lists;
import top.focess.mahjong.game.packet.GameSyncPacket;
import top.focess.mahjong.game.remote.RemotePlayer;
import top.focess.mahjong.game.rule.MahjongRule;
import top.focess.mahjong.terminal.TerminalLauncher;
import top.focess.net.socket.FocessMultiSocket;
import top.focess.scheduler.FocessScheduler;
import top.focess.scheduler.Task;

import java.time.Duration;
import java.util.List;

public class LocalGame extends Game {

    private static final FocessScheduler FOCESS_SCHEDULER = new FocessScheduler("GameTicker", true);
    private final FocessMultiSocket serverSocket;
    private Task task;

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
        List<Player> old = Lists.newArrayList(this.players);
        TerminalLauncher.change("players", this, old, this.players);
        this.players.add(player);
        player.setGame(this);
        this.syncOtherPlayer(player);
        return true;
    }

    private void syncOtherPlayer(Player player) {
        this.players.stream().filter(p -> p != player).forEach(p -> {
            if (p instanceof RemotePlayer) {
                int clientId = ((RemotePlayer) p).getClientId();
                if (clientId == -1) {
                    System.out.println("Player " + p.getId() + " is not connected to server");
                }
                this.serverSocket.getReceiver().sendPacket(clientId, new GameSyncPacket(this.getGameData()));
            }
        });
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

    private synchronized void countdown() {
        if (this.getGameState() != GameState.WAITING)
            return;
        if (this.getStartTime() == 0) {
            this.task.cancel();
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
        this.syncPlayer();
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
    }

}
