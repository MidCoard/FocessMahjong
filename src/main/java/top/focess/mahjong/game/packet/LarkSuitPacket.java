package top.focess.mahjong.game.packet;

import top.focess.mahjong.game.tile.TileState;
import top.focess.net.packet.Packet;

import java.util.UUID;

public class LarkSuitPacket extends Packet {

    public static final int PACKET_ID = 134;
    private final UUID playerId;
    private final UUID gameId;
    private final TileState.TileStateCategory category;

    public LarkSuitPacket(UUID playerId, UUID gameId, TileState.TileStateCategory category) {
        this.playerId = playerId;
        this.gameId = gameId;
        this.category = category;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public UUID getGameId() {
        return gameId;
    }

    public TileState.TileStateCategory getCategory() {
        return category;
    }

    @Override
    public int getId() {
        return PACKET_ID;
    }
}
