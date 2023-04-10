package top.focess.mahjong.game.tile;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PlayerTiles {

    private final Set<Tile> notDiscardTiles = Sets.newHashSet();

    private final List<Tile> discardTiles = Lists.newArrayList();

    private final Set<Tile> tiles = Sets.newHashSet();

    private int score = 0;
    private boolean isHu = false;

    public void addTile(Set<Tile> tiles) {
        this.tiles.addAll(tiles);
    }

    public List<TileState> getRawTileStates() {
        List<TileState> tiles = Lists.newArrayList();
        for (Tile tile : this.tiles)
            tiles.add(tile.getTileState());
        Collections.sort(tiles);
        return tiles;
    }

    public List<Tile> getRawTiles() {
        List<Tile> tiles = Lists.newArrayList();
        tiles.addAll(this.tiles);
        Collections.sort(tiles);
        return tiles;
    }

    public int getTilesSize() {
        return this.tiles.size();
    }

    public Tile getTile(int index) {
        return getRawTiles().get(index);
    }

    public TileState getTileState(int index) {
        return this.getTile(index).getTileState();
    }

    public TileState.TileStateCategory getLeastCategory(int size) {
        Map<TileState.TileStateCategory, Integer> map = Maps.newHashMap();
        for (Tile tile : this.tiles) {
            TileState.TileStateCategory category = tile.getTileState().getCategory();
            map.compute(category, (k, v) -> v == null ? 1 : v + 1);
        }
        int min = Integer.MAX_VALUE;
        TileState.TileStateCategory category = null;
        for (Map.Entry<TileState.TileStateCategory, Integer> entry : map.entrySet())
            if (entry.getValue() >= size && entry.getValue() < min) {
                min = entry.getValue();
                category = entry.getKey();
            }
        return category;
    }

    public List<Integer> getRandomIndexes(int size, TileState.TileStateCategory category) {
        List<Integer> indexes = Lists.newArrayList();
        List<TileState> tileStates = this.getRawTileStates();
        for (int i = 0; i < tileStates.size(); i++)
            if (tileStates.get(i).getCategory() == category)
                indexes.add(i);
        Collections.shuffle(indexes);
        return indexes.subList(0, size);
    }

    public Set<Tile> getAndRemoveTiles(List<Integer> indexes) {
        Set<Tile> ret = Sets.newHashSet();
        List<Tile> tiles = this.getRawTiles();
        for (int i = 0; i < tiles.size(); i++)
            if (indexes.contains(i))
                ret.add(tiles.get(i));
        ret.forEach(this.tiles::remove);
        return ret;
    }

    public int getTileStateCount(TileState tileState) {
        return (int) getRawTileStates().stream().filter(tileState1 -> tileState1.equals(tileState)).count() + (int) this.notDiscardTiles.stream().filter(tile -> tile.getTileState().equals(tileState)).count();
    }

    public Set<Tile> getTiles(TileState tileState) {
        Set<Tile> ret = Sets.newHashSet();
        for (Tile tile : this.tiles)
            if (tile.getTileState().equals(tileState))
                ret.add(tile);
        return ret;
    }

    public void kong(TileState tileState) {
        this.notDiscardTiles.addAll(this.getTiles(tileState));
        this.tiles.removeIf(tile -> tile.getTileState().equals(tileState));
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void addScore(int score) {
        this.score += score;
    }

    public boolean isHu() {
        return this.isHu;
    }

    public void discard(TileState tileState) {
        Tile tile = this.tiles.stream().filter(tile1 -> tile1.getTileState().equals(tileState)).findFirst().orElse(null);
        if (tile == null)
            throw new IllegalArgumentException("No such tile");
        this.tiles.remove(tile);
        this.discardTiles.add(tile);
    }

    public List<TileState> getDiscardTileStates() {
        List<TileState> tileStates = Lists.newArrayList();
        for (Tile tile : this.discardTiles)
            tileStates.add(tile.getTileState());
        return tileStates;
    }

    public void addTile(Tile tile) {
        this.tiles.add(tile);
    }

    public int getHandTileStateCount(TileState tileState) {
        return (int) this.tiles.stream().filter(tile -> tile.getTileState().equals(tileState)).count();
    }

    public boolean huable(TileState tileState) {
        // calc hu
        return true;
    }

    public void hu(Tile tile) {
        this.isHu = true;
        this.addTile(tile);
    }
}
