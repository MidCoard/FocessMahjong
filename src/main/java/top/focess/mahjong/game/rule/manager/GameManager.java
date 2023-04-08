package top.focess.mahjong.game.rule.manager;

import top.focess.mahjong.game.GameTileState;
import top.focess.mahjong.game.LocalGame;
import top.focess.mahjong.game.data.TilesData;
import top.focess.mahjong.game.tile.TileState;

public abstract class GameManager {
    public abstract void tick();

    public abstract int getCountdown();

    public abstract TilesData getTilesData(int player);

    public abstract GameTileState getGameTileState();

    public abstract void doTileAction(LocalGame.TileAction tileAction, int i, Object... objects);

    public abstract int getCurrent();

    public abstract TileState getCurrentTileState();
}
