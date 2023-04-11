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


    public GameTileActionPacket(final UUID playerId, final UUID gameId, final TileAction tileAction, final TileState... tileStates) {
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

    public TileAction getTileAction() {
        return this.tileAction;
    }

    public TileState[] getTileStates() {
        return this.tileStates;
    }

    @Override
    public int getId() {
        return GameTileActionPacket.PACKET_ID;
    }

    public enum TileAction {
        PUNG(2),KONG(1), DISCARD_TILE(-1), HU(0), CHANGE_3_TILES(-1);

        private final int priority;

        TileAction(final int priority) {
            this.priority = priority;
        }

        public int getPriority() {
            return this.priority;
        }
    }
}
