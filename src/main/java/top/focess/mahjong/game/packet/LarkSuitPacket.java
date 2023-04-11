package top.focess.mahjong.game.packet;

import top.focess.mahjong.game.tile.TileState;
import top.focess.net.packet.Packet;

import java.util.UUID;

public class LarkSuitPacket extends Packet {

	public static final int PACKET_ID = 134;
	private final UUID playerId;
	private final UUID gameId;
	private final TileState.TileStateCategory category;

	public LarkSuitPacket(final UUID playerId, final UUID gameId, final TileState.TileStateCategory category) {
		this.playerId = playerId;
		this.gameId = gameId;
		this.category = category;
	}

	public TileState.TileStateCategory getCategory() {
		return this.category;
	}

	public UUID getGameId() {
		return this.gameId;
	}

	@Override
	public int getId() {
		return LarkSuitPacket.PACKET_ID;
	}

	public UUID getPlayerId() {
		return this.playerId;
	}
}
