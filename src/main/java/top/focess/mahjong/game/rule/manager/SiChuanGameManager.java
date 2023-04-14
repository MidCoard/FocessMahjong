package top.focess.mahjong.game.rule.manager;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import top.focess.mahjong.game.GameTileState;
import top.focess.mahjong.game.LocalGame;
import top.focess.mahjong.game.data.TilesData;
import top.focess.mahjong.game.packet.Change3TilesDirectionPacket;
import top.focess.mahjong.game.packet.GameTileActionConfirmPacket;
import top.focess.mahjong.game.packet.GameTileActionNoticePacket;
import top.focess.mahjong.game.packet.GameTileActionPacket;
import top.focess.mahjong.game.tile.PlayerTiles;
import top.focess.mahjong.game.tile.Tile;
import top.focess.mahjong.game.tile.TileState;
import top.focess.mahjong.game.tile.Tiles;
import top.focess.mahjong.terminal.TerminalLauncher;
import top.focess.util.Pair;

import java.util.*;
import java.util.function.Function;

public class SiChuanGameManager extends GameManager {

	private final int playerSize;
	private final LocalGame game;
	private final Function<Integer, Integer> selfDrawn;
	private final Tiles tiles = new Tiles(108);
	private final List<PlayerTiles> playerTilesList = Lists.newArrayList();
	// used to save the cached action like change 3 tileStates
	private final List<Map<GameTileActionPacket.TileAction, Set<Tile>>> cachedActions = Lists.newArrayList();
	private final Random random = new Random();
	private GameTileState previousGameTileState;
	private GameTileState gameTileState = GameTileState.SHUFFLING;
	private int countdown = this.gameTileState.getTime();
	// indicate current discard player
	private int currentPlayer;
	// indicate current fetched tile or discarded tile
	@Nullable
	private Tile currentTile;
	private int dealer = -1;

	public SiChuanGameManager(final LocalGame game, final int playerSize, final Function<Integer, Integer> selfDrawn) {
		this.game = game;
		this.playerSize = playerSize;
		this.selfDrawn = selfDrawn;

		// shuffling tileStates
		final List<TileState> tileStates = Lists.newArrayList();
		for (int i = 0; i < 27; i++)
			for (int j = 0; j < 4; j++)
				tileStates.add(TileState.values()[i]);
		Collections.shuffle(tileStates, this.random);
		for (int i = 0; i < 108; i++)
			this.tiles.getTile(i).setTileState(tileStates.get(i));

		for (int i = 0; i < playerSize; i++)
			this.playerTilesList.add(new PlayerTiles());

		for (int i = 0; i < playerSize; i++)
			this.cachedActions.add(Maps.newHashMap());


		// we ignore the random tileStates because shuffling tileStates is enough
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < playerSize; j++)
				this.playerTilesList.get(j).addTile(this.tiles.fetch(4));

