package top.focess.mahjong.game.packet.codec;

import org.checkerframework.checker.nullness.qual.Nullable;
import top.focess.mahjong.game.packet.GameActionPacket;
import top.focess.mahjong.game.packet.GameActionStatusPacket;
import top.focess.net.PacketPreCodec;
import top.focess.net.packet.PacketCodec;

import java.util.UUID;

public class GameActionStatusPacketCodec extends PacketCodec<GameActionStatusPacket> {
	@Override
	public @Nullable GameActionStatusPacket readPacket(final PacketPreCodec packetPreCodec) {
		final UUID playerId = UUID.fromString(packetPreCodec.readString());
		final UUID gameId = UUID.fromString(packetPreCodec.readString());
		final GameActionPacket.GameAction gameAction = GameActionPacket.GameAction.values()[packetPreCodec.readInt()];
		final GameActionStatusPacket.GameActionStatus gameActionStatus = GameActionStatusPacket.GameActionStatus.values()[packetPreCodec.readInt()];
		return new GameActionStatusPacket(playerId, gameId, gameAction, gameActionStatus);
	}

	@Override
	public void writePacket(final GameActionStatusPacket packet, final PacketPreCodec packetPreCodec) {
		packetPreCodec.writeString(packet.getPlayerId().toString());
		packetPreCodec.writeString(packet.getGameId().toString());
		packetPreCodec.writeInt(packet.getGameAction().ordinal());
		packetPreCodec.writeInt(packet.getGameActionStatus().ordinal());
	}
}
