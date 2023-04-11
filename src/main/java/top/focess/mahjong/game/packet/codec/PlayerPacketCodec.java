package top.focess.mahjong.game.packet.codec;

import org.checkerframework.checker.nullness.qual.Nullable;
import top.focess.mahjong.game.data.PlayerData;
import top.focess.mahjong.game.packet.PacketUtil;
import top.focess.mahjong.game.packet.PlayerPacket;
import top.focess.net.PacketPreCodec;
import top.focess.net.packet.PacketCodec;

import java.util.UUID;

public class PlayerPacketCodec extends PacketCodec<PlayerPacket> {
	@Override
	public @Nullable PlayerPacket readPacket(final PacketPreCodec packetPreCodec) {
		final UUID gameId = UUID.fromString(packetPreCodec.readString());
		final PlayerData playerData = PacketUtil.readPlayerData(packetPreCodec);
		return new PlayerPacket(gameId, playerData);
	}

	@Override
	public void writePacket(final PlayerPacket packet, final PacketPreCodec packetPreCodec) {
		packetPreCodec.writeString(packet.getGameId().toString());
		PacketUtil.writePlayerData(packetPreCodec, packet.getPlayerData());
	}
}
