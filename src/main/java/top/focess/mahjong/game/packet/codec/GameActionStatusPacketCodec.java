package top.focess.mahjong.game.packet.codec;

import org.checkerframework.checker.nullness.qual.Nullable;
import top.focess.mahjong.game.packet.GameActionStatusPacket;
import top.focess.net.PacketPreCodec;
import top.focess.net.packet.PacketCodec;

public class GameActionStatusPacketCodec extends PacketCodec<GameActionStatusPacket> {
    @Override
    public @Nullable GameActionStatusPacket readPacket(PacketPreCodec packetPreCodec) {
        return null;
    }

    @Override
    public void writePacket(GameActionStatusPacket packet, PacketPreCodec packetPreCodec) {

    }
}
