package top.focess.mahjong.game.packet;

import top.focess.mahjong.game.packet.codec.ListGamesPacketCodec;
import top.focess.net.PacketPreCodec;
import top.focess.net.packet.Packet;

public class ListGamesPacket extends Packet {

    public static final int PACKET_ID = 120;

    static {
        PacketPreCodec.register(PACKET_ID, new ListGamesPacketCodec());
    }
    @Override
    public int getId() {
        return PACKET_ID;
    }
}
