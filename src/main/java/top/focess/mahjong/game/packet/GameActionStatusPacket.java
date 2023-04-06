package top.focess.mahjong.game.packet;

import top.focess.net.packet.Packet;

public class GameActionStatusPacket extends Packet {

    public static final int PACKET_ID = 102;

    @Override
    public int getId() {
        return PACKET_ID;
    }
}
