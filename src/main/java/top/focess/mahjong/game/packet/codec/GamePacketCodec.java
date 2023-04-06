package top.focess.mahjong.game.packet.codec;

import com.google.common.collect.Lists;
import org.checkerframework.checker.nullness.qual.Nullable;
import top.focess.mahjong.game.GameState;
import top.focess.mahjong.game.data.GameData;
import top.focess.mahjong.game.data.PlayerData;
import top.focess.mahjong.game.packet.GamePacket;
import top.focess.mahjong.game.rule.MahjongRule;
import top.focess.net.PacketPreCodec;
import top.focess.net.packet.PacketCodec;

import java.util.List;
import java.util.UUID;

public class GamePacketCodec extends PacketCodec<GamePacket> {
    @Override
    public @Nullable GamePacket readPacket(PacketPreCodec packetPreCodec) {
        UUID gameId = UUID.fromString(packetPreCodec.readString());
        MahjongRule rule = MahjongRule.valueOf(packetPreCodec.readString());
        GameState gameState = GameState.valueOf(packetPreCodec.readString());
        // todo tiles data

        List<PlayerData> playerData = Lists.newArrayList();
        int playerSize = packetPreCodec.readInt();
        for (int j = 0; j < playerSize; j++) {
            UUID playerId = UUID.fromString(packetPreCodec.readString());
            // todo
            playerData.add(new PlayerData(playerId, playerState));
        }
        return new GamePacket(new GameData(gameId, rule, gameState, null, playerData));
    }

    @Override
    public void writePacket(GamePacket packet, PacketPreCodec packetPreCodec) {
        GameData gameData = packet.getGameData();
        packetPreCodec.writeString(gameData.getId().toString());
        packetPreCodec.writeString(gameData.getRule().name());
        packetPreCodec.writeString(gameData.getGameState().name());
        // todo tiles data

        packetPreCodec.writeInt(gameData.getPlayerData().size());
        for (PlayerData playerData : gameData.getPlayerData()) {
            packetPreCodec.writeString(playerData.getId().toString());
            // todo
        }
    }
}
