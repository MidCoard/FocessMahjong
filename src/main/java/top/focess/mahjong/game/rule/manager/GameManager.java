package top.focess.mahjong.game.rule.manager;

import top.focess.mahjong.game.GameTileState;
import top.focess.mahjong.game.data.TilesData;
import top.focess.mahjong.game.packet.GameTileActionPacket;
import top.focess.mahjong.game.tile.Tile;
import top.focess.mahjong.game.tile.TileState;

public abstract class GameManager {
    public abstract void tick();

    public abstract int getCountdown();

    public abstract TilesData getTilesData(int player);

    public abstract GameTileState getGameTileState();

    public abstract void doTileAction(GameTileActionPacket.TileAction tileAction, int i, TileState... tileStates);

    public abstract int getCurrentPlayer();

    public abstract TileState getCurrentTileState();
}
