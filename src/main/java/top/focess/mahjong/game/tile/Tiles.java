package top.focess.mahjong.game.tile;

public class Tiles {


    private final Tile[] tiles;

    public Tiles(int tileSize) {
        this.tiles = new Tile[tileSize];
        for (int i = 0; i < tileSize; i++)
            this.tiles[i] = new Tile(i + 1);
    }
}
