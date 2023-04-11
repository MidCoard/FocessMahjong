package top.focess.mahjong.game.packet;

import top.focess.mahjong.game.tile.TileState;
import top.focess.net.packet.Packet;

import java.util.UUID;

public class GameTileActionNoticePacket extends Packet {

    public static final int PACKET_ID = 133;
    private final UUID playerId;
    private final UUID gameId;
    private final GameTileActionPacket.TileAction tileAction;
    private final TileState[] tileStates;

    public GameTileActionNoticePacket(final UUID playerId, final UUID gameId, final GameTileActionPacket.TileAction tileAction, final TileState... tileStates) {
        this.playerId = playerId;
        this.gameId = gameId;
        this.tileAction = tileAction;
        this.tileStates = tileStates;
    }

    public UUID getPlayerId() {
        return this.playerId;
    }

    public UUID getGameId() {
        return this.gameId;
    }

    public GameTileActionPacket.TileAction getTileAction() {
        return this.tileAction;
    }

    public TileState[] getTileStates() {
        return this.tileStates;
    }

    @Override
    public int getId() {
        return GameTileActionNoticePacket.PACKET_ID;
    }
}
