package top.focess.mahjong.game.packet;

import top.focess.net.packet.Packet;

import java.util.UUID;

public class HuPacket extends Packet {

    public static final int PACKET_ID = 134;
    private final UUID playerId
            ;
    private final UUID gameId;

    public HuPacket(UUID playerId, UUID gameId) {
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
