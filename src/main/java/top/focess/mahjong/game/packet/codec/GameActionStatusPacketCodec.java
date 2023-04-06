package top.focess.mahjong.game.packet.codec;

import org.checkerframework.checker.nullness.qual.Nullable;
import top.focess.mahjong.game.packet.GameActionStatusPacket;
import top.focess.net.PacketPreCodec;
import top.focess.net.packet.PacketCodec;

import java.util.UUID;

public class GameActionStatusPacketCodec extends PacketCodec<GameActionStatusPacket> {
    @Override
    public @Nullable GameActionStatusPacket readPacket(PacketPreCodec packetPreCodec) {
        UUID playerId = UUID.fromString(packetPreCodec.readString());
        UUID gameId = UUID.fromString(packetPreCodec.readString());
        GameActionStatusPacket.GameAction gameAction = GameActionStatusPacket.GameAction.valueOf(packetPreCodec.readString());
        GameActionStatusPacket.GameActionStatus gameActionStatus = GameActionStatusPacket.GameActionStatus.valueOf(packetPreCodec.readString());
        return new GameActionStatusPacket(playerId, gameId, gameAction, gameActionStatus);
    }

    @Override
    public void writePacket(GameActionStatusPacket packet, PacketPreCodec packetPreCodec) {
        packetPreCodec.writeString(packet.getPlayerId().toString());
        packetPreCodec.writeString(packet.getGameId().toString());
        packetPreCodec.writeString(packet.getGameAction().name());
        packetPreCodec.writeString(packet.getGameActionStatus().name());
    }
}
