package top.focess.mahjong.game.packet;

import com.google.common.collect.Lists;
import top.focess.mahjong.game.Game;
import top.focess.mahjong.game.Player;
import top.focess.mahjong.game.data.GameData;
import top.focess.mahjong.game.data.PlayerData;
import top.focess.mahjong.game.rule.MahjongRule;
import top.focess.net.PacketPreCodec;

import java.util.List;
import java.util.UUID;

public class PacketUtil {

    public static void writeGameData(PacketPreCodec codec, GameData gameData) {
        codec.writeString(gameData.id().toString());
        codec.writeString(gameData.rule().name());
        codec.writeString(gameData.gameState().name());
        codec.writeInt(gameData.startTime());
        // todo tiles data

        codec.writeInt(gameData.playerData().size());
        for (PlayerData playerData : gameData.playerData())
            writePlayerData(codec, playerData);
    }

    public static void writePlayerData(PacketPreCodec codec, PlayerData playerData) {
        codec.writeString(playerData.id().toString());
        codec.writeString(playerData.name());
        codec.writeString(playerData.playerState().name());
        codec.tryWriteString(playerData.gameId() == null ? null : playerData.gameId().toString());
    }

    public static GameData readGameData(PacketPreCodec codec) {
        UUID gameId = UUID.fromString(codec.readString());
        MahjongRule rule = MahjongRule.valueOf(codec.readString());
        Game.GameState gameState = Game.GameState.valueOf(codec.readString());
        int startTime = codec.readInt();
        // todo tiles data

        List<PlayerData> playerData = Lists.newArrayList();
        int playerSize = codec.readInt();
        for (int j = 0; j < playerSize; j++)
            playerData.add(PacketUtil.readPlayerData(codec));
        return new GameData(gameId, rule, gameState, startTime, null, playerData);
    }

    public static PlayerData readPlayerData(PacketPreCodec codec) {
        UUID playerId = UUID.fromString(codec.readString());
        String name = codec.readString();
        Player.PlayerState playerState = Player.PlayerState.valueOf(codec.readString());
        String gameIdStr = codec.tryReadString();
        UUID gameId = gameIdStr == null ? null : UUID.fromString(gameIdStr);
        return new PlayerData(playerId, name, playerState, gameId);
    }
}
