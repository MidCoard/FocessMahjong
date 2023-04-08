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
        codec.writeString(gameData.getId().toString());
        codec.writeString(gameData.getRule().name());
        codec.writeString(gameData.getGameState().name());
        codec.writeInt(gameData.getStartTime());
        // todo tiles data

        codec.writeInt(gameData.getPlayerData().size());
        for (PlayerData playerData : gameData.getPlayerData())
            writePlayerData(codec, playerData);
    }

    public static void writePlayerData(PacketPreCodec codec, PlayerData playerData) {
        codec.writeString(playerData.id().toString());
        codec.writeString(playerData.playerState().name());
        // todo
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
        Player.PlayerState playerState = Player.PlayerState.valueOf(codec.readString());
        // todo
        return new PlayerData(playerId, this.name, playerState);
    }
}
