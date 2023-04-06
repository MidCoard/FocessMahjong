package top.focess.mahjong.game.data;

import java.util.UUID;

public class PlayerData {

    private final UUID id;

    public PlayerData(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }
}
