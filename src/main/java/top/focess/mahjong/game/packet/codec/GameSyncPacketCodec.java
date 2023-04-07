package top.focess.mahjong.game.packet.codec;

import org.checkerframework.checker.nullness.qual.Nullable;
import top.focess.mahjong.game.data.GameData;
import top.focess.mahjong.game.packet.GameSyncPacket;
import top.focess.mahjong.game.packet.PacketUtil;
import top.focess.net.PacketPreCodec;
import top.focess.net.packet.PacketCodec;

public class GameSyncPacketCodec extends PacketCodec<GameSyncPacket> {
    @Override
    public @Nullable GameSyncPacket readPacket(PacketPreCodec packetPreCodec) {
        GameData gameData = PacketUtil.readGameData(packetPreCodec);
        return new GameSyncPacket(gameData);
    }

    @Override
    public void writePacket(GameSyncPacket packet, PacketPreCodec packetPreCodec) {
        PacketUtil.writeGameData(packetPreCodec, packet.getGameData());
    }
}
