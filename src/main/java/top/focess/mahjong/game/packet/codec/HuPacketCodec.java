package top.focess.mahjong.game.packet.codec;

import org.checkerframework.checker.nullness.qual.Nullable;
import top.focess.mahjong.game.packet.HuPacket;
import top.focess.net.PacketPreCodec;
import top.focess.net.packet.PacketCodec;

import java.util.UUID;

public class HuPacketCodec extends PacketCodec<HuPacket> {
    @Override
    public @Nullable HuPacket readPacket(PacketPreCodec packetPreCodec) {
        UUID playerId = UUID.fromString(packetPreCodec.readString());
        UUID gameId = UUID.fromString(packetPreCodec.readString());
        return new HuPacket(playerId, gameId);
    }

    @Override
    public void writePacket(HuPacket packet, PacketPreCodec packetPreCodec) {
        packetPreCodec.writeString(packet.getPlayerId().toString());
        packetPreCodec.writeString(packet.getGameId().toString());
    }
}
