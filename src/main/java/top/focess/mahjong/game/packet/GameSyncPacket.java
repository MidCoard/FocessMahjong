package top.focess.mahjong.game.packet;

import top.focess.mahjong.game.data.GameData;
import top.focess.net.packet.Packet;

public class GameSyncPacket extends Packet {

	public static final int PACKET_ID = 102;

	private final GameData gameData;

	public GameSyncPacket(final GameData gameData) {
		this.gameData = gameData;
	}

	public GameData getGameData() {
		return this.gameData;
	}

	@Override
	public int getId() {
		return GameSyncPacket.PACKET_ID;
	}
}
