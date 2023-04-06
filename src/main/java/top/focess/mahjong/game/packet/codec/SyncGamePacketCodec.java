package top.focess.mahjong.game.packet.codec;

import org.checkerframework.checker.nullness.qual.Nullable;
import top.focess.mahjong.game.packet.SyncGamePacket;
import top.focess.net.PacketPreCodec;
import top.focess.net.packet.PacketCodec;

import java.util.UUID;

public class SyncGamePacketCodec extends PacketCodec<SyncGamePacket> {
    @Override
    public @Nullable SyncGamePacket readPacket(PacketPreCodec packetPreCodec) {
        UUID gameId = UUID.fromString(packetPreCodec.readString());
        return new SyncGamePacket(gameId);
    }

    @Override
    public void writePacket(SyncGamePacket packet, PacketPreCodec packetPreCodec) {
        packetPreCodec.writeString(packet.getGameId().toString());
    }
}
