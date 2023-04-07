package top.focess.mahjong.game.packet;

import top.focess.mahjong.game.packet.codec.GameStartPacketCodec;
import top.focess.net.PacketPreCodec;
import top.focess.net.packet.Packet;

import java.util.UUID;

public class GameStartPacket extends Packet {

    public static final int PACKET_ID = 102;

    static {
        PacketPreCodec.register(PACKET_ID, new GameStartPacketCodec());
    }

    private final UUID gameId;

    public GameStartPacket(UUID gameId) {
        this.gameId = gameId;
    }

    @Override
    public int getId() {
        return PACKET_ID;
    }

    public UUID getGameId() {
        return gameId;
    }
}
