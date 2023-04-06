package top.focess.mahjong.game.packet;

import top.focess.mahjong.game.data.GameData;
import top.focess.mahjong.game.packet.codec.GamesPacketCodec;
import top.focess.net.PacketPreCodec;
import top.focess.net.packet.Packet;

import java.util.List;

public class GamesPacket extends Packet {

    public static final int PACKET_ID = 121;

    static {
        PacketPreCodec.register(PACKET_ID, new GamesPacketCodec());
    }
    private final List<GameData> games;

    public GamesPacket(List<GameData> games) {
        this.games = games;
    }

    @Override
    public int getId() {
        return PACKET_ID;
    }

    public List<GameData> getGames() {
        return games;
    }
}
