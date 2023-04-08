package top.focess.mahjong.game.packet;

import top.focess.mahjong.game.tile.TileState;
import top.focess.net.packet.Packet;

import java.util.UUID;

public class FetchTilePacket extends Packet {

    public static final int PACKET_ID = 132;
    private final UUID playerId;
    private final UUID gameId;
    private final TileState tileState;

    public FetchTilePacket(UUID playerId, UUID gameId, TileState tileState) {
        this.playerId = playerId;
        this.gameId = gameId;
        this.tileState = tileState;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public UUID getGameId() {
        return gameId;
    }

    public TileState getTileState() {
        return tileState;
    }

    @Override
    public int getId() {
        return PACKET_ID;
    }
}
