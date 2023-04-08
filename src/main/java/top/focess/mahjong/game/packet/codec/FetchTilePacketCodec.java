package top.focess.mahjong.game.packet.codec;

import org.checkerframework.checker.nullness.qual.Nullable;
import top.focess.mahjong.game.packet.FetchTilePacket;
import top.focess.mahjong.game.tile.TileState;
import top.focess.net.PacketPreCodec;
import top.focess.net.packet.PacketCodec;

import java.util.UUID;

public class FetchTilePacketCodec extends PacketCodec<FetchTilePacket> {
    @Override
    public @Nullable FetchTilePacket readPacket(PacketPreCodec packetPreCodec) {
        UUID playerId = UUID.fromString(packetPreCodec.readString());
        UUID gameId = UUID.fromString(packetPreCodec.readString());
        TileState tileState = TileState.valueOf(packetPreCodec.readString());
        return new FetchTilePacket(playerId,gameId,tileState);
    }

    @Override
    public void writePacket(FetchTilePacket packet, PacketPreCodec packetPreCodec) {
        packetPreCodec.writeString(packet.getPlayerId().toString());
        packetPreCodec.writeString(packet.getGameId().toString());
        packetPreCodec.writeString(packet.getTileState().name());
    }
}
