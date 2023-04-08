package top.focess.mahjong.game.packet.codec;

import org.checkerframework.checker.nullness.qual.Nullable;
import top.focess.mahjong.game.packet.Change3TilesPacket;
import top.focess.net.PacketPreCodec;
import top.focess.net.packet.PacketCodec;

import java.util.UUID;

public class Change3TilesPacketCodec extends PacketCodec<Change3TilesPacket> {
    @Override
    public @Nullable Change3TilesPacket readPacket(PacketPreCodec packetPreCodec) {
        UUID playerID = UUID.fromString(packetPreCodec.readString());
        UUID gameID = UUID.fromString(packetPreCodec.readString());
        int tile1 = packetPreCodec.readInt();
        int tile2 = packetPreCodec.readInt();
        int tile3 = packetPreCodec.readInt();
        return new Change3TilesPacket(playerID, gameID, tile1, tile2, tile3);
    }

    @Override
    public void writePacket(Change3TilesPacket packet, PacketPreCodec packetPreCodec) {
        packetPreCodec.writeString(packet.getPlayerId().toString());
        packetPreCodec.writeString(packet.getGameId().toString());
        packetPreCodec.writeInt(packet.getTile1());
        packetPreCodec.writeInt(packet.getTile2());
        packetPreCodec.writeInt(packet.getTile3());
    }
}
