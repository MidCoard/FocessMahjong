package top.focess.mahjong.game.data;

import top.focess.mahjong.game.Player;

import java.util.UUID;

public class PlayerData {

    private final UUID id;

    private final Player.PlayerState playerState;

    public PlayerData(UUID id, Player.PlayerState playerState) {
        this.id = id;
        this.playerState = playerState;
    }

    public UUID getId() {
        return id;
    }

    public Player.PlayerState getPlayerState() {
        return playerState;
    }
}
