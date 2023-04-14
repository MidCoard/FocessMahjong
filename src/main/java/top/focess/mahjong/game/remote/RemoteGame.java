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
			if (player == null)
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
		if (game != null)
			throw new IllegalArgumentException("Game " + data.id() + " is not a remote game");
		return new RemoteGame(socket, data);
	}

	@Override
	public void doTileAction(final GameTileActionPacket.TileAction tileAction, final Player player, final TileState... tileStates) {
		this.socket.getReceiver().sendPacket(new GameTileActionPacket(player.getId(), this.getId(), tileAction, tileStates));
	}

	@Override
	public GameTileState getGameTileState() {
		if (this.getTilesData() == null)
			return null;
		return this.getTilesData().gameTileState();
	}

	@Override
	public synchronized boolean join(final Player player) {
		final GameActionStatusPacket.GameActionStatus status = this.gameRequester.request("join",
				() -> this.socket.getReceiver().sendPacket(new GameActionPacket(player.getId(), this.getId(), GameActionPacket.GameAction.JOIN)),
				player.getId());
		if (status == GameActionStatusPacket.GameActionStatus.SUCCESS) {
			this.syncGameData(player);
			if (this.getGameState() == GameState.PLAYING)
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
		if (status == GameActionStatusPacket.GameActionStatus.SUCCESS) {
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
		if (status == GameActionStatusPacket.GameActionStatus.SUCCESS) {
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
		if (status == GameActionStatusPacket.GameActionStatus.SUCCESS) {
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

	public void remove() {
		Game.GAMES.remove(this.getId());
	}
}
