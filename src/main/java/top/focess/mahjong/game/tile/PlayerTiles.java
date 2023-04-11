package top.focess.mahjong.game.tile;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.*;

public class PlayerTiles {

    private final Set<Tile> notDiscardTiles = Sets.newHashSet();

    private final List<Tile> discardTiles = Lists.newArrayList();

    private final Set<Tile> tiles = Sets.newHashSet();

    private int score = 0;
    private boolean isHu = false;
    private TileState.TileStateCategory larkSuit;

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

    public int getHandSize() {
        return this.tiles.size();
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

    public int getTileStateCount(TileState tileState) {
        return (int) getRawTileStates().stream().filter(tileState1 -> tileState1.equals(tileState)).count() + (int) this.notDiscardTiles.stream().filter(tile -> tile.getTileState().equals(tileState)).count();
    }

    public Set<Tile> getTiles(TileState tileState) {
        Set<Tile> ret = Sets.newHashSet();
        for (Tile tile : this.tiles)
            if (tile.getTileState().equals(tileState))
                ret.add(tile);
        for (Tile tile : this.notDiscardTiles)
            if (tile.getTileState().equals(tileState))
                ret.add(tile);
        return ret;
    }

    public void kong(Set<Tile> tiles) {
        this.notDiscardTiles.addAll(tiles);
        this.tiles.removeAll(tiles);
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
        return this.discardTiles.stream().map(Tile::getTileState).toList();
    }

    public void addTile(Tile tile) {
        if (tile == null)
            return;
        this.tiles.add(tile);
    }

    public int getHandTileStateCount(TileState tileState) {
        return (int) this.tiles.stream().filter(tile -> tile.getTileState().equals(tileState)).count();
    }

    public boolean huable(TileState tileState) {
        // todo calc hu
        return true;
    }

    public void hu(Tile tile) {
        this.isHu = true;
        this.addTile(tile);
    }

    public Set<Tile> getHandTiles(TileState... tileStates) {
        List<Tile> tmp = Lists.newCopyOnWriteArrayList();
        Set<Tile> ret = Sets.newHashSet();
        tmp.addAll(this.tiles);
        for (Tile tile : tmp)
            for (TileState tileState : tileStates)
                if (tile.getTileState().equals(tileState)) {
                    tmp.remove(tile);
                    ret.add(tile);
                }
        return ret;
    }

    public Set<Tile> getRandomTiles(int size, TileState.TileStateCategory category, Random random) {
        List<Tile> tiles = Lists.newArrayList();
        for (Tile tile : this.tiles)
            if (tile.getTileState().getCategory().equals(category))
                tiles.add(tile);
        Collections.shuffle(tiles, random);
        return Sets.newHashSet(tiles.subList(0, size));
    }

    public void removeTiles(Collection<Tile> tiles) {
        this.tiles.removeAll(tiles);
    }

    public void setLarkSuit(TileState.TileStateCategory larkSuit) {
        this.larkSuit = larkSuit;
    }

    public TileState.TileStateCategory getLarkSuit() {
        return larkSuit;
    }
}
