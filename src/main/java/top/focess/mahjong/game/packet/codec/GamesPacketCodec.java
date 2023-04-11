package top.focess.mahjong.game.packet.codec;

import com.google.common.collect.Lists;
import org.checkerframework.checker.nullness.qual.Nullable;
import top.focess.mahjong.game.data.GameData;
import top.focess.mahjong.game.packet.GamesPacket;
import top.focess.mahjong.game.packet.PacketUtil;
import top.focess.net.PacketPreCodec;
import top.focess.net.packet.PacketCodec;

import java.util.List;

public class GamesPacketCodec extends PacketCodec<GamesPacket> {
	@Override
	public @Nullable GamesPacket readPacket(final PacketPreCodec packetPreCodec) {
		final List<GameData> games = Lists.newArrayList();
		final int size = packetPreCodec.readInt();
		for (int i = 0; i < size; i++)
			games.add(PacketUtil.readGameData(packetPreCodec));
		return new GamesPacket(games);
	}

	@Override
	public void writePacket(final GamesPacket packet, final PacketPreCodec packetPreCodec) {
		packetPreCodec.writeInt(packet.getGames().size());
		for (final GameData gameData : packet.getGames())
			PacketUtil.writeGameData(packetPreCodec, gameData);
	}
}