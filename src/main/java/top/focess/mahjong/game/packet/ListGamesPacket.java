package top.focess.mahjong.game.packet;

import top.focess.net.packet.ClientPacket;
import top.focess.net.packet.Packet;

public class ListGamesPacket extends Packet {

    public static final int PACKET_ID = 120;
    @Override
    public int getId() {
        return PACKET_ID;
    }
}
