package top.focess.mahjong.game;

import top.focess.mahjong.game.data.PlayerData;

import java.util.UUID;

public class Player {

    private final UUID id;

    private Game game;

    protected PlayerState playerState;

    public Player() {
        this(UUID.randomUUID());
    }

    public Player(UUID id) {
        this.id = id;
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
}
