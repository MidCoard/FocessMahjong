package top.focess.mahjong.game.data;

import top.focess.mahjong.game.PlayerState;

import java.util.UUID;

public class PlayerData {

    private final UUID id;

    private final PlayerState playerState;

    public PlayerData(UUID id, PlayerState playerState) {
        this.id = id;
        this.playerState = playerState;
    }

    public UUID getId() {
        return id;
    }

    public PlayerState getPlayerState() {
        return playerState;
    }
}
