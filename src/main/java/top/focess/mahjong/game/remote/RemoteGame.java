package top.focess.mahjong.game.remote;

import com.google.common.collect.Lists;
import top.focess.mahjong.game.Game;
import top.focess.mahjong.game.GameTileState;
import top.focess.mahjong.game.Player;
import top.focess.mahjong.game.data.GameData;
import top.focess.mahjong.game.data.PlayerData;
import top.focess.mahjong.game.data.TilesData;
import top.focess.mahjong.game.packet.*;
import top.focess.mahjong.game.tile.TileState;
import top.focess.mahjong.terminal.TerminalLauncher;
import top.focess.net.socket.FocessClientSocket;

import java.util.List;

public class RemoteGame extends Game {
	private final FocessClientSocket socket;

	private TilesData tilesData;

	public RemoteGame(final FocessClientSocket socket, final GameData data) {
		super(data.id(), data.rule());
		this.socket = socket;
		this.update(data);
	}

	public synchronized void update(final GameData gameData) {
		if (!this.getId().equals(gameData.id()) || this.getRule() != gameData.rule())
			throw new IllegalArgumentException("The game base data is not match");
		this.setGameState(gameData.gameState());
		this.setStartTime(gameData.startTime());
		this.setGameTime(gameData.gameTime());
		this.setCountdown(gameData.countdown());

		this.setTilesData(gameData.tilesData());
		final List<Player> temp = Lists.newArrayList();
		for (final PlayerData playerData : gameData.playerData()) {
			final Player player = Player.getPlayer(-1, playerData);
			if (null == player)
				throw new IllegalArgumentException("The player is not exist.");
			temp.add(player);
		}
		TerminalLauncher.change("players", this, this.players, temp);
		this.players.clear();
		this.players.addAll(temp);
	}

	public static RemoteGame getOrCreateGame(final FocessClientSocket socket, final GameData data) {
		final Game game = Game.getGame(data.id());
		if (game instanceof RemoteGame) {
			((RemoteGame) game).update(data);
			return (RemoteGame) game;
		}
		if (null != game)
			throw new IllegalArgumentException("Game " + data.id() + " is not a remote game");
		return new RemoteGame(socket, data);
	}

	@Override
	public void doTileAction(final GameTileActionPacket.TileAction tileAction, final Player player, final TileState... tileStates) {
		this.socket.getReceiver().sendPacket(new GameTileActionPacket(player.getId(), this.getId(), tileAction, tileStates));
	}

	@Override
	public GameTileState getGameTileState() {
		if (null == this.tilesData)
			return null;
		return this.tilesData.gameTileState();
	}

	@Override
	public synchronized boolean join(final Player player) {
		final GameActionStatusPacket.GameActionStatus status = this.gameRequester.request("join",
				() -> this.socket.getReceiver().sendPacket(new GameActionPacket(player.getId(), this.getId(), GameActionPacket.GameAction.JOIN)),
				player.getId());
		if (GameActionStatusPacket.GameActionStatus.SUCCESS == status) {
			this.syncGameData(player);
			if (GameState.PLAYING == this.getGameState())
				player.setPlayerState(Player.PlayerState.PLAYING);
			player.setGame(this);
			return true;
		}
		return false;
	}

	@Override
	public void larkSuit(final RemotePlayer player, final TileState.TileStateCategory category) {
		this.socket.getReceiver().sendPacket(new LarkSuitPacket(player.getId(), this.getId(), category));
	}

	@Override
	public synchronized boolean leave(final Player player) {
		final GameActionStatusPacket.GameActionStatus status = this.gameRequester.request("leave",
				() -> this.socket.getReceiver().sendPacket(new GameActionPacket(player.getId(), this.getId(), GameActionPacket.GameAction.LEAVE)),
				player.getId());
		if (GameActionStatusPacket.GameActionStatus.SUCCESS == status) {
			player.setGame(null);
			player.setPlayerState(Player.PlayerState.WAITING);
			return true;
		}
		return false;
	}

