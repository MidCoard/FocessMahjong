package top.focess.mahjong.game.tile;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.*;

public class PlayerTiles {

    private final Set<Tile> notDiscardTiles = Sets.newHashSet();

    private final List<Tile> discardTiles = Lists.newArrayList();

    private final Set<Tile> tiles = Sets.newHashSet();

    private final Set<Tile> huableTiles = Sets.newHashSet();

    private int score;
    private boolean isHu;
    private TileState.TileStateCategory larkSuit;

    public void addTile(final Set<Tile> tiles) {
        this.tiles.addAll(tiles);
    }

    public List<TileState> getRawTileStates() {
        final List<TileState> tiles = Lists.newArrayList();
        for (final Tile tile : this.tiles)
            tiles.add(tile.getTileState());
        Collections.sort(tiles);
        return tiles;
    }

    public List<Tile> getRawTiles() {
        final List<Tile> tiles = Lists.newArrayList();
        tiles.addAll(this.tiles);
        Collections.sort(tiles);
        return tiles;
    }

    public TileState.TileStateCategory getLeastCategory(final int size) {
        final Map<TileState.TileStateCategory, Integer> map = Maps.newHashMap();
        for (final Tile tile : this.tiles) {
            final TileState.TileStateCategory category = tile.getTileState().getCategory();
            map.compute(category, (k, v) -> null == v ? 1 : v + 1);
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

    public int getTileStateCount(final TileState tileState) {
        return (int) this.getRawTileStates().stream().filter(tileState1 -> tileState1 == tileState).count() + (int) this.notDiscardTiles.stream().filter(tile -> tile.getTileState() == tileState).count();
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

    public void kong(final Set<Tile> tiles) {
        this.notDiscardTiles.addAll(tiles);
        this.tiles.removeAll(tiles);
    }

    public int getScore() {
        return this.score;
    }

    public void addScore(final int score) {
        this.score += score;
    }

    public boolean isHu() {
        return this.isHu;
    }

    public void discard(final Tile tile) {
        this.tiles.remove(tile);
        this.discardTiles.add(tile);
        this.huableTiles.clear();
    }

    public List<TileState> getDiscardTileStates() {
        return this.discardTiles.stream().filter(tile -> !tile.isDetail(Tile.KONG_TILE) && !tile.isDetail(Tile.PUNG_TILE) && !tile.isDetail(Tile.HU_TILE)).map(Tile::getTileState).toList();
    }

    public void addTile(final Tile tile) {
        this.huableTiles.clear();
        if (null == tile)
            return;
        this.tiles.add(tile);
    }

    public int getHandTileStateCount(final TileState tileState) {
        return (int) this.tiles.stream().filter(tile -> tile.getTileState() == tileState).count();
    }

    public boolean huable(final Tile tile) {
        return !this.huableTiles.contains(tile);
        // todo calc hu
    }

    public void hu() {
        this.isHu = true;
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

    public Set<Tile> getRandomTiles(final int size, final TileState.TileStateCategory category, final Random random) {
        final List<Tile> tiles = Lists.newArrayList();
        for (final Tile tile : this.tiles)
            if (tile.getTileState().getCategory() == category)
                tiles.add(tile);
        Collections.shuffle(tiles, random);
        return Sets.newHashSet(tiles.subList(0, size));
    }

    public void removeTiles(final Collection<Tile> tiles) {
        this.tiles.removeAll(tiles);
    }

    public TileState.TileStateCategory getLarkSuit() {
        return this.larkSuit;
    }

    public void setLarkSuit(final TileState.TileStateCategory larkSuit) {
        this.larkSuit = larkSuit;
    }

    public int getTileScore(final Tile tile) {
        final int score = 1;
        // todo
        return Math.min(score, 16);
    }

    public void pung(final Set<Tile> tiles) {
        this.notDiscardTiles.addAll(tiles);
        this.tiles.removeAll(tiles);
    }

    public List<TileState> getNoDiscardTileStates() {
        return this.notDiscardTiles.stream().map(Tile::getTileState).toList();
    }

    public void markHu(final Tile tile) {
        if (this.huable(tile))
            this.huableTiles.add(tile);
    }

}
