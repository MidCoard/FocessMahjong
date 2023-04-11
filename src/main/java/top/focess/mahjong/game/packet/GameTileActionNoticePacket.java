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

    public GameTileActionNoticePacket(UUID playerId, UUID gameId, GameTileActionPacket.TileAction tileAction, TileState... tileStates) {
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

    public GameTileActionPacket.TileAction getTileAction() {
        return tileAction;
    }

    public TileState[] getTileStates() {
        return tileStates;
    }

    @Override
    public int getId() {
        return PACKET_ID;
    }
}
