package top.focess.mahjong.game.packet.codec;

import org.checkerframework.checker.nullness.qual.Nullable;
import top.focess.mahjong.game.packet.Change3TilesDirectionPacket;
import top.focess.net.PacketPreCodec;
import top.focess.net.packet.PacketCodec;

import java.util.UUID;

public class Change3TilesDirectionPacketCodec extends PacketCodec<Change3TilesDirectionPacket> {
    @Override
    public @Nullable Change3TilesDirectionPacket readPacket(PacketPreCodec packetPreCodec) {
        UUID gameId = UUID.fromString(packetPreCodec.readString());
        int direction = packetPreCodec.readInt();
        return new Change3TilesDirectionPacket(gameId,direction);
    }

    @Override
    public void writePacket(Change3TilesDirectionPacket packet, PacketPreCodec packetPreCodec) {
        packetPreCodec.writeString(packet.getGameId().toString());
        packetPreCodec.writeInt(packet.getDirection());
    }
}
