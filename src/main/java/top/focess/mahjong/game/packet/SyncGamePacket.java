package top.focess.mahjong.game.packet;

import top.focess.mahjong.game.packet.codec.SyncGamePacketCodec;
import top.focess.net.PacketPreCodec;
import top.focess.net.packet.Packet;

import java.util.UUID;

public class SyncGamePacket extends Packet {

    public static final int PACKET_ID = 122;

    static {
        PacketPreCodec.register(PACKET_ID, new SyncGamePacketCodec());
    }

    private final UUID gameId;

    public SyncGamePacket(UUID gameId) {
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
