package top.focess.mahjong.game.packet;

import top.focess.mahjong.game.data.GameData;
import top.focess.mahjong.game.packet.codec.GamePacketCodec;
import top.focess.net.PacketPreCodec;
import top.focess.net.packet.Packet;

public class GamePacket extends Packet {

    public static final int PACKET_ID = 123;

    private final GameData gameData;

    public GamePacket(final GameData gameData) {
        this.gameData = gameData;
    }

    public GameData getGameData() {
        return this.gameData;
    }

    @Override
    public int getId() {
        return GamePacket.PACKET_ID;
    }
}
