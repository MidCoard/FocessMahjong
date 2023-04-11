package top.focess.mahjong.game.packet.codec;

import top.focess.mahjong.game.packet.GameActionPacket;
import top.focess.net.PacketPreCodec;
import top.focess.net.packet.PacketCodec;

import java.util.UUID;

public class GameActionPacketCodec extends PacketCodec<GameActionPacket> {
	@Override
	public GameActionPacket readPacket(final PacketPreCodec packetPreCodec) {
		final UUID playerId = UUID.fromString(packetPreCodec.readString());
		final UUID gameId = UUID.fromString(packetPreCodec.readString());
		final GameActionPacket.GameAction gameAction = GameActionPacket.GameAction.values()[packetPreCodec.readInt()];
		return new GameActionPacket(playerId, gameId, gameAction);
	}

	@Override
	public void writePacket(final GameActionPacket packet, final PacketPreCodec packetPreCodec) {
		packetPreCodec.writeString(packet.getPlayerId().toString());
		packetPreCodec.writeString(packet.getGameId().toString());
		packetPreCodec.writeInt(packet.getGameAction().ordinal());
	}
}
