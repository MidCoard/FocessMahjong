package top.focess.mahjong.game.packet;

import top.focess.net.packet.Packet;

import java.util.UUID;

public class GameActionPacket extends Packet {

	public static final int PACKET_ID = 100;

	private final UUID playerId;
	private final UUID gameId;
	private final GameAction gameAction;

	public GameActionPacket(final UUID playerId, final UUID gameId, final GameAction gameAction) {
		this.playerId = playerId;
		this.gameId = gameId;
		this.gameAction = gameAction;
	}

	public GameAction getGameAction() {
		return this.gameAction;
	}

	public UUID getGameId() {
		return this.gameId;
	}

	@Override
	public int getId() {
		return GameActionPacket.PACKET_ID;
	}

	public UUID getPlayerId() {
		return this.playerId;
	}

	public enum GameAction {
		JOIN("join"),
		LEAVE("leave"),
		READY("ready"),
		UNREADY("unready");

		private final String name;

		GameAction(final String name) {
			this.name = name;
		}

		public String getName() {
			return this.name;
		}
	}
}
