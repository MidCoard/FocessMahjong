package top.focess.mahjong.game.packet;

import top.focess.net.packet.Packet;

import java.util.UUID;

public class GameActionStatusPacket extends Packet {

	public static final int PACKET_ID = 101;
	private final UUID playerId;
	private final UUID gameId;
	private final GameActionPacket.GameAction gameAction;
	private final GameActionStatus gameActionStatus;

	public GameActionStatusPacket(final UUID playerId, final UUID gameId, final GameActionPacket.GameAction gameAction, final GameActionStatus gameActionStatus) {
		this.playerId = playerId;
		this.gameId = gameId;
		this.gameAction = gameAction;
		this.gameActionStatus = gameActionStatus;
	}

	public GameActionPacket.GameAction getGameAction() {
		return this.gameAction;
	}

	public GameActionStatus getGameActionStatus() {
		return this.gameActionStatus;
	}

	public UUID getGameId() {
		return this.gameId;
	}

	@Override
	public int getId() {
		return GameActionStatusPacket.PACKET_ID;
	}

	public UUID getPlayerId() {
		return this.playerId;
	}

	public enum GameActionStatus {
		SUCCESS,
		FAILURE,
		UNKNOWN
	}
}
