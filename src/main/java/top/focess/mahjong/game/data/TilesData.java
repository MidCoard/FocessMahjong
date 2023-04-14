package top.focess.mahjong.game.data;

import top.focess.mahjong.game.GameTileState;
import top.focess.mahjong.game.tile.TileState;

import java.util.List;
import java.util.UUID;

public record TilesData(int remainTiles, List<TileState> tileStates, GameTileState gameTileState,
                        List<TileState.TileStateCategory> larkSuits,
                        List<Integer> scores, List<List<TileState>> noDiscardTileStates,
                        List<List<TileState>> discardTileStates, UUID currentPlayerId, TileState currentTileState) {

}
