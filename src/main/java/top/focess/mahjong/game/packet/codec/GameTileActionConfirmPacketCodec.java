package top.focess.mahjong.game.packet.codec;

import org.checkerframework.checker.nullness.qual.Nullable;
import top.focess.mahjong.game.packet.GameTileActionConfirmPacket;
import top.focess.mahjong.game.packet.GameTileActionPacket;
import top.focess.mahjong.game.tile.TileState;
import top.focess.net.PacketPreCodec;
import top.focess.net.packet.PacketCodec;

import java.util.UUID;

public class GameTileActionConfirmPacketCodec extends PacketCodec<GameTileActionConfirmPacket> {
    @Override
    public @Nullable GameTileActionConfirmPacket readPacket(PacketPreCodec packetPreCodec) {
        UUID playerId = UUID.fromString(packetPreCodec.readString());
        UUID gameId = UUID.fromString(packetPreCodec.readString());
        GameTileActionPacket.TileAction tileAction = GameTileActionPacket.TileAction.values()[packetPreCodec.readInt()];
        int length = packetPreCodec.readInt();
        TileState[] tileStates = new TileState[length];
        for (int i = 0; i < length; i++)
            tileStates[i] = TileState.values()[packetPreCodec.readInt()];
        return new GameTileActionConfirmPacket(playerId, gameId, tileAction, tileStates);
    }

    @Override
    public void writePacket(GameTileActionConfirmPacket packet, PacketPreCodec packetPreCodec) {
        packetPreCodec.writeString(packet.getPlayerId().toString());
        packetPreCodec.writeString(packet.getGameId().toString());
        packetPreCodec.writeInt(packet.getTileAction().ordinal());
        packetPreCodec.writeInt(packet.getTileStates().length);
        for (TileState tileState : packet.getTileStates())
            packetPreCodec.writeInt(tileState.ordinal());
    }
}
