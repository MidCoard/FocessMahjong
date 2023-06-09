package top.focess.mahjong.game.rule.manager;

import top.focess.mahjong.game.GameTileState;
import top.focess.mahjong.game.data.TilesData;
import top.focess.mahjong.game.packet.GameTileActionPacket;
import top.focess.mahjong.game.tile.TileState;

public abstract class GameManager {
	public abstract void doTileAction(GameTileActionPacket.TileAction tileAction, int i, TileState... tileStates);

	public abstract int getCountdown();

	public abstract int getCurrentPlayer();

	public abstract TileState getCurrentTileState();

	public abstract GameTileState getGameTileState();

	public abstract TilesData getTilesData(int player);

	public abstract void larkSuit(int player, TileState.TileStateCategory category);

	public abstract void tick();
}
