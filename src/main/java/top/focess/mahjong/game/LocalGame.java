package top.focess.mahjong.game;

import com.google.common.collect.Lists;
import top.focess.mahjong.game.data.GameData;
import top.focess.mahjong.game.data.TilesData;
import top.focess.mahjong.game.packet.FetchTilePacket;
import top.focess.mahjong.game.packet.GameSyncPacket;
import top.focess.mahjong.game.packet.GameTileActionPacket;
import top.focess.mahjong.game.remote.RemotePlayer;
import top.focess.mahjong.game.rule.MahjongRule;
import top.focess.mahjong.game.rule.manager.GameManager;
import top.focess.mahjong.game.tile.TileState;
import top.focess.mahjong.terminal.TerminalLauncher;
import top.focess.net.packet.Packet;
import top.focess.net.socket.FocessMultiSocket;
import top.focess.scheduler.FocessScheduler;
import top.focess.scheduler.Task;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

public class LocalGame extends Game {

	private static final FocessScheduler FOCESS_SCHEDULER = new FocessScheduler("GameTicker", true);
	private final FocessMultiSocket serverSocket;
	private final List<UUID> startPlayers = Lists.newArrayList();
	private Task task;
	private GameManager gameManager;

	public LocalGame(final FocessMultiSocket serverSocket, final MahjongRule rule) {
		super(rule);
		this.serverSocket = serverSocket;
	}

	private synchronized void countdown() {
		if (this.getGameState() != GameState.WAITING)
			return;
		if (this.getStartTime() == 0) {
			this.task.cancel();
			this.task = null;
			this.start();
			return;
		}
		this.countdownStartTime();
		this.syncPlayer();
	}

	@Override
	public void doTileAction(final GameTileActionPacket.TileAction tileAction, final Player player, final TileState... tileStates) {
		if (this.gameManager == null || this.getGameState() != GameState.PLAYING)
			return;
		this.gameManager.doTileAction(tileAction, this.startPlayers.indexOf(player.getId()), tileStates);
	}

	@Override
	public GameTileState getGameTileState() {
		if (this.gameManager == null || this.getGameState() != GameState.PLAYING)
			return null;
		return this.gameManager.getGameTileState();
	}

	public synchronized boolean join(final Player player) {
		if (player.getGame() == this && this.players.contains(player))
			return true;
		if (!this.getRule().checkPlayerSize(this.players.size() + 1))
			return false;
		if (player.getGame() != null || player.getPlayerState() != Player.PlayerState.WAITING)
			return false;
		if (this.getGameState() == GameState.PLAYING && !this.startPlayers.contains(player.getId()))
			return false;
		if (this.getGameState() == GameState.PLAYING)
			player.setPlayerState(Player.PlayerState.PLAYING);
		final List<Player> old = Lists.newArrayList(this.players);
		this.players.add(player);
		TerminalLauncher.change("players", this, old, this.players);
		player.setGame(this);
		this.syncOtherPlayer(player);
		return true;
	}

	private void syncOtherPlayer(final Player player) {
		for (final Player p : this.players) {
			if (p != player && p instanceof RemotePlayer) {
				final int clientId = ((RemotePlayer) p).getClientId();
				if (clientId == -1)
					throw new IllegalStateException("Remote player " + p.getName() + " has no client id");
				this.serverSocket.getReceiver().sendPacket(clientId, new GameSyncPacket(this.getPartGameData(p)));
			}
		}
	}

	public GameData getPartGameData(final Player player) {
		return this.getPartGameData(this.startPlayers.indexOf(player.getId()));
	}

	private synchronized GameData getPartGameData(final int player) {
		final TilesData tilesData;
		if (this.getGameState() == GameState.PLAYING && this.gameManager != null)
			tilesData = this.gameManager.getTilesData(player);
		else tilesData = null;
		return new GameData(this.getId(), this.getRule(), this.getGameState(), this.getStartTime(), this.getGameTime(), this.getCountdown(), tilesData, this.players.stream().map(Player::getPlayerData).toList());
	}

	@Override
	public void larkSuit(final RemotePlayer player, final TileState.TileStateCategory category) {
		if (this.gameManager == null || this.getGameState() != GameState.PLAYING)
			return;
		this.gameManager.larkSuit(this.startPlayers.indexOf(player.getId()), category);
	}

