package top.focess.mahjong.game.packet;

import top.focess.mahjong.game.tile.TileState;
import top.focess.net.packet.Packet;

import java.util.UUID;

public class KongPacket extends Packet {

    public static final int PACKET_ID = 133;
    private final UUID playerId;
    private final UUID gameId;
    private final TileState tileState;

    public KongPacket(UUID playerId, UUID gameId, TileState tileState) {
        this.playerId = playerId;
        this.gameId = gameId;
        this.tileState = tileState;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public TileState getTileState() {
        return tileState;
    }

    @Override
    public int getId() {
        return PACKET_ID;
    }

    public UUID getGameId() {
        return gameId;
    }
}
