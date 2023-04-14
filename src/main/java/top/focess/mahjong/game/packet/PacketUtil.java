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

	public static GameData readGameData(final PacketPreCodec codec) {
		final UUID gameId = UUID.fromString(codec.readString());
		final MahjongRule rule = MahjongRule.valueOf(codec.readString());
		final Game.GameState gameState = Game.GameState.valueOf(codec.readString());
		final int startTime = codec.readInt();
		final int gameTime = codec.readInt();
		final int countdown = codec.readInt();

		final TilesData tilesData = PacketUtil.readTilesData(codec);

		final List<PlayerData> playerData = Lists.newArrayList();
		final int playerSize = codec.readInt();
		for (int j = 0; j < playerSize; j++)
			playerData.add(PacketUtil.readPlayerData(codec));
		return new GameData(gameId, rule, gameState, startTime, gameTime, countdown, tilesData, playerData);
	}

	private static TilesData readTilesData(final PacketPreCodec codec) {
		if (codec.readBoolean()) {
			final int remainTiles = codec.readInt();
			final int tileStateSize = codec.readInt();
			final List<TileState> tileStates = Lists.newArrayList();
			for (int i = 0; i < tileStateSize; i++)
				tileStates.add(TileState.values()[codec.readInt()]);
			final GameTileState gameTileState = GameTileState.values()[codec.readInt()];
			final int larkSuitSize = codec.readInt();
			final List<TileState.TileStateCategory> larkSuits = Lists.newArrayList();
			for (int i = 0; i < larkSuitSize; i++)
				larkSuits.add(TileState.TileStateCategory.values()[codec.readInt()]);
			final List<Integer> scores = Lists.newArrayList();
			for (int i = 0; i < larkSuitSize; i++)
				scores.add(codec.readInt());
			final List<List<TileState>> noDiscardTileStats = Lists.newArrayList();
			for (int i = 0; i < larkSuitSize; i++) {
				final int noDiscardTileSize = codec.readInt();
				final List<TileState> noDiscardTileStatList = Lists.newArrayList();
				for (int j = 0; j < noDiscardTileSize; j++)
					noDiscardTileStatList.add(TileState.values()[codec.readInt()]);
				noDiscardTileStats.add(noDiscardTileStatList);
			}
			final List<List<TileState>> discardTileStats = Lists.newArrayList();
			for (int i = 0; i < larkSuitSize; i++) {
				final int discardTileSize = codec.readInt();
				final List<TileState> discardTileStatList = Lists.newArrayList();
				for (int j = 0; j < discardTileSize; j++)
					discardTileStatList.add(TileState.values()[codec.readInt()]);
				discardTileStats.add(discardTileStatList);
			}
			final UUID currentPlayerId = UUID.fromString(codec.readString());
			final TileState currentTileState = TileState.values()[codec.readInt()];
			return new TilesData(remainTiles, tileStates, gameTileState, larkSuits, scores, noDiscardTileStats, discardTileStats, currentPlayerId, currentTileState);
		} else
			return null;
	}

	public static PlayerData readPlayerData(final PacketPreCodec codec) {
		final UUID playerId = UUID.fromString(codec.readString());
		final String name = codec.readString();
		final Player.PlayerState playerState = Player.PlayerState.valueOf(codec.readString());
		final String gameIdStr = codec.tryReadString();
		final UUID gameId = gameIdStr == null ? null : UUID.fromString(gameIdStr);
		return new PlayerData(playerId, name, playerState, gameId);
	}

	public static void writeGameData(final PacketPreCodec codec, final GameData gameData) {
		codec.writeString(gameData.id().toString());
		codec.writeString(gameData.rule().name());
		codec.writeString(gameData.gameState().name());
		codec.writeInt(gameData.startTime());
		codec.writeInt(gameData.gameTime());
		codec.writeInt(gameData.countdown());

		PacketUtil.writeTilesData(codec, gameData.tilesData());

		codec.writeInt(gameData.playerData().size());
		for (final PlayerData playerData : gameData.playerData())
			PacketUtil.writePlayerData(codec, playerData);
	}

	private static void writeTilesData(final PacketPreCodec codec, final TilesData tilesData) {
		if (tilesData == null)
			codec.writeBoolean(false);
		else {
			codec.writeBoolean(true);
			codec.writeInt(tilesData.remainTiles());
			codec.writeInt(tilesData.tileStates().size());
			for (final TileState tile : tilesData.tileStates())
				codec.writeInt(tile.ordinal());
			codec.writeInt(tilesData.gameTileState().ordinal());
			codec.writeInt(tilesData.larkSuits().size());
			for (final TileState.TileStateCategory tileStateCategory : tilesData.larkSuits())
				codec.writeInt(tileStateCategory.ordinal());
			for (final int score : tilesData.scores())
				codec.writeInt(score);
			for (final List<TileState> tiles : tilesData.noDiscardTileStates()) {
				codec.writeInt(tiles.size());
				for (final TileState tile : tiles)
					codec.writeInt(tile.ordinal());
			}
			for (final List<TileState> tiles : tilesData.discardTileStates()) {
				codec.writeInt(tiles.size());
				for (final TileState tile : tiles)
					codec.writeInt(tile.ordinal());
			}
			codec.writeString(tilesData.currentPlayerId().toString());
			codec.writeInt(tilesData.currentTileState().ordinal());
		}
	}

	public static void writePlayerData(final PacketPreCodec codec, final PlayerData playerData) {
		codec.writeString(playerData.id().toString());
		codec.writeString(playerData.name());
		codec.writeString(playerData.playerState().name());
		codec.tryWriteString(playerData.gameId() == null ? null : playerData.gameId().toString());
	}
}
