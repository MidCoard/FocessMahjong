package top.focess.mahjong.game.packet;

import top.focess.mahjong.game.data.GameData;
import top.focess.net.packet.Packet;

public class GameSyncPacket extends Packet {

    public static final int PACKET_ID = 102;

    private final GameData gameData;

    public GameSyncPacket(GameData gameData) {
        this.gameData = gameData;
    }

    @Override
    public int getId() {
        return PACKET_ID;
    }

    public GameData getGameData() {
        return gameData;
    }
}
