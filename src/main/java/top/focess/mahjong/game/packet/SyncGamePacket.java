package top.focess.mahjong.game.packet;

import top.focess.net.packet.Packet;

import java.util.UUID;

public class SyncGamePacket extends Packet {

    public static final int PACKET_ID = 122;
    private final UUID gameId;
    private final UUID playerId;

    public SyncGamePacket(final UUID playerId, final UUID gameId) {
        this.playerId = playerId;
        this.gameId = gameId;
    }

    @Override
    public int getId() {
        return SyncGamePacket.PACKET_ID;
    }

    public UUID getGameId() {
        return this.gameId;
    }

    public UUID getPlayerId() {
        return this.playerId;
    }
}
