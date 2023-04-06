package top.focess.mahjong.game.tile;

public class Tile {

    private final int number;

    private TileState tileState;

    public Tile(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }
}
