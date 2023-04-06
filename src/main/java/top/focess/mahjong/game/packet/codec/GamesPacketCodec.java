package top.focess.mahjong.game.packet.codec;

import com.google.common.collect.Lists;
import org.checkerframework.checker.nullness.qual.Nullable;
import top.focess.mahjong.game.GameState;
import top.focess.mahjong.game.data.GameData;
import top.focess.mahjong.game.data.PlayerData;
import top.focess.mahjong.game.packet.GamesPacket;
import top.focess.mahjong.game.rule.MahjongRule;
import top.focess.net.PacketPreCodec;
import top.focess.net.packet.PacketCodec;

import java.util.List;
import java.util.UUID;

public class GamesPacketCodec extends PacketCodec<GamesPacket> {
    @Override
    public @Nullable GamesPacket readPacket(PacketPreCodec packetPreCodec) {
        List<GameData> games = Lists.newArrayList();
        int size = packetPreCodec.readInt();
        for (int i = 0; i < size; i++) {
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

            games.add(new GameData(gameId, rule, gameState, null, playerData));
        }
        return new GamesPacket(games);
    }

    @Override
    public void writePacket(GamesPacket packet, PacketPreCodec packetPreCodec) {
        packetPreCodec.writeInt(packet.getGames().size());
        for (GameData gameData : packet.getGames()) {
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
}