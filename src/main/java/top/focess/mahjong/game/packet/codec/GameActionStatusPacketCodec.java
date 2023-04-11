package top.focess.mahjong.game.packet.codec;

import org.checkerframework.checker.nullness.qual.Nullable;
import top.focess.mahjong.game.packet.GameActionPacket;
import top.focess.mahjong.game.packet.GameActionStatusPacket;
import top.focess.net.PacketPreCodec;
import top.focess.net.packet.PacketCodec;

import java.util.UUID;

public class GameActionStatusPacketCodec extends PacketCodec<GameActionStatusPacket> {
    @Override
    public @Nullable GameActionStatusPacket readPacket(PacketPreCodec packetPreCodec) {
        UUID playerId = UUID.fromString(packetPreCodec.readString());
        UUID gameId = UUID.fromString(packetPreCodec.readString());
        GameActionPacket.GameAction gameAction = GameActionPacket.GameAction.values()[packetPreCodec.readInt()];
        GameActionStatusPacket.GameActionStatus gameActionStatus = GameActionStatusPacket.GameActionStatus.values()[packetPreCodec.readInt()];
        return new GameActionStatusPacket(playerId, gameId, gameAction, gameActionStatus);
    }

    @Override
    public void writePacket(GameActionStatusPacket packet, PacketPreCodec packetPreCodec) {
        packetPreCodec.writeString(packet.getPlayerId().toString());
        packetPreCodec.writeString(packet.getGameId().toString());
        packetPreCodec.writeInt(packet.getGameAction().ordinal());
        packetPreCodec.writeInt(packet.getGameActionStatus().ordinal());
    }
}
