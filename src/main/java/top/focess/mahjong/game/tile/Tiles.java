package top.focess.mahjong.game.tile;

import com.google.common.collect.Sets;

import java.util.List;
import java.util.Set;

public class Tiles {


    private final Tile[] tiles;

    private int pointer = 0;

    public Tiles(int tileSize) {
        this.tiles = new Tile[tileSize];
        for (int i = 0; i < tileSize; i++)
            this.tiles[i] = new Tile(i + 1);
    }

    public Tile getTile(int i) {
        return this.tiles[i];
    }

    public Set<Tile> fetch(int size) {
        if (this.pointer + size > this.tiles.length)
            throw new IndexOutOfBoundsException("The pointer is out of the tileStates");
        Set<Tile> tiles = Sets.newHashSet();
        for (int i = 0; i < size; i++)
            tiles.add(this.tiles[this.pointer++]);
        return tiles;
    }

    public int getRemainSize() {
        return this.tiles.length - this.pointer;
    }

    public Tile fetch() {
        return this.tiles[this.pointer++];
    }
}
