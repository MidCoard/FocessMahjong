package top.focess.mahjong.game.packet.codec;

import org.checkerframework.checker.nullness.qual.Nullable;
import top.focess.mahjong.game.packet.SyncGamePacket;
import top.focess.net.PacketPreCodec;
import top.focess.net.packet.PacketCodec;

import java.util.UUID;

public class SyncGamePacketCodec extends PacketCodec<SyncGamePacket> {

    @Override
    public @Nullable SyncGamePacket readPacket(final PacketPreCodec packetPreCodec) {
        final UUID playerId = UUID.fromString(packetPreCodec.readString());
        final UUID gameId = UUID.fromString(packetPreCodec.readString());
        return new SyncGamePacket(playerId, gameId);
    }

    @Override
    public void writePacket(final SyncGamePacket packet, final PacketPreCodec packetPreCodec) {
        packetPreCodec.writeString(packet.getPlayerId().toString());
        packetPreCodec.writeString(packet.getGameId().toString());
    }
}
