package top.focess.mahjong.game.remote;

import com.google.common.collect.Lists;
import top.focess.mahjong.game.Game;
import top.focess.mahjong.game.Player;
import top.focess.mahjong.game.data.PlayerData;

import java.util.*;

public class RemotePlayer extends Player {

	private static final Map<UUID, RemotePlayer> PLAYERS = Collections.synchronizedMap(new WeakHashMap<>());

	private int clientId;

	public RemotePlayer(final int clientId, final PlayerData playerData) {
		super(playerData.id(), playerData.name());
		this.clientId = clientId;
		this.update(playerData);
	}

	public RemotePlayer update(final PlayerData playerData) {
		if (!this.getId().equals(playerData.id()) || !this.getName().equals(playerData.name()))
			throw new IllegalArgumentException("The player base data is not match!");
		this.setPlayerState(playerData.playerState());
		this.setGame(playerData.gameId() == null ? null : Game.getGame(playerData.gameId()));
		return this;
	}

	public static synchronized RemotePlayer getOrCreatePlayer(final int clientId, final PlayerData playerData) {
		if (RemotePlayer.PLAYERS.containsKey(playerData.id())) {
			assert clientId != -1;//todo
			final RemotePlayer player = RemotePlayer.PLAYERS.get(playerData.id());
			if (player.clientId == -1 && clientId != -1)
				player.clientId = clientId;
			if (player.clientId == clientId || clientId == -1)
				return player.update(playerData);
			return null;
		}
		final RemotePlayer player = new RemotePlayer(clientId, playerData);
		RemotePlayer.PLAYERS.put(playerData.id(), player);
		return player;
	}

	public static RemotePlayer getPlayer(final int clientId, final UUID id) {
		final RemotePlayer player = RemotePlayer.PLAYERS.get(id);
		if (player == null)
			return null;
		if (player.clientId == clientId || clientId == -1)
			return player;
		return null;
	}

	public static List<RemotePlayer> removePlayers(final int clientId) {
		final List<RemotePlayer> ret = Lists.newArrayList();
		for (final Map.Entry<UUID, RemotePlayer> entry : RemotePlayer.PLAYERS.entrySet())
			if (entry.getValue().clientId == clientId) {
				ret.add(entry.getValue());
				RemotePlayer.PLAYERS.remove(entry.getKey());
			}
		return ret;
	}

	public int getClientId() {
		return this.clientId;
	}
}
