package top.focess.mahjong.terminal;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import top.focess.mahjong.game.Game;
import top.focess.mahjong.game.GameTileState;
import top.focess.mahjong.game.LocalPlayer;
import top.focess.mahjong.game.Player;
import top.focess.mahjong.game.packet.GameTileActionPacket;
import top.focess.mahjong.game.tile.TileState;
import top.focess.mahjong.terminal.command.CommandLine;
import top.focess.mahjong.terminal.listener.TerminalGameListener;
import top.focess.mahjong.terminal.listener.TerminalPlayerListener;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class TerminalLauncher {

	private static final Map<String, TerminalGameListener<?>> GAME_LISTENERS = Maps.newConcurrentMap();

	private static final Map<String, TerminalPlayerListener<?>> PLAYER_LISTENERS = Maps.newConcurrentMap();

	static {
		TerminalLauncher.registerGameChangeListener("gameState", Game.GameState.class, (game, oldValue, newValue) -> {
			if (Game.GameState.PLAYING == newValue)
				System.out.println("Game " + game + " started!");
			else if (Game.GameState.WAITING == newValue)
				System.out.println("Game " + game + " stopped!");
		});
		TerminalLauncher.registerGameChangeListener("startTime", Integer.class, (game, oldValue, newValue) -> System.out.println("Game " + game.getId() + " start time changed to " + newValue));
		TerminalLauncher.registerGameChangeListener("countdown", Integer.class, (game, oldValue, newValue) -> System.out.println("Game " + game.getId() + " countdown changed to " + newValue));
		TerminalLauncher.registerGameChangeListener("gameTileState", GameTileState.class, (game, oldValue, newValue) -> {
			if (GameTileState.CHANGING_3_TILES == newValue)
				System.out.println("We should select three tileStates to change to other tileStates!");
			else if (GameTileState.FINISHED == newValue)
				System.out.println("Game " + game.getId() + " finished!");
			else if (GameTileState.CONDITION == newValue)
				System.out.println("Game " + game.getId() + " condition! You can pung, kong, hu the tile!");
			else if (GameTileState.CONDITION_HU == newValue)
				System.out.println("Game " + game.getId() + " condition! You can hu the tile!");
			else if (GameTileState.LARKING_1_SUIT == newValue)
				System.out.println("Game " + game.getId() + " larking 1 suit! You can lark the tile!");
			else if (GameTileState.DISCARDING == newValue)
				System.out.println("Game " + game.getId() + " discarding! Someone should discard a tile!");
			else if (GameTileState.SHUFFLING == newValue)
				System.out.println("Game " + game.getId() + " shuffling! You can't do anything!");
			else if (GameTileState.WAITING == newValue)
				System.out.println("Game " + game.getId() + " waiting! You can't do anything!");
			else if (GameTileState.WAITING_HU == newValue)
				System.out.println("Game " + game.getId() + " waiting hu! You can't do anything!");
		});
		TerminalLauncher.registerGameChangeListener("players", List.class, (game, oldValue, newValue) -> {
			final List<Player> joinPlayers = Lists.newArrayList();
			final List<Player> leavePlayers = Lists.newArrayList();
			for (final Object o : oldValue)
				if (!newValue.contains(o))
					leavePlayers.add((Player) o);
			for (final Object o : newValue)
				if (!oldValue.contains(o))
					joinPlayers.add((Player) o);
			for (final Player player : joinPlayers)
				System.out.println("Player " + player + " join game " + game);
			for (final Player player : leavePlayers)
				System.out.println("Player " + player + " leave game " + game);
		});



		for (final GameTileActionPacket.TileAction tileAction : GameTileActionPacket.TileAction.values())
			TerminalLauncher.registerPlayerChangeListener(tileAction.getName() + "_notice", List.class, (player, oldValue, newValue) -> {
				if (null != player.getGame())
					System.out.println("Notice: Player " + player + " " + tileAction.getName() + " " + newValue + " in game " + player.getGame());
			});

		for (final GameTileActionPacket.TileAction tileAction : GameTileActionPacket.TileAction.values())
			TerminalLauncher.registerPlayerChangeListener(tileAction.getName() + "_confirm", List.class, (player, oldValue, newValue) -> {
				if (null != player.getGame())
					System.out.println("Confirm: Player " + player + " " + tileAction.getName() + " " + newValue + " in game " + player.getGame());
			});


		TerminalLauncher.registerPlayerChangeListener("playerState", Player.PlayerState.class, (player, oldValue, newValue) -> {
			if (Player.PlayerState.READY == newValue && Player.PlayerState.WAITING == oldValue && null != player.getGame())
				System.out.println("Player " + player + " ready game " + player.getGame());
			else if (Player.PlayerState.WAITING == newValue && Player.PlayerState.READY == oldValue && null != player.getGame())
				System.out.println("Player " + player + " unready game " + player.getGame());
		});
		TerminalLauncher.registerPlayerChangeListener("fetchTileState", TileState.class, (player, oldValue, newValue) -> {
			if (LocalPlayer.localPlayer.getId().equals(player.getId()))
				System.out.println("Player " + player + " fetch tile " + newValue + " in game " + player.getGame());
			else throw new RuntimeException("Player " + player + " fetch tile " + newValue + " in game " + player.getGame());
		});
	}

	public static <T> void change(final String arg, final Game game, final T oldValue, final T newValue) {
		final TerminalGameListener<T> terminalGameListener = (TerminalGameListener<T>) TerminalLauncher.GAME_LISTENERS.get(arg);
		if (null != terminalGameListener)
			terminalGameListener.onChanged(game, oldValue, newValue);
	}

	public static <T> void change(final String arg, final Player player, final T oldValue, final T newValue) {
		final TerminalPlayerListener<T> terminalPlayerListener = (TerminalPlayerListener<T>) TerminalLauncher.PLAYER_LISTENERS.get(arg);
		if (null != terminalPlayerListener)
			terminalPlayerListener.onChanged(player, oldValue, newValue);
	}

	public static void launch() {
		final Scanner scanner = new Scanner(System.in);
		while (scanner.hasNextLine()) {
			final String nextLine = scanner.nextLine();
			CommandLine.execute(nextLine);
		}
	}

	public static <T> void registerGameChangeListener(final String arg, final Class<T> ignored, final TerminalGameListener<T> listener) {
		TerminalLauncher.GAME_LISTENERS.put(arg, listener);
	}

	public static <T> void registerPlayerChangeListener(final String arg, final Class<T> ignored, final TerminalPlayerListener<T> listener) {
		TerminalLauncher.PLAYER_LISTENERS.put(arg, listener);
	}
}
