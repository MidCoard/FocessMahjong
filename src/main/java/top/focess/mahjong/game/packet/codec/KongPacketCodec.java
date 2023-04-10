package top.focess.mahjong.game.packet.codec;

import org.checkerframework.checker.nullness.qual.Nullable;
import top.focess.mahjong.game.packet.KongPacket;
import top.focess.mahjong.game.tile.TileState;
import top.focess.net.PacketPreCodec;
import top.focess.net.packet.PacketCodec;

import java.util.UUID;

public class KongPacketCodec extends PacketCodec<KongPacket> {
    @Override
    public @Nullable KongPacket readPacket(PacketPreCodec packetPreCodec) {
        UUID playerId = UUID.fromString(packetPreCodec.readString());
        UUID gameId = UUID.fromString(packetPreCodec.readString());
        TileState tileState = TileState.valueOf(packetPreCodec.readString());
        return new KongPacket(playerId, gameId , tileState);
    }

    @Override
    public void writePacket(KongPacket packet, PacketPreCodec packetPreCodec) {
        packetPreCodec.writeString(packet.getPlayerId().toString());
        packetPreCodec.writeString(packet.getGameId().toString());
        packetPreCodec.writeString(packet.getTileState().name());
    }
}
