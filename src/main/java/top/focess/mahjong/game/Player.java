package top.focess.mahjong.game;

import com.google.common.collect.Maps;
import top.focess.mahjong.game.data.PlayerData;
import top.focess.mahjong.terminal.TerminalLauncher;

import java.util.Map;
import java.util.UUID;

public class Player {

    private static final Map<UUID, Player> PLAYERS = Maps.newConcurrentMap();

    private final UUID id;

    private Game game;

    private PlayerState playerState = PlayerState.WAITING;

    public Player() {
        this(UUID.randomUUID());
    }

    public Player(UUID id) {
        this.id = id;
        PLAYERS.put(id, this);
    }

    public Game getGame() {
        return this.game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public boolean leave() {
        return this.getGame() != null && this.getGame().leave(this);
    }

    public boolean join(Game game) {
        return game.join(this);
    }

    public UUID getId() {
        return this.id;
    }

    public PlayerData getPlayerData() {
        return new PlayerData(this.id, playerState);
    }

    public PlayerState getPlayerState() {
        return playerState;
    }

    public void setPlayerState(PlayerState playerState) {
        if (this.playerState != playerState) {
            TerminalLauncher.change("playerState", this, this.playerState, playerState);
            this.playerState = playerState;
        }
    }

    public static Player getPlayer(UUID id) {
        return PLAYERS.get(id);
    }

    public enum PlayerState {

        WAITING,
        READY,
        PLAYING;
    }
}
