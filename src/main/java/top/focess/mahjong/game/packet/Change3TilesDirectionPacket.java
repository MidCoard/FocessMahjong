package top.focess.mahjong.game.packet;

import top.focess.net.packet.Packet;

import java.util.UUID;

public class Change3TilesDirectionPacket extends Packet {

	public static final int PACKET_ID = 131;
	private final UUID gameId;
	private final int direction;

	public Change3TilesDirectionPacket(final UUID gameId, final int direction) {
		this.gameId = gameId;
		this.direction = direction;
	}

	public int getDirection() {
		return this.direction;
	}

	public UUID getGameId() {
		return this.gameId;
	}

	@Override
	public int getId() {
		return 0;
	}
}
