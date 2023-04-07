package top.focess.mahjong.game.packet.codec;

import top.focess.mahjong.game.packet.GameActionPacket;
import top.focess.net.PacketPreCodec;
import top.focess.net.packet.PacketCodec;

import java.util.UUID;

public class GameActionPacketCodec extends PacketCodec<GameActionPacket> {
    @Override
    public GameActionPacket readPacket(PacketPreCodec packetPreCodec) {
        UUID playerId = UUID.fromString(packetPreCodec.readString());
        UUID gameId = UUID.fromString(packetPreCodec.readString());
        GameActionPacket.GameAction gameAction = GameActionPacket.GameAction.valueOf(packetPreCodec.readString());
        return new GameActionPacket(playerId, gameId, gameAction);
    }

    @Override
    public void writePacket(GameActionPacket packet, PacketPreCodec packetPreCodec) {
        packetPreCodec.writeString(packet.getPlayerId().toString());
        packetPreCodec.writeString(packet.getGameId().toString());
        packetPreCodec.writeString(packet.getGameAction().name());
    }
}
