package top.focess.mahjong.game;

import java.util.UUID;

public class LocalPlayer extends Player{

    public static LocalPlayer localPlayer;

    public LocalPlayer(final String name) {
        super(UUID.randomUUID(), name);
    }
}
