package top.focess.mahjong.game.packet;

import top.focess.mahjong.game.packet.codec.JoinGamePacketCodec;
import top.focess.net.PacketPreCodec;
import top.focess.net.packet.ClientPacket;
import top.focess.net.packet.Packet;

import java.util.UUID;

public class JoinGamePacket extends Packet {

    public static final int PACKET_ID = 100;


    static {
        PacketPreCodec.register(JoinGamePacket.PACKET_ID, new JoinGamePacketCodec());
    }

    private final UUID playerId;
    private final UUID gameId;

    public JoinGamePacket(UUID playerId, UUID gameId) {
        this.playerId = playerId;
        this.gameId = gameId;
    }

    @Override
    public int getId() {
        return PACKET_ID;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public UUID getGameId() {
        return gameId;
    }
}
