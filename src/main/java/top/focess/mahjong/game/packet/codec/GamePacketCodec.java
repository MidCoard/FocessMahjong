package top.focess.mahjong.game.packet.codec;

import org.checkerframework.checker.nullness.qual.Nullable;
import top.focess.mahjong.game.data.GameData;
import top.focess.mahjong.game.packet.GamePacket;
import top.focess.mahjong.game.packet.PacketUtil;
import top.focess.net.PacketPreCodec;
import top.focess.net.packet.PacketCodec;

public class GamePacketCodec extends PacketCodec<GamePacket> {
    @Override
    public @Nullable GamePacket readPacket(final PacketPreCodec packetPreCodec) {
        final GameData gameData = PacketUtil.readGameData(packetPreCodec);
        return new GamePacket(gameData);
    }

    @Override
    public void writePacket(final GamePacket packet, final PacketPreCodec packetPreCodec) {
        PacketUtil.writeGameData(packetPreCodec, packet.getGameData());
    }
}
