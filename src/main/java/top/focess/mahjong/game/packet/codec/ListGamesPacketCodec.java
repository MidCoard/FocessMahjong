package top.focess.mahjong.game.packet.codec;

import org.checkerframework.checker.nullness.qual.Nullable;
import top.focess.mahjong.game.packet.ListGamesPacket;
import top.focess.net.PacketPreCodec;
import top.focess.net.packet.PacketCodec;

public class ListGamesPacketCodec extends PacketCodec<ListGamesPacket> {
    @Override
    public @Nullable ListGamesPacket readPacket(PacketPreCodec packetPreCodec) {
        return new ListGamesPacket();
    }

    @Override
    public void writePacket(ListGamesPacket packet, PacketPreCodec packetPreCodec) {}
}
