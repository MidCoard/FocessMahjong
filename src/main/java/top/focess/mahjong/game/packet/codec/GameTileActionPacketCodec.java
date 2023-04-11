package top.focess.mahjong.game.packet.codec;

import org.checkerframework.checker.nullness.qual.Nullable;
import top.focess.mahjong.game.packet.GameTileActionPacket;
import top.focess.mahjong.game.tile.TileState;
import top.focess.net.PacketPreCodec;
import top.focess.net.packet.PacketCodec;

import java.util.UUID;

public class GameTileActionPacketCodec extends PacketCodec<GameTileActionPacket> {
    @Override
    public @Nullable GameTileActionPacket readPacket(PacketPreCodec packetPreCodec) {
        UUID playerId = UUID.fromString(packetPreCodec.readString());
        UUID gameId = UUID.fromString(packetPreCodec.readString());
        GameTileActionPacket.TileAction tileAction = GameTileActionPacket.TileAction.valueOf(packetPreCodec.readString());
        int length = packetPreCodec.readInt();
        TileState[] tileStates = new TileState[length];
        for (int i = 0; i < length; i++)
            tileStates[i] = TileState.valueOf(packetPreCodec.readString());
        return new GameTileActionPacket(playerId, gameId, tileAction, tileStates);
    }

    @Override
    public void writePacket(GameTileActionPacket packet, PacketPreCodec packetPreCodec) {
        packetPreCodec.writeString(packet.getPlayerId().toString());
        packetPreCodec.writeString(packet.getGameId().toString());
        packetPreCodec.writeString(packet.getTileAction().name());
        packetPreCodec.writeInt(packet.getTileStates().length);
        for (TileState tileState : packet.getTileStates())
            packetPreCodec.writeString(tileState.name());
    }
}
