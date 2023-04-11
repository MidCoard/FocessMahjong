package top.focess.mahjong.game.packet;

import top.focess.mahjong.game.tile.TileState;
import top.focess.net.packet.Packet;

import java.util.UUID;

public class GameTileActionPacket extends Packet {

    public static final int PACKET_ID = 130;
    private final UUID playerId;
    private final UUID gameId;
    private final TileAction tileAction;
    private final TileState[] tileStates;


    public GameTileActionPacket(UUID playerId, UUID gameId, TileAction tileAction, TileState... tileStates) {
        this.playerId = playerId;
        this.gameId = gameId;
        this.tileAction = tileAction;
        this.tileStates = tileStates;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public UUID getGameId() {
        return gameId;
    }

    public TileAction getTileAction() {
        return tileAction;
    }

    public TileState[] getTileStates() {
        return tileStates;
    }

    @Override
    public int getId() {
        return PACKET_ID;
    }

    public enum TileAction {
        KONG, DISCARD_TILE, HU, CHANGE_3_TILES

    }
}
