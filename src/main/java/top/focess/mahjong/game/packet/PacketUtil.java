package top.focess.mahjong.game.packet;

import com.google.common.collect.Lists;
import top.focess.mahjong.game.Game;
import top.focess.mahjong.game.GameTileState;
import top.focess.mahjong.game.Player;
import top.focess.mahjong.game.data.GameData;
import top.focess.mahjong.game.data.PlayerData;
import top.focess.mahjong.game.data.TilesData;
import top.focess.mahjong.game.rule.MahjongRule;
import top.focess.mahjong.game.tile.TileState;
import top.focess.net.PacketPreCodec;

import java.util.List;
import java.util.UUID;

public class PacketUtil {

    public static void writeGameData(PacketPreCodec codec, GameData gameData) {
        codec.writeString(gameData.id().toString());
        codec.writeString(gameData.rule().name());
        codec.writeString(gameData.gameState().name());
        codec.writeInt(gameData.startTime());
        codec.writeInt(gameData.gameTime());
        codec.writeInt(gameData.countdown());

        writeTilesData(codec, gameData.tilesData());

        codec.writeInt(gameData.playerData().size());
        for (PlayerData playerData : gameData.playerData())
            writePlayerData(codec, playerData);
    }

    private static void writeTilesData(PacketPreCodec codec, TilesData tilesData) {
        if (tilesData == null)
            codec.writeBoolean(false);
        else {
            codec.writeBoolean(true);
            codec.writeInt(tilesData.remainTiles());
            codec.writeInt(tilesData.tiles().size());
            for (TileState state : tilesData.tiles())
                codec.writeInt(state.ordinal());
            codec.writeString(tilesData.gameTileState().name());
            codec.writeInt(tilesData.discardTiles().size());
            for (List<TileState> tiles : tilesData.discardTiles()) {
                codec.writeInt(tiles.size());
                for (TileState tile : tiles)
                    codec.writeInt(tile.ordinal());
            }
        }
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
        int gameTime = codec.readInt();
        int countdown = codec.readInt();

        TilesData tilesData = readTilesData(codec);

        List<PlayerData> playerData = Lists.newArrayList();
        int playerSize = codec.readInt();
        for (int j = 0; j < playerSize; j++)
            playerData.add(PacketUtil.readPlayerData(codec));
        return new GameData(gameId, rule, gameState, startTime,  gameTime, countdown, null, playerData);
    }

    private static TilesData readTilesData(PacketPreCodec codec) {
        if (codec.readBoolean()) {
            int remainTiles = codec.readInt();
            int tilesSize = codec.readInt();
            List<TileState> tiles = Lists.newArrayList();
            for (int i = 0; i < tilesSize; i++)
                tiles.add(TileState.values()[codec.readInt()]);
            GameTileState gameTileState = GameTileState.valueOf(codec.readString());
            int discardTilesSize = codec.readInt();
            List<List<TileState>> discardTiles = Lists.newArrayList();
            for (int i = 0; i < discardTilesSize; i++) {
                int discardTileSize = codec.readInt();
                List<TileState> discardTile = Lists.newArrayList();
                for (int j = 0; j < discardTileSize; j++)
                    discardTile.add(TileState.values()[codec.readInt()]);
                discardTiles.add(discardTile);
            }
            return new TilesData(remainTiles, tiles, gameTileState, discardTiles);
        } else
            return null;
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
