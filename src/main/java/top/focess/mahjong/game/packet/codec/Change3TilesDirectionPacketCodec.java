package top.focess.mahjong.game.packet.codec;

import org.checkerframework.checker.nullness.qual.Nullable;
import top.focess.mahjong.game.packet.Change3TilesDirectionPacket;
import top.focess.net.PacketPreCodec;
import top.focess.net.packet.PacketCodec;

import java.util.UUID;

public class Change3TilesDirectionPacketCodec extends PacketCodec<Change3TilesDirectionPacket> {
    @Override
    public @Nullable Change3TilesDirectionPacket readPacket(final PacketPreCodec packetPreCodec) {
        final UUID gameId = UUID.fromString(packetPreCodec.readString());
        final int direction = packetPreCodec.readInt();
        return new Change3TilesDirectionPacket(gameId,direction);
    }

    @Override
    public void writePacket(final Change3TilesDirectionPacket packet, final PacketPreCodec packetPreCodec) {
        packetPreCodec.writeString(packet.getGameId().toString());
        packetPreCodec.writeInt(packet.getDirection());
    }
}
