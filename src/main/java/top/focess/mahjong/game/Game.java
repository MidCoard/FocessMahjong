package top.focess.mahjong.game;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import top.focess.mahjong.game.data.GameData;
import top.focess.mahjong.game.data.TilesData;
import top.focess.mahjong.game.packet.GameTileActionPacket;
import top.focess.mahjong.game.remote.GameRequester;
import top.focess.mahjong.game.remote.RemotePlayer;
import top.focess.mahjong.game.rule.MahjongRule;
import top.focess.mahjong.game.tile.TileState;
import top.focess.mahjong.terminal.TerminalLauncher;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public abstract class Game {

	protected static final Map<UUID, Game> GAMES = Maps.newConcurrentMap();
	protected final GameRequester gameRequester = new GameRequester();
	protected final List<Player> players = Lists.newArrayList();
	private final MahjongRule rule;
	private final UUID id;
	private int startTime = -1;
	private int gameTime;
	private int countdown = -1;
	private GameState gameState = GameState.WAITING;
	private TilesData tilesData;

	public Game(final MahjongRule rule) {
		this(UUID.randomUUID(), rule);
	}

	public Game(final UUID id, final MahjongRule rule) {
		this.id = id;
		this.rule = rule;
		Game.GAMES.put(id, this);
	}

	public static Game getGame(final UUID gameId) {
		return Game.GAMES.get(gameId);
	}

	public static List<Game> getGames() {
		return Collections.unmodifiableList(Lists.newArrayList(Game.GAMES.values()));
	}

	protected void countdownStartTime() {
		this.setStartTime(this.getStartTime() - 1);
	}

	public int getStartTime() {
		return this.startTime;
	}

	protected void setStartTime(final int startTime) {
		if (this.startTime != startTime) {
			TerminalLauncher.change("startTime", this, this.startTime, startTime);
			this.startTime = startTime;
		}
	}

	public abstract void doTileAction(GameTileActionPacket.TileAction tileAction, Player player, TileState... tileStates);

	public GameData getGameData() {
		return new GameData(this.getId(), this.rule, this.gameState, this.startTime, this.getGameTime(), this.getCountdown(), null, this.players.stream().map(Player::getPlayerData).toList());
	}

	public UUID getId() {
		return this.id;
	}

	public int getGameTime() {
		return this.gameTime;
	}

	public void setGameTime(final int gameTime) {
		if (this.gameTime != gameTime) {
			TerminalLauncher.change("gameTime", this, this.gameTime, gameTime);
			this.gameTime = gameTime;
		}
	}

	public int getCountdown() {
		return this.countdown;
	}

	public void setCountdown(final int countdown) {
		if (this.countdown != countdown) {
			TerminalLauncher.change("countdown", this, this.countdown, countdown);
			this.countdown = countdown;
		}
	}

	public GameRequester getGameRequester() {
		return this.gameRequester;
	}

	public GameState getGameState() {
		return this.gameState;
	}

	public void setGameState(final GameState gameState) {
		if (this.gameState != gameState) {
			TerminalLauncher.change("gameState", this, this.gameState, gameState);
			this.gameState = gameState;
		}
	}

	public abstract GameTileState getGameTileState();

	public MahjongRule getRule() {
		return this.rule;
	}

	public abstract boolean join(Player player);

	public abstract void larkSuit(RemotePlayer player, TileState.TileStateCategory category);

	public abstract boolean leave(Player player);

	public abstract boolean ready(Player player);

	protected void tickGameTime() {
		this.setGameTime(this.getGameTime() + 1);
	}

	public List<Player> getPlayers() {
		return this.players;
	}

	@Override
	public String toString() {
		return "Game{" +
				"gameState=" + this.gameState +
				", rule=" + this.rule +
				", id=" + this.id +
				'}' + super.toString();
	}

	public abstract boolean unready(Player player);

	public TilesData getTilesData() {
		return this.tilesData;
	}

	public void setTilesData(final TilesData tilesData) {
		if (this.tilesData != null && tilesData != null) {
			if (this.tilesData.remainTiles() != tilesData.remainTiles())
				TerminalLauncher.change("remainTiles", this, this.tilesData.remainTiles(), tilesData.remainTiles());
			if (!this.tilesData.tileStates().equals(tilesData.tileStates()))
				TerminalLauncher.change("tileStates", this, this.tilesData.tileStates(), tilesData.tileStates());
			if (this.tilesData.gameTileState() != tilesData.gameTileState())
				TerminalLauncher.change("gameTileState", this, this.tilesData.gameTileState(), tilesData.gameTileState());
			if (!this.tilesData.larkSuits().equals(tilesData.larkSuits()))
				TerminalLauncher.change("larkSuits", this, this.tilesData.larkSuits(), tilesData.larkSuits());
			if (!this.tilesData.scores().equals(tilesData.scores()))
				TerminalLauncher.change("scores", this, this.tilesData.scores(), tilesData.scores());
			if (!this.tilesData.discardTileStates().equals(tilesData.discardTileStates()))
				TerminalLauncher.change("discardTileStates", this, this.tilesData.discardTileStates(), tilesData.discardTileStates());
			if (!this.tilesData.currentPlayerId().equals(tilesData.currentPlayerId()))
				TerminalLauncher.change("currentPlayerId", this, this.tilesData.currentPlayerId(), tilesData.currentPlayerId());
			if (this.tilesData.currentTileState() != tilesData.currentTileState())
				TerminalLauncher.change("currentTileState", this, this.tilesData.currentTileState(), tilesData.currentTileState());
		} else if (this.tilesData != null || tilesData != null) {
			TerminalLauncher.change("remainTiles", this, this.tilesData == null ? null : this.tilesData.remainTiles(), tilesData == null ? null : tilesData.remainTiles());
			TerminalLauncher.change("tileStates", this, this.tilesData == null ? null : this.tilesData.tileStates(), tilesData == null ? null : tilesData.tileStates());
			TerminalLauncher.change("gameTileState", this, this.tilesData == null ? null : this.tilesData.gameTileState(), tilesData == null ? null : tilesData.gameTileState());
			TerminalLauncher.change("larkSuits", this, this.tilesData == null ? null : this.tilesData.larkSuits(), tilesData == null ? null : tilesData.larkSuits());
			TerminalLauncher.change("scores", this, this.tilesData == null ? null : this.tilesData.scores(), tilesData == null ? null : tilesData.scores());
			TerminalLauncher.change("discardTileStates", this, this.tilesData == null ? null : this.tilesData.discardTileStates(), tilesData == null ? null : tilesData.discardTileStates());
			TerminalLauncher.change("currentPlayerId", this, this.tilesData == null ? null : this.tilesData.currentPlayerId(), tilesData == null ? null : tilesData.currentPlayerId());
			TerminalLauncher.change("currentTileState", this, this.tilesData == null ? null : this.tilesData.currentTileState(), tilesData == null ? null : tilesData.currentTileState());
		}
		this.tilesData = tilesData;
	}


	public enum GameState {

		WAITING, // setup is done, waiting for players to join and ready.
		PLAYING // game is playing. including the shuffling tileStates, dealing tileStates, playing tileStates, and game over.

	}
}
