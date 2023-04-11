package top.focess.mahjong.terminal;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import top.focess.mahjong.game.Game;
import top.focess.mahjong.game.GameTileState;
import top.focess.mahjong.game.Player;
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
			if (GameTileState.CHANGING_3_TILES == newValue) {
				System.out.println("We should select three tileStates to change to other tileStates!");
			}
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


		TerminalLauncher.registerPlayerChangeListener("playerState", Player.PlayerState.class, (player, oldValue, newValue) -> {
			if (Player.PlayerState.READY == newValue && Player.PlayerState.WAITING == oldValue && null != player.getGame())
				System.out.println("Player " + player + " ready game " + player.getGame());
			else if (Player.PlayerState.WAITING == newValue && Player.PlayerState.READY == oldValue && null != player.getGame())
				System.out.println("Player " + player + " unready game " + player.getGame());
		});
	}

	public static void launch() {
		final Scanner scanner = new Scanner(System.in);
		while (scanner.hasNextLine()) {
			final String nextLine = scanner.nextLine();
			CommandLine.execute(nextLine);
		}
	}

	public static <T> void change(final String arg, final Game game, final T oldValue, final T newValue) {
		final TerminalGameListener<T> terminalGameListener = (TerminalGameListener<T>) TerminalLauncher.GAME_LISTENERS.get(arg);
		if (null != terminalGameListener)
			terminalGameListener.onChanged(game, oldValue, newValue);
	}

	public static <T> void registerGameChangeListener(final String arg, final Class<T> ignored, final TerminalGameListener<T> listener) {
		TerminalLauncher.GAME_LISTENERS.put(arg, listener);
	}

	public static <T> void registerPlayerChangeListener(final String arg, final Class<T> ignored, final TerminalPlayerListener<T> listener) {
		TerminalLauncher.PLAYER_LISTENERS.put(arg, listener);
	}

	public static <T> void change(final String arg, final Player player, final T oldValue, final T newValue) {
		final TerminalPlayerListener<T> terminalPlayerListener = (TerminalPlayerListener<T>) TerminalLauncher.PLAYER_LISTENERS.get(arg);
		if (null != terminalPlayerListener)
			terminalPlayerListener.onChanged(player, oldValue, newValue);
	}
}
