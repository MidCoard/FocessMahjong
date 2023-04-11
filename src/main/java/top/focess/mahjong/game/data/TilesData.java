package top.focess.mahjong.game.data;

import top.focess.mahjong.game.GameTileState;
import top.focess.mahjong.game.tile.TileState;

import java.util.List;

public record TilesData(int remainTiles, List<TileState> tileStates, GameTileState gameTileState, List<TileState.TileStateCategory> larkSuits,
                        List<Integer> scores, List<List<TileState>> discardTileStates) {

}
