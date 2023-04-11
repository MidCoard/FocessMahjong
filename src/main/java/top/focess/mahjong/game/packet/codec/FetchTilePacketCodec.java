package top.focess.mahjong.game.packet.codec;

import org.checkerframework.checker.nullness.qual.Nullable;
import top.focess.mahjong.game.packet.FetchTilePacket;
import top.focess.mahjong.game.tile.TileState;
import top.focess.net.PacketPreCodec;
import top.focess.net.packet.PacketCodec;

import java.util.UUID;

public class FetchTilePacketCodec extends PacketCodec<FetchTilePacket> {
    @Override
    public @Nullable FetchTilePacket readPacket(final PacketPreCodec packetPreCodec) {
        final UUID playerId = UUID.fromString(packetPreCodec.readString());
        final UUID gameId = UUID.fromString(packetPreCodec.readString());
        final TileState tileState = TileState.values()[packetPreCodec.readInt()];
        return new FetchTilePacket(playerId, gameId, tileState);
    }

    @Override
    public void writePacket(final FetchTilePacket packet, final PacketPreCodec packetPreCodec) {
        packetPreCodec.writeString(packet.getPlayerId().toString());
        packetPreCodec.writeString(packet.getGameId().toString());
        packetPreCodec.writeInt(packet.getTileState().ordinal());
    }
}
