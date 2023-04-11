package top.focess.mahjong.game;

import top.focess.mahjong.game.data.PlayerData;
import top.focess.mahjong.game.remote.RemotePlayer;
import top.focess.mahjong.terminal.TerminalLauncher;

import java.util.UUID;

public class Player {

	private final UUID id;
	private final String name;

	private Game game;

	private PlayerState playerState = PlayerState.WAITING;

	public Player() {
		this(UUID.randomUUID());
	}

	public Player(final UUID id) {
		this(id, "Player" + id.toString().substring(0, 4));
	}

	public Player(final UUID id, final String name) {
		this.id = id;
		this.name = name;
	}

	public static Player getPlayer(final int clientId, final PlayerData playerData) {
		if (LocalPlayer.localPlayer.getId().equals(playerData.id()))
			return LocalPlayer.localPlayer;
		return RemotePlayer.getOrCreatePlayer(clientId, playerData);
	}

	public UUID getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public PlayerData getPlayerData() {
		return new PlayerData(this.id, this.name, this.playerState, null == this.game ? null : this.game.getId());
	}

	public PlayerState getPlayerState() {
		return this.playerState;
	}

	public void setPlayerState(final PlayerState playerState) {
		if (this.playerState != playerState) {
			TerminalLauncher.change("playerState", this, this.playerState, playerState);
			this.playerState = playerState;
		}
	}

	public boolean join(final Game game) {
		return game.join(this);
	}

	public boolean leave() {
		return null != this.getGame() && this.getGame().leave(this);
	}

	public Game getGame() {
		return this.game;
	}

	public void setGame(final Game game) {
		this.game = game;
	}

	@Override
	public String toString() {
		return "Player{" +
				"id=" + this.id +
				", name='" + this.name + '\'' +
				", playerState=" + this.playerState +
				'}' + super.toString();
	}

	public enum PlayerState {

		WAITING,
		READY,
		PLAYING
	}
}
