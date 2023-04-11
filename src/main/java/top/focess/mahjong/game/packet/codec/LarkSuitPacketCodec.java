package top.focess.mahjong.game.packet.codec;

import org.checkerframework.checker.nullness.qual.Nullable;
import top.focess.mahjong.game.packet.LarkSuitPacket;
import top.focess.mahjong.game.tile.TileState;
import top.focess.net.PacketPreCodec;
import top.focess.net.packet.PacketCodec;

import java.util.UUID;

public class LarkSuitPacketCodec extends PacketCodec<LarkSuitPacket> {
    @Override
    public @Nullable LarkSuitPacket readPacket(PacketPreCodec packetPreCodec) {
        UUID playerId = UUID.fromString(packetPreCodec.readString());
        UUID gameId = UUID.fromString(packetPreCodec.readString());
        TileState.TileStateCategory category = TileState.TileStateCategory.values()[packetPreCodec.readInt()];
        return new LarkSuitPacket(playerId, gameId, category);
    }

    @Override
    public void writePacket(LarkSuitPacket packet, PacketPreCodec packetPreCodec) {
        packetPreCodec.writeString(packet.getPlayerId().toString());
        packetPreCodec.writeString(packet.getGameId().toString());
        packetPreCodec.writeInt(packet.getCategory().ordinal());
    }
}
