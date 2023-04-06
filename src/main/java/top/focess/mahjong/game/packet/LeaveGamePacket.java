package top.focess.mahjong.game.packet;

import top.focess.mahjong.game.packet.codec.LeaveGamePacketCodec;
import top.focess.net.PacketPreCodec;
import top.focess.net.packet.Packet;

import java.util.UUID;

public class LeaveGamePacket extends Packet {

    public static final int PACKET_ID = 101;

    static {
        PacketPreCodec.register(PACKET_ID, new LeaveGamePacketCodec());
    }

    private final UUID playerId;
    private final UUID gameId;

    public LeaveGamePacket(UUID playerId, UUID gameId) {
        this.playerId = playerId;
        this.gameId = gameId;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public UUID getGameId() {
        return gameId;
    }

    @Override
    public int getId() {
        return PACKET_ID;
    }
}
