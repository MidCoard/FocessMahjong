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

	public UUID getGameId() {
		return this.gameId;
	}

	@Override
	public int getId() {
		return GameTileActionPacket.PACKET_ID;
	}

	public UUID getPlayerId() {
		return this.playerId;
	}

	public TileAction getTileAction() {
		return this.tileAction;
	}

	public TileState[] getTileStates() {
		return this.tileStates;
	}

	public enum TileAction {
		PUNG("pung", 2), KONG("kong", 1), DISCARD_TILE("discard",-1), HU("hu",0), CHANGE_3_TILES("change",-1);

		private final String name;
		private final int priority;

		TileAction(final String name, final int priority) {
			this.name = name;
			this.priority = priority;
		}

		public String getName() {
			return this.name;
		}

		public int getPriority() {
			return this.priority;
		}
	}
}
