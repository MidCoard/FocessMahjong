package top.focess.mahjong.game.packet;

import top.focess.mahjong.game.tile.TileState;
import top.focess.net.packet.Packet;

import java.util.UUID;

public class FetchTilePacket extends Packet {

	public static final int PACKET_ID = 132;
	private final UUID playerId;
	private final UUID gameId;
	private final TileState tileState;

	public FetchTilePacket(final UUID playerId, final UUID gameId, final TileState tileState) {
		this.playerId = playerId;
		this.gameId = gameId;
		this.tileState = tileState;
	}

	public UUID getGameId() {
		return this.gameId;
	}

	@Override
	public int getId() {
		return FetchTilePacket.PACKET_ID;
	}

	public UUID getPlayerId() {
		return this.playerId;
	}

	public TileState getTileState() {
		return this.tileState;
	}
}
