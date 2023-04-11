package top.focess.mahjong.game.packet.codec;

import org.checkerframework.checker.nullness.qual.Nullable;
import top.focess.mahjong.game.packet.SyncPlayerPacket;
import top.focess.net.PacketPreCodec;
import top.focess.net.packet.PacketCodec;

import java.util.UUID;

public class SyncPlayerPacketCodec extends PacketCodec<SyncPlayerPacket> {
	@Override
	public @Nullable SyncPlayerPacket readPacket(final PacketPreCodec packetPreCodec) {
		final UUID playerId = UUID.fromString(packetPreCodec.readString());
		final UUID gameId = UUID.fromString(packetPreCodec.readString());
		return new SyncPlayerPacket(playerId, gameId);
	}

	@Override
	public void writePacket(final SyncPlayerPacket packet, final PacketPreCodec packetPreCodec) {
		packetPreCodec.writeString(packet.getPlayerId().toString());
		packetPreCodec.writeString(packet.getGameId().toString());
	}
}
