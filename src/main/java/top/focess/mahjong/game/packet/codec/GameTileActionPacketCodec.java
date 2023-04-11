package top.focess.mahjong.game.packet.codec;

import org.checkerframework.checker.nullness.qual.Nullable;
import top.focess.mahjong.game.packet.GameTileActionPacket;
import top.focess.mahjong.game.tile.TileState;
import top.focess.net.PacketPreCodec;
import top.focess.net.packet.PacketCodec;

import java.util.UUID;

public class GameTileActionPacketCodec extends PacketCodec<GameTileActionPacket> {
	@Override
	public @Nullable GameTileActionPacket readPacket(final PacketPreCodec packetPreCodec) {
		final UUID playerId = UUID.fromString(packetPreCodec.readString());
		final UUID gameId = UUID.fromString(packetPreCodec.readString());
		final GameTileActionPacket.TileAction tileAction = GameTileActionPacket.TileAction.values()[packetPreCodec.readInt()];
		final int length = packetPreCodec.readInt();
		final TileState[] tileStates = new TileState[length];
		for (int i = 0; i < length; i++)
			tileStates[i] = TileState.values()[packetPreCodec.readInt()];
		return new GameTileActionPacket(playerId, gameId, tileAction, tileStates);
	}

	@Override
	public void writePacket(final GameTileActionPacket packet, final PacketPreCodec packetPreCodec) {
		packetPreCodec.writeString(packet.getPlayerId().toString());
		packetPreCodec.writeString(packet.getGameId().toString());
		packetPreCodec.writeInt(packet.getTileAction().ordinal());
		packetPreCodec.writeInt(packet.getTileStates().length);
		for (final TileState tileState : packet.getTileStates())
			packetPreCodec.writeInt(tileState.ordinal());
	}
}
