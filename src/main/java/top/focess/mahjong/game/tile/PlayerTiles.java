package top.focess.mahjong.game.tile;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import top.focess.mahjong.game.algorithm.HuAlgorithm;

import javax.annotation.Nullable;
import java.util.*;

public class PlayerTiles {

	private final Set<Tile> notDiscardTiles = Sets.newHashSet();

	private final List<Tile> discardTiles = Lists.newArrayList();

	private final Set<Tile> tiles = Sets.newHashSet();

	private final Map<Tile, Integer> huableTiles = Maps.newHashMap();

	private int score;
	private boolean isHu;
	private TileState.TileStateCategory larkSuit;

	public void addScore(final int score) {
		this.score += score;
	}

	public void addTile(final Set<Tile> tiles) {
		this.tiles.addAll(tiles);
	}

	public void addTile(final Tile tile) {
		this.huableTiles.clear();
		if (tile == null)
			return;
		this.tiles.add(tile);
	}

	public void discard(final Tile tile) {
		this.tiles.remove(tile);
		this.discardTiles.add(tile);
		this.huableTiles.clear();
	}

	public List<TileState> getDiscardTileStates() {
		return this.discardTiles.stream().filter(tile -> !tile.isDetail(Tile.KONG_TILE) && !tile.isDetail(Tile.PUNG_TILE) && !tile.isDetail(Tile.HU_TILE)).map(Tile::getTileState).toList();
	}

	public int getHandTileStateCount(final TileState tileState) {
		return (int) this.tiles.stream().filter(tile -> tile.getTileState() == tileState).count();
	}

	public Set<Tile> getHandTiles(final TileState... tileStates) {
		final List<Tile> tmp = Lists.newCopyOnWriteArrayList();
		final Set<Tile> ret = Sets.newHashSet();
		tmp.addAll(this.tiles);
		for (final Tile tile : tmp)
			for (final TileState tileState : tileStates)
				if (tile.getTileState() == tileState) {
					tmp.remove(tile);
					ret.add(tile);
				}
		return ret;
	}

	public TileState.TileStateCategory getLarkSuit() {
		return this.larkSuit;
	}

	public void setLarkSuit(final TileState.TileStateCategory larkSuit) {
		this.larkSuit = larkSuit;
	}

	public TileState.TileStateCategory getLeastCategory(final int size) {
		final Map<TileState.TileStateCategory, Integer> map = Maps.newHashMap();
		for (final Tile tile : this.tiles) {
			final TileState.TileStateCategory category = tile.getTileState().getCategory();
			map.compute(category, (k, v) -> v == null ? 1 : v + 1);
		}
		int min = Integer.MAX_VALUE;
		TileState.TileStateCategory category = null;
		for (final Map.Entry<TileState.TileStateCategory, Integer> entry : map.entrySet())
			if (entry.getValue() >= size && entry.getValue() < min) {
				min = entry.getValue();
				category = entry.getKey();
			}
		return category;
	}

	public List<TileState> getNoDiscardTileStates() {
		return this.notDiscardTiles.stream().map(Tile::getTileState).toList();
	}

	public Set<Tile> getRandomTiles(final int size, final TileState.TileStateCategory category, final Random random) {
		final List<Tile> tiles = Lists.newArrayList();
		for (final Tile tile : this.tiles)
			if (tile.getTileState().getCategory() == category)
				tiles.add(tile);
		Collections.shuffle(tiles, random);
		return Sets.newHashSet(tiles.subList(0, size));
	}

	public List<Tile> getRawTiles() {
		final List<Tile> tiles = Lists.newArrayList();
		tiles.addAll(this.tiles);
		Collections.sort(tiles);
		return tiles;
	}

	public int getScore() {
		return this.score;
	}

	public int getTileScore(final Tile tile) {
		final int score = HuAlgorithm.calculateHuScore(HuAlgorithm.calculateHuType(this.tiles, this.notDiscardTiles, tile));
		return Math.min(score, 16);
	}

	public int getTileStateCount(final TileState tileState) {
		return (int) this.getRawTileStates().stream().filter(tileState1 -> tileState1 == tileState).count() + (int) this.notDiscardTiles.stream().filter(tile -> tile.getTileState() == tileState).count();
	}

	public List<TileState> getRawTileStates() {
		final List<TileState> tiles = Lists.newArrayList();
		for (final Tile tile : this.tiles)
			tiles.add(tile.getTileState());
		Collections.sort(tiles);
		return tiles;
	}

	public Set<Tile> getTiles(final TileState tileState) {
		final Set<Tile> ret = Sets.newHashSet();
		for (final Tile tile : this.tiles)
			if (tile.getTileState() == tileState)
				ret.add(tile);
		for (final Tile tile : this.notDiscardTiles)
			if (tile.getTileState() == tileState)
				ret.add(tile);
		return ret;
	}

	public void hu() {
		this.isHu = true;
	}

	public boolean isHu() {
		return this.isHu;
	}

	public void kong(final Set<Tile> tiles) {
		this.notDiscardTiles.addAll(tiles);
		this.tiles.removeAll(tiles);
	}

	public void markHu(final Tile tile) {
		final int type = HuAlgorithm.calculateHuType(this.tiles, this.notDiscardTiles, tile);
		// mark hu type or hu score to indicate ...
		if (type != 0)
			this.huableTiles.put(tile, type);
	}

	public Set<Tile> getTiles() {
		final Set<Tile> ret = Sets.newHashSet(this.tiles);
		ret.addAll(this.notDiscardTiles);
		return ret;
	}

	public boolean huable(@Nullable final Tile tile) {
		final int type = HuAlgorithm.calculateHuType(this.tiles, this.notDiscardTiles, tile);
		if (type == 0)
			return false;
		return !this.huableTiles.containsValue(type);
	}

	public void pung(final Set<Tile> tiles) {
		this.notDiscardTiles.addAll(tiles);
		this.tiles.removeAll(tiles);
	}

	public void removeTiles(final Collection<Tile> tiles) {
		this.tiles.removeAll(tiles);
	}

}
