package top.focess.mahjong.game.packet.codec;

import org.checkerframework.checker.nullness.qual.Nullable;
import top.focess.mahjong.game.packet.GameStartPacket;
import top.focess.net.PacketPreCodec;
import top.focess.net.packet.PacketCodec;

import java.util.UUID;

public class GameStartPacketCodec extends PacketCodec<GameStartPacket> {
    @Override
    public @Nullable GameStartPacket readPacket(PacketPreCodec packetPreCodec) {
        UUID gameId = UUID.fromString(packetPreCodec.readString());
        return new GameStartPacket(gameId);
    }

    @Override
    public void writePacket(GameStartPacket packet, PacketPreCodec packetPreCodec) {
        packetPreCodec.writeString(packet.getGameId().toString());
    }
}