	public synchronized boolean leave(final Player player) {
		if (player.getGame() == null && !this.players.contains(player))
			return true;
		final List<Player> old = Lists.newArrayList(this.players);
		if (player.getGame() != this || !this.players.remove(player))
			return false;
		TerminalLauncher.change("players", this, old, this.players);
		player.setGame(null);
		player.setPlayerState(Player.PlayerState.WAITING);
		if (this.task != null) {
			this.task.cancel();
			this.task = null;
		}
		this.syncOtherPlayer(player);
		return true;
	}

	@Override
	public synchronized boolean ready(final Player player) {
		if (this.getGameState() != GameState.WAITING)
			return false;
		if (this.players.contains(player))
			if (player.getPlayerState() == Player.PlayerState.WAITING) {
				player.setPlayerState(Player.PlayerState.READY);
				if (this.players.stream().allMatch(p -> p.getPlayerState() == Player.PlayerState.READY)) {
					this.setStartTime(this.getRule().getReadyTime(this.players.size()));
					if (this.task != null)
						this.task.cancel();
					if (this.getStartTime() != -1)
						this.task = LocalGame.FOCESS_SCHEDULER.runTimer(this::countdown, Duration.ZERO, Duration.ofSeconds(1));
				}
				this.syncOtherPlayer(player);
				return true;
			} else return player.getPlayerState() == Player.PlayerState.READY;
		return false;
	}

	@Override
	public synchronized boolean unready(final Player player) {
		if (this.getGameState() != GameState.WAITING)
			return false;
		if (this.players.contains(player))
			if (player.getPlayerState() == Player.PlayerState.READY) {
				player.setPlayerState(Player.PlayerState.WAITING);
				if (this.task != null) {
					this.task.cancel();
					this.task = null;
				}
				this.syncOtherPlayer(player);
				return true;
			} else return player.getPlayerState() == Player.PlayerState.WAITING;
		return false;
	}

	public synchronized void end() {
		if (this.getGameState() != GameState.PLAYING)
			return;
		this.setGameState(GameState.WAITING);
		this.players.forEach(player -> player.setPlayerState(Player.PlayerState.WAITING));
		this.syncPlayer();
		this.startPlayers.clear();
	}

	private void syncPlayer() {
		this.syncOtherPlayer(null);
	}

	public Player getPlayer(final int index) {
		final UUID id = this.startPlayers.get(index);
		for (final Player player : this.players)
			if (player.getId().equals(id))
				return player;
		return null;
	}

	public UUID getPlayerId(final int player) {
		return this.startPlayers.get(player);
	}

	public void sendPacket(final Packet packet) {
		this.players.forEach(player -> {
			if (player instanceof RemotePlayer) {
				final int clientId = ((RemotePlayer) player).getClientId();
				if (clientId == -1)
					throw new IllegalStateException("Remote player " + player.getName() + " has no client id");
				this.serverSocket.getReceiver().sendPacket(clientId, packet);
			}
		});
	}

	public synchronized void start() {
		if (this.getGameState() != GameState.WAITING)
			return;
		if (!this.players.stream().allMatch(player -> player.getPlayerState() == Player.PlayerState.READY))
			return;
		this.setGameState(GameState.PLAYING);
		this.players.forEach(player -> player.setPlayerState(Player.PlayerState.PLAYING));
		this.startPlayers.clear();
		for (final Player player : this.players)
			this.startPlayers.add(player.getId());
		this.gameManager = this.getRule().getGameManager(this, this.players.size());
		this.setCountdown(this.gameManager.getCountdown());
		this.syncPlayer();
		this.task = LocalGame.FOCESS_SCHEDULER.runTimer(this::tick, Duration.ZERO, Duration.ofSeconds(1));
	}

	public synchronized void tick() {
		if (this.getGameState() != GameState.PLAYING)
			return;
		this.tickGameTime();
		this.gameManager.tick();
		this.setCountdown(this.gameManager.getCountdown());
		this.syncPlayer();
		if (this.gameManager.getGameTileState() == GameTileState.DISCARDING) {
			final Player player = this.getPlayer(this.gameManager.getCurrentPlayer());
			if (player != null)
				if (player instanceof LocalPlayer)
					TerminalLauncher.change("fetchTileState", player, null, this.gameManager.getCurrentTileState());
				else if (player instanceof RemotePlayer)
					this.serverSocket.getReceiver().sendPacket(((RemotePlayer) player).getClientId(), new FetchTilePacket(player.getId(), this.getId(), this.gameManager.getCurrentTileState()));
		}
	}

}
