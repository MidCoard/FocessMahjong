package top.focess.mahjong.game;

import java.util.UUID;

public class LocalPlayer extends Player{

    public LocalPlayer() {
        this.playerState = PlayerState.WAITING;
    }
}