	@Override
	public synchronized boolean ready(final Player player) {
		final GameActionStatusPacket.GameActionStatus status = this.gameRequester.request("ready",
				() -> this.socket.getReceiver().sendPacket(new GameActionPacket(player.getId(), this.getId(), GameActionPacket.GameAction.READY)),
				player.getId());
		if (GameActionStatusPacket.GameActionStatus.SUCCESS == status) {
			player.setPlayerState(Player.PlayerState.READY);
			return true;
		}
		return false;
	}

	@Override
	public synchronized boolean unready(final Player player) {
		final GameActionStatusPacket.GameActionStatus status = this.gameRequester.request("unready",
				() -> this.socket.getReceiver().sendPacket(new GameActionPacket(player.getId(), this.getId(), GameActionPacket.GameAction.UNREADY)),
				player.getId());
		if (GameActionStatusPacket.GameActionStatus.SUCCESS == status) {
			player.setPlayerState(Player.PlayerState.WAITING);
			return true;
		}
		return false;
	}

	public synchronized void syncGameData(final Player player) {
		final GameData gameData = this.gameRequester.request("sync",
				() -> this.socket.getReceiver().sendPacket(new SyncGamePacket(player.getId(), this.getId())),
				this.getId());
		this.update(gameData);
	}

	public TilesData getTilesData() {
		return this.tilesData;
	}

	private void setTilesData(final TilesData tilesData) {
		if (null != this.tilesData && null != tilesData) {
			if (this.tilesData.remainTiles() != tilesData.remainTiles())
				TerminalLauncher.change("remainTiles", this, this.tilesData.remainTiles(), tilesData.remainTiles());
			if (!this.tilesData.tileStates().equals(tilesData.tileStates()))
				TerminalLauncher.change("tileStates", this, this.tilesData.tileStates(), tilesData.tileStates());
			if (this.tilesData.gameTileState() != tilesData.gameTileState())
				TerminalLauncher.change("gameTileState", this, this.tilesData.gameTileState(), tilesData.gameTileState());
			if (!this.tilesData.larkSuits().equals(tilesData.larkSuits()))
				TerminalLauncher.change("larkSuits", this, this.tilesData.larkSuits(), tilesData.larkSuits());
			if (!this.tilesData.scores().equals(tilesData.scores()))
				TerminalLauncher.change("scores", this, this.tilesData.scores(), tilesData.scores());
			if (!this.tilesData.discardTileStates().equals(tilesData.discardTileStates()))
				TerminalLauncher.change("discardTileStates", this, this.tilesData.discardTileStates(), tilesData.discardTileStates());
			if (!this.tilesData.currentPlayerId().equals(tilesData.currentPlayerId()))
				TerminalLauncher.change("currentPlayerId", this, this.tilesData.currentPlayerId(), tilesData.currentPlayerId());
			if (this.tilesData.currentTileState() != tilesData.currentTileState())
				TerminalLauncher.change("currentTileState", this, this.tilesData.currentTileState(), tilesData.currentTileState());
		} else if (null != this.tilesData || null != tilesData) {
			TerminalLauncher.change("remainTiles", this, null == this.tilesData ? null : this.tilesData.remainTiles(), null == tilesData ? null : tilesData.remainTiles());
			TerminalLauncher.change("tileStates", this, null == this.tilesData ? null : this.tilesData.tileStates(), null == tilesData ? null : tilesData.tileStates());
			TerminalLauncher.change("gameTileState", this, null == this.tilesData ? null : this.tilesData.gameTileState(), null == tilesData ? null : tilesData.gameTileState());
			TerminalLauncher.change("larkSuits", this, null == this.tilesData ? null : this.tilesData.larkSuits(), null == tilesData ? null : tilesData.larkSuits());
			TerminalLauncher.change("scores", this, null == this.tilesData ? null : this.tilesData.scores(), null == tilesData ? null : tilesData.scores());
			TerminalLauncher.change("discardTileStates", this, null == this.tilesData ? null : this.tilesData.discardTileStates(), null == tilesData ? null : tilesData.discardTileStates());
			TerminalLauncher.change("currentPlayerId", this, null == this.tilesData ? null : this.tilesData.currentPlayerId(), null == tilesData ? null : tilesData.currentPlayerId());
		}
		this.tilesData = tilesData;
	}

	public void remove() {
		Game.GAMES.remove(this.getId());
	}
}