		this.playerTilesList.get(0).addTile(this.tiles.fetch(1));
		for (int i = 1; i < playerSize; i++)
			this.playerTilesList.get(i).addTile(this.tiles.fetch(1));
		this.playerTilesList.get(0).addTile(this.tiles.fetch(1));
		this.currentPlayer = 0;
		this.currentTile = null;
	}

	@Override
	public synchronized void doTileAction(final GameTileActionPacket.TileAction tileAction, final int player, final TileState... tileStates) {
		if (player >= this.playerTilesList.size() || player < 0)
			throw new IndexOutOfBoundsException("The player is out of the playerTiles");
		final PlayerTiles playerTiles = this.playerTilesList.get(player);
		if (tileAction == GameTileActionPacket.TileAction.CHANGE_3_TILES) {
			if (this.gameTileState != GameTileState.CHANGING_3_TILES)
				return;
			if (tileStates.length != 3)
				return;
			final Set<Tile> tiles;
			if ((tiles = playerTiles.getHandTiles(tileStates)).size() != 3)
				return;
			final TileState.TileStateCategory category = tileStates[0].getCategory();
			for (int i = 1; i < 3; i++)
				if (tileStates[i].getCategory() != category)
					return;
			this.cachedActions.get(player).put(tileAction, tiles);
		} else if (tileAction == GameTileActionPacket.TileAction.KONG) {
			// discarding and 4 tileStates with the same state
			// discarding and 3 tileStates with the same state and 1 tile with the same state
			// condition and 4 tileStates with the same state
			if (this.tiles.getRemainSize() == 0)
				return;
			if (this.gameTileState != GameTileState.DISCARDING && this.gameTileState != GameTileState.CONDITION)
				return;
			if (this.gameTileState == GameTileState.DISCARDING && this.currentPlayer != player)
				return;
			if (this.gameTileState == GameTileState.CONDITION && this.currentPlayer == player)
				return;
			if (tileStates.length != 1)
				return;
			final int count = playerTiles.getTileStateCount(tileStates[0]);
			if (count < 3)
				return;
			if (count < 4 && this.currentTile == null)
				return;
			if (count < 4 && this.currentTile.getTileState() != tileStates[0])
				return;
			if (playerTiles.getHandTileStateCount(tileStates[0]) == 0)
				return;
			// push to stack wait other players action
			final Set<Tile> tiles = playerTiles.getTiles(tileStates[0]);
			if (this.currentTile != null && this.currentTile.getTileState() == tileStates[0])
				tiles.add(this.currentTile);
			if (this.gameTileState == GameTileState.DISCARDING) {
				// no wait
				if (playerTiles.getHandTileStateCount(tileStates[0]) >= 3) {
					playerTiles.addScore((int) ((this.playerTilesList.stream().filter(i -> !i.isHu()).count() - 1) * 2));
					this.playerTilesList.stream().filter(i -> !i.isHu()).filter(i -> i != playerTiles).forEach(t -> t.addScore(-2));
					playerTiles.addTile(this.currentTile);
					playerTiles.kong(tiles);
					this.currentTile = this.tiles.fetch();
					this.currentTile.addDetail(Tile.AFTER_KONG_FETCHED_TILE);
					this.previousGameTileState = this.gameTileState;
					this.gameTileState = GameTileState.DISCARDING;
					this.countdown = this.gameTileState.getTime();
					this.game.sendPacket(new GameTileActionConfirmPacket(this.game.getPlayerId(player), this.game.getId(), tileAction, tileStates));
				} else {
					final Tile tile;
					if (this.currentTile.getTileState() == tileStates[0])
						tile = this.currentTile;
					else
						tile = playerTiles.getHandTiles(tileStates[0]).iterator().next();
					tile.addDetail(Tile.NORMAL_KONG_TILE);
					this.currentTile = tile;
					this.gameTileState = GameTileState.CONDITION_HU;
					this.countdown = this.gameTileState.getTime();
					this.game.sendPacket(new GameTileActionNoticePacket(this.game.getPlayerId(player), this.game.getId(), tileAction, tileStates));
				}
			} else {
				// condition wait other players action
				this.cachedActions.get(player).put(tileAction, tiles);
				this.game.sendPacket(new GameTileActionNoticePacket(this.game.getPlayerId(player), this.game.getId(), tileAction, tileStates));
			}
		} else if (tileAction == GameTileActionPacket.TileAction.DISCARD) {
			if (this.gameTileState != GameTileState.DISCARDING)
				return;
			if (this.currentPlayer != player)
				return;
			if (tileStates.length != 1)
				return;
			if (playerTiles.getRandomTiles(1, playerTiles.getLarkSuit(), this.random).size() != 0 && playerTiles.getLarkSuit() != tileStates[0].getCategory())
				return;
			if (this.currentTile != null && this.currentTile.getTileState() != tileStates[0] && playerTiles.getHandTileStateCount(tileStates[0]) == 0)
				return;
			if (this.currentTile == null && playerTiles.getHandTileStateCount(tileStates[0]) == 0)
				return;
			// force stop discarding
			final Tile tile;
			if (this.currentTile != null && this.currentTile.getTileState() == tileStates[0])
				tile = this.currentTile;
			else
				tile = playerTiles.getHandTiles(tileStates[0]).iterator().next();
			if (this.currentTile != null && this.currentTile.isDetail(Tile.AFTER_KONG_FETCHED_TILE))
				tile.addDetail(Tile.AFTER_KONG_DISCARDED_TILE);
			playerTiles.addTile(this.currentTile);
			playerTiles.discard(tile);
			this.playerTilesList.stream().filter(i -> !i.isHu()).forEach(i -> i.markHu(tile));
			this.currentTile = tile;
			this.previousGameTileState = this.gameTileState;
			this.gameTileState = GameTileState.WAITING;
			this.countdown = this.gameTileState.getTime();
			this.game.sendPacket(new GameTileActionConfirmPacket(this.game.getPlayerId(player), this.game.getId(), tileAction, tileStates));
		} else if (tileAction == GameTileActionPacket.TileAction.HU) {
			if (this.gameTileState != GameTileState.DISCARDING && this.gameTileState != GameTileState.CONDITION && this.gameTileState != GameTileState.CONDITION_HU)
				return;
			if (this.gameTileState == GameTileState.DISCARDING && this.currentPlayer != player)
				return;
			if (this.gameTileState == GameTileState.CONDITION && this.currentPlayer == player)
				return;
			if (this.gameTileState == GameTileState.CONDITION_HU && this.currentPlayer == player)
				return;
			if (tileStates.length != 0)
				return;
			if (this.currentTile != null && !this.playerTilesList.get(player).huable(this.currentTile))
				return;
			// which means in first player discarding tiles
			if (this.currentTile == null && !this.playerTilesList.get(player).huable(null))
				return;
			if (this.gameTileState == GameTileState.DISCARDING) {
				// no wait
				final int score = this.selfDrawn.apply(playerTiles.getTileScore(this.currentTile));
				playerTiles.addScore((int) ((this.playerTilesList.stream().filter(i -> !i.isHu()).count() - 1) * score));
				this.playerTilesList.stream().filter(i -> !i.isHu()).filter(i -> i != playerTiles).forEach(t -> t.addScore(-score));
				playerTiles.addTile(this.currentTile);
				playerTiles.hu();
				if (this.dealer == -1)
					this.dealer = this.currentPlayer;
				this.game.sendPacket(new GameTileActionConfirmPacket(this.game.getPlayerId(player), this.game.getId(), tileAction));
				this.previousGameTileState = this.gameTileState;
				this.gameTileState = GameTileState.WAITING_HU;
				this.countdown = this.gameTileState.getTime();
			} else {
				this.cachedActions.get(player).put(tileAction, Collections.singleton(this.currentTile));
				this.game.sendPacket(new GameTileActionNoticePacket(this.game.getPlayerId(player), this.game.getId(), tileAction, tileStates));
			}
		} else if (tileAction == GameTileActionPacket.TileAction.PUNG) {
			if (this.gameTileState != GameTileState.CONDITION)
				return;
			if (this.currentPlayer == player)
				return;
			if (tileStates.length != 0)
				return;
			if (playerTiles.getHandTileStateCount(this.currentTile.getTileState()) < 2)
				return;
			final Set<Tile> tiles = playerTiles.getHandTiles(this.currentTile.getTileState());
			tiles.add(this.currentTile);
			this.cachedActions.get(player).put(GameTileActionPacket.TileAction.PUNG, tiles);
			this.game.sendPacket(new GameTileActionNoticePacket(this.game.getPlayerId(player), this.game.getId(), tileAction, tileStates));
		}
	}

	public int getCountdown() {
		return this.countdown;
	}

	public int getCurrentPlayer() {
		return this.currentPlayer;
	}

	@Override
	public TileState getCurrentTileState() {
		return this.currentTile == null ? null : this.currentTile.getTileState();
	}

	@Override
	public GameTileState getGameTileState() {
		return this.gameTileState;
	}

	@Override
	public TilesData getTilesData(final int player) {
		if (player >= this.playerTilesList.size() || player < 0)
			return null;
		return new TilesData(this.tiles.getRemainSize(), this.playerTilesList.get(player).getRawTileStates(), this.gameTileState, this.playerTilesList.stream().map(PlayerTiles::getLarkSuit).toList(), this.playerTilesList.stream().map(PlayerTiles::getScore).toList(), this.playerTilesList.stream().map(PlayerTiles::getNoDiscardTileStates).toList(), this.playerTilesList.stream().map(PlayerTiles::getDiscardTileStates).toList(), this.game.getPlayerId(this.currentPlayer), (this.gameTileState == GameTileState.DISCARDING || this.currentTile == null) ? null : this.currentTile.getTileState());
	}

	@Override
	public void larkSuit(final int player, final TileState.TileStateCategory category) {
		if (player >= this.playerTilesList.size() || player < 0)
			throw new IndexOutOfBoundsException("The player is out of the playerTiles");
		final PlayerTiles playerTiles = this.playerTilesList.get(player);
		if (this.gameTileState != GameTileState.LARKING_1_SUIT)
			return;
		playerTiles.setLarkSuit(category);
	}

	public synchronized void tick() {
		if (this.countdown > 0)
			this.countdown--;
		if (this.countdown == 0) {
			this.previousGameTileState = this.gameTileState;
			this.gameTileState = this.calculateNextState();
			this.countdown = this.gameTileState.getTime();
		}
	}

	private @NonNull GameTileState calculateNextState() {
		if (this.gameTileState == GameTileState.SHUFFLING)
			return GameTileState.CHANGING_3_TILES;
		else if (this.gameTileState == GameTileState.CHANGING_3_TILES) {
			final List<Set<Tile>> tiles = Lists.newArrayList();
			for (int i = 0; i < this.playerTilesList.size(); i++) {
				if (this.cachedActions.get(i).getOrDefault(GameTileActionPacket.TileAction.CHANGE_3_TILES, Set.of()).size() == 0) {
					final PlayerTiles playerTiles = this.playerTilesList.get(i);
					final TileState.TileStateCategory category = playerTiles.getLeastCategory(3);// can be fixed. if we have multiple tileStates with the same category, we should choose the one with the most effective tileStates
					this.cachedActions.get(i).put(GameTileActionPacket.TileAction.CHANGE_3_TILES, playerTiles.getRandomTiles(3, category, this.random));
				}
				final Set<Tile> list = this.cachedActions.get(i).get(GameTileActionPacket.TileAction.CHANGE_3_TILES);
				this.cachedActions.get(i).remove(GameTileActionPacket.TileAction.CHANGE_3_TILES);
				this.playerTilesList.get(i).removeTiles(list);
				tiles.add(Sets.newHashSet(list));
			}
			final int dir = this.random.nextInt(this.playerSize - 1);
			if (dir == 0)
				for (int i = 0; i < this.playerTilesList.size(); i++)
					this.playerTilesList.get(i).addTile(tiles.get((i + 1) % this.playerSize));
			else if (dir == 1)
				for (int i = 0; i < this.playerTilesList.size(); i++)
					this.playerTilesList.get(i).addTile(tiles.get((i + this.playerSize - 1) % this.playerSize));
			else if (dir == 2)
				for (int i = 0; i < this.playerTilesList.size(); i++)
					this.playerTilesList.get(i).addTile(tiles.get((i + this.playerSize - 2) % this.playerSize));
			TerminalLauncher.change("changeDirection", this.game, -1, dir);
			this.game.sendPacket(new Change3TilesDirectionPacket(this.game.getId(), dir));
			return GameTileState.WAITING;
		} else if (this.gameTileState == GameTileState.DISCARDING) {
			final PlayerTiles playerTiles = this.playerTilesList.get(this.currentPlayer);
			Tile tile = this.currentTile;
			if (tile == null) {
				final Set<Tile> tiles = playerTiles.getRandomTiles(1, playerTiles.getLarkSuit(), this.random);
				if (tiles.size() == 0) {
					final TileState.TileStateCategory category = playerTiles.getLeastCategory(1);
					tile = playerTiles.getRandomTiles(1, category, this.random).iterator().next();
					playerTiles.discard(tile);
				} else {
					tile = tiles.iterator().next();
					playerTiles.discard(tile);
				}
			} else if (tile.isDetail(Tile.AFTER_KONG_FETCHED_TILE))
				tile.addDetail(Tile.AFTER_KONG_DISCARDED_TILE);
			final Tile finalTile = tile;
			this.playerTilesList.stream().filter(i -> !i.isHu()).forEach(i -> i.markHu(finalTile));
			this.game.sendPacket(new GameTileActionConfirmPacket(this.game.getPlayerId(this.currentPlayer), this.game.getId(), GameTileActionPacket.TileAction.DISCARD, tile.getTileState()));
			return GameTileState.WAITING;
		} else if (this.gameTileState == GameTileState.CONDITION) {
			final List<Pair<GameTileActionPacket.TileAction, Pair<Integer, Set<Tile>>>> pairs = Lists.newArrayList();
			for (int i = 0; i < this.playerSize; i++) {
				final Map<GameTileActionPacket.TileAction, Set<Tile>> map = this.cachedActions.get(i);
				for (final Map.Entry<GameTileActionPacket.TileAction, Set<Tile>> entry : map.entrySet())
					pairs.add(Pair.of(entry.getKey(), Pair.of(i, entry.getValue())));
			}
			pairs.sort(Comparator.comparingInt(o -> o.getLeft().getPriority()));
			boolean flag = false;
			final List<Integer> huPlayers = Lists.newArrayList();
			if (!pairs.isEmpty()) {
				final int priority = pairs.get(0).getLeft().getPriority();
				final List<Pair<GameTileActionPacket.TileAction, Pair<Integer, Set<Tile>>>> list = pairs.stream().filter(pair -> pair.getLeft().getPriority() == priority).toList();
				for (final Pair<GameTileActionPacket.TileAction, Pair<Integer, Set<Tile>>> pair : list) {
					final GameTileActionPacket.TileAction action = pair.getLeft();
					final PlayerTiles playerTiles = this.playerTilesList.get(pair.getRight().getLeft());
					final PlayerTiles currentTiles = this.playerTilesList.get(this.currentPlayer);
					final Set<Tile> tiles = pair.getRight().getRight();
					if (action == GameTileActionPacket.TileAction.HU) {
						this.currentTile.addDetail(Tile.HU_TILE);
						final int score = playerTiles.getTileScore(this.currentTile);
						playerTiles.addScore(score);
						currentTiles.addScore(-score);
						playerTiles.addTile(this.currentTile);
						playerTiles.hu();
						huPlayers.add(pair.getRight().getLeft());
						flag = true;
					} else if (action == GameTileActionPacket.TileAction.KONG) {
						this.currentTile.addDetail(Tile.KONG_TILE);
						playerTiles.addScore(2);
						currentTiles.addScore(-2);
						playerTiles.addTile(this.currentTile);
						playerTiles.kong(tiles);
						this.currentPlayer = pair.getRight().getLeft();
					} else if (action == GameTileActionPacket.TileAction.PUNG) {
						this.currentTile.addDetail(Tile.PUNG_TILE);
						playerTiles.addTile(this.currentTile);
						playerTiles.pung(tiles);
						this.currentPlayer = pair.getRight().getLeft();
					} else throw new IllegalStateException("Unexpected value: " + action);
					this.game.sendPacket(new GameTileActionConfirmPacket(this.game.getPlayerId(pair.getRight().getLeft()), this.game.getId(), action, tiles.stream().map(Tile::getTileState).toArray(TileState[]::new)));
				}
			}
			if (flag) {
				if (this.dealer == -1)
					if (huPlayers.size() > 1)
						this.dealer = this.currentPlayer;
					else this.dealer = huPlayers.get(0);
				for (int i = 1; i < this.playerSize; i++) {
					huPlayers.remove((Object) ((this.currentPlayer + i) % this.playerSize));
					if (huPlayers.isEmpty()) {
						this.currentPlayer = (this.currentPlayer + i) % this.playerSize;
						break;
					}
				}
			}
			return GameTileState.WAITING;
		} else if (this.gameTileState == GameTileState.LARKING_1_SUIT) {
			for (final PlayerTiles playerTiles : this.playerTilesList)
				if (playerTiles.getLarkSuit() == null)
					playerTiles.setLarkSuit(playerTiles.getLeastCategory(0));
			return GameTileState.WAITING;
		} else if (this.gameTileState == GameTileState.CONDITION_HU) {
			// this.currentTile cannot be null
			boolean flag = false;
			final List<Integer> huPlayers = Lists.newArrayList();
			for (int i = 0; i < this.playerSize; i++) {
				final PlayerTiles playerTiles = this.playerTilesList.get(i);
				final Map<GameTileActionPacket.TileAction, Set<Tile>> map = this.cachedActions.get(i);
				if (map.containsKey(GameTileActionPacket.TileAction.HU)) {
					this.currentTile.addDetail(Tile.HU_TILE);
					final int score = playerTiles.getTileScore(this.currentTile);
					playerTiles.addScore(score);
					this.playerTilesList.get(this.currentPlayer).addScore(-score);
					playerTiles.addTile(this.currentTile);
					playerTiles.hu();
					huPlayers.add(i);
					this.game.sendPacket(new GameTileActionConfirmPacket(this.game.getPlayerId(i), this.game.getId(), GameTileActionPacket.TileAction.HU, this.currentTile.getTileState()));
					flag = true;
				}
			}
			if (flag) {
				if (this.dealer == -1)
					if (huPlayers.size() > 1)
						this.dealer = this.currentPlayer;
					else this.dealer = huPlayers.get(0);
				return GameTileState.WAITING;
			}
			final PlayerTiles playerTiles = this.playerTilesList.get(this.currentPlayer);
			playerTiles.addScore((int) (this.playerTilesList.stream().filter(PlayerTiles::isHu).count() - 1));
			this.playerTilesList.stream().filter(i -> !i.isHu()).filter(i -> i != playerTiles).forEach(t -> t.addScore(-1));
			this.currentTile = this.tiles.fetch(); // must have a tile
			this.game.sendPacket(new GameTileActionConfirmPacket(this.game.getPlayerId(this.currentPlayer), this.game.getId(), GameTileActionPacket.TileAction.KONG, this.currentTile.getTileState()));
			return GameTileState.DISCARDING;
		} else if (this.gameTileState == GameTileState.WAITING) {
			if (this.previousGameTileState == GameTileState.CHANGING_3_TILES)
				return GameTileState.LARKING_1_SUIT;
			else if (this.previousGameTileState == GameTileState.LARKING_1_SUIT)
				return GameTileState.DISCARDING;
			else if (this.previousGameTileState == GameTileState.DISCARDING)
				return GameTileState.CONDITION;
			else if (this.previousGameTileState == GameTileState.CONDITION) {
				if (this.currentTile.isDetail(Tile.PUNG_TILE) || this.currentTile.isDetail(Tile.HU_TILE))
					this.currentPlayer = this.calculateNextPlayer();
				if (this.currentPlayer == -1)
					return GameTileState.FINISHED;
				if (this.tiles.getRemainSize() == 0)
					return GameTileState.FINISHED;
				this.currentTile = this.tiles.fetch();
				return GameTileState.DISCARDING;
			}
		} else if (this.gameTileState == GameTileState.WAITING_HU) {
			this.currentPlayer = this.calculateNextPlayer();
			if (this.currentPlayer == -1)
				return GameTileState.FINISHED;
			if (this.tiles.getRemainSize() == 0)
				return GameTileState.FINISHED;
			this.currentTile = this.tiles.fetch();
			return GameTileState.DISCARDING;
		} else if (this.gameTileState == GameTileState.FINISHED) {
			return GameTileState.FINISHED;
		}
		throw new IllegalStateException("The gameTileState is illegal");
	}

	private int calculateNextPlayer() {
		for (int i = 1; i < this.playerSize; i++) {
			final PlayerTiles playerTiles = this.playerTilesList.get((this.currentPlayer + i) % this.playerSize);
			if (!playerTiles.isHu())
				return (this.currentPlayer + i) % this.playerSize;
		}
		return -1;
	}

	public int getDealer() {
		if (this.dealer == -1)
			this.dealer = 0;
		return this.dealer;
	}
}
