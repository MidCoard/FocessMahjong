package top.focess.mahjong.game.tile;

import com.google.common.collect.Comparators;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;

public class Tile implements Comparable<Tile> {

    private final int number;

    private TileState tileState;

    public Tile(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    public void setTileState(TileState tileState) {
        this.tileState = tileState;
    }

    public TileState getTileState() {
        return tileState;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tile tile = (Tile) o;

        return number == tile.number;
    }

    @Override
    public int hashCode() {
        return number;
    }

    @Override
    public int compareTo(@NotNull Tile o) {
        return this.getTileState().compareTo(o.getTileState());
    }
}
