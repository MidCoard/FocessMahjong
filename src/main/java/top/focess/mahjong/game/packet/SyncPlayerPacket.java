package top.focess.mahjong.game.packet;

import top.focess.net.packet.Packet;

import java.util.UUID;

public class SyncPlayerPacket extends Packet {

    public static final int PACKET_ID = 124;
    private final UUID playerId;
    private final UUID gameId;

    public SyncPlayerPacket(UUID playerId, UUID gameId) {
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
