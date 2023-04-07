package top.focess.mahjong.game;

import top.focess.mahjong.game.packet.GameSyncPacket;
import top.focess.mahjong.game.remote.RemotePlayer;
import top.focess.mahjong.game.rule.MahjongRule;
import top.focess.net.socket.FocessMultiSocket;
import top.focess.scheduler.FocessScheduler;
import top.focess.scheduler.Task;

import java.time.Duration;

public class LocalGame extends Game {

    private static final FocessScheduler FOCESS_SCHEDULER = new FocessScheduler("GameTicker", true);
    private final FocessMultiSocket serverSocket;
    private int startTime;
    private Task task;

    public LocalGame(FocessMultiSocket serverSocket, MahjongRule rule) {
        super(rule);
        this.serverSocket = serverSocket;
        this.gameState = GameState.WAITING;
    }

    public synchronized boolean join(Player player) {
        if (this.getGameState() == GameState.NEW)
            return false;
        if (!this.rule.checkPlayerSize(players.size() + 1))
            return false;
        if (player.getGame() != null || player.getPlayerState() != Player.PlayerState.WAITING)
            return false;
        this.players.add(player);
        player.setGame(this);
        return true;
    }

    public synchronized boolean leave(Player player) {
        if (this.getGameState() == GameState.NEW)
            return false;
        if (player.getGame() != this || !this.players.remove(player))
            return false;
        player.setGame(null);
        player.setPlayerState(Player.PlayerState.WAITING);
        if (this.task != null) {
            this.task.cancel();
            this.task = null;
        }
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
                    this.startTime = this.rule.getReadyTime(this.players.size());
                    if (this.task != null)
                        this.task.cancel();
                    if (this.startTime != -1)
                        this.task = FOCESS_SCHEDULER.runTimer(this::countdown, Duration.ZERO, Duration.ofSeconds(1));
                }
                return true;
            }
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
                return true;
            }
        return false;
    }

    private synchronized void countdown() {
        if (this.getGameState() != GameState.WAITING)
            return;
        if (this.startTime == 0) {
            this.task.cancel();
            this.start();
            return;
        }
        this.startTime--;
    }

    public synchronized void start() {
        if (this.getGameState() != GameState.WAITING)
            return;
        if (!this.players.stream().allMatch(player -> player.getPlayerState() == Player.PlayerState.READY))
            return;
        this.gameState = GameState.PLAYING;
        this.players.forEach(player -> player.setPlayerState(Player.PlayerState.PLAYING));
        sync();
    }

    public synchronized void end() {
        if (this.getGameState() != GameState.PLAYING)
            return;
        this.gameState = GameState.WAITING;
        this.players.forEach(player -> player.setPlayerState(Player.PlayerState.WAITING));
        sync();
    }

    private void sync() {
        GameSyncPacket gameEndPacket = new GameSyncPacket(this.getGameData());
        for (Player player : this.players)
            if (player instanceof RemotePlayer) {
                int clientId = ((RemotePlayer) player).getClientId();
                if (clientId == -1)
                    throw new IllegalStateException("Client id is -1");
                serverSocket.getReceiver().sendPacket(((RemotePlayer) player).getClientId(), gameEndPacket);
            }
    }
}
