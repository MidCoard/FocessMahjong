package top.focess.mahjong.game;

import com.google.common.collect.Maps;
import top.focess.mahjong.game.data.PlayerData;

import java.util.Map;
import java.util.UUID;

public class Player {

    private static final Map<UUID, Player> PLAYERS = Maps.newConcurrentMap();

    private final UUID id;

    private Game game;

    protected PlayerState playerState;

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
        this.playerState = playerState;
    }

    public static Player getPlayer(UUID id) {
        return PLAYERS.get(id);
    }

    public void update(PlayerData playerData) {
        if (!this.getId().equals(playerData.getId()))
            throw new IllegalArgumentException("The player id is not equal to the player data id.");
        this.playerState = playerData.getPlayerState();
    }

    public enum PlayerState {

        WAITING,
        READY,
        PLAYING;
    }
}
