package top.focess.mahjong.game.tile;

import org.jetbrains.annotations.NotNull;

public class Tile implements Comparable<Tile> {

    public static final int NORMAL_KONG_TILE = 0b1;
    public static final int AFTER_KONG_FETCHED_TILE = 0b10;
    public static final int AFTER_KONG_DISCARDED_TILE = 0b100;

    public static final int KONG_TILE = 0b1000;
    public static final int PUNG_TILE = 0b10000;
    public static final int HU_TILE = 0b100000;


    private final int number;

    private TileState tileState;

    private int detail = 0;

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

    public void addDetail(int detail) {
        this.detail |= detail;
    }

    public boolean isDetail(int detail) {
        return (this.detail & detail) != 0;
    }
}
