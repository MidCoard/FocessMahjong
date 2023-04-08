package top.focess.mahjong.game.tile;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PlayerTiles {

    private final Set<Tile> tiles = Sets.newHashSet();

    public void addTiles(Set<Tile> tiles) {
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
}
