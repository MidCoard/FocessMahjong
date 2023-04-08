package top.focess.mahjong.terminal;

import com.google.common.collect.Maps;
import top.focess.mahjong.game.Game;
import top.focess.mahjong.game.Player;
import top.focess.mahjong.terminal.command.CommandLine;
import top.focess.mahjong.terminal.listener.TerminalGameListener;
import top.focess.mahjong.terminal.listener.TerminalPlayerListener;

import java.util.Map;
import java.util.Scanner;

public class TerminalLauncher {

    private static final Map<String, TerminalGameListener<?>> GAME_LISTENERS = Maps.newConcurrentMap();

    private static final Map<String, TerminalPlayerListener<?>> PLAYER_LISTENERS = Maps.newConcurrentMap();

    static {
        registerGameChangeListener("gameState", Game.GameState.class, (game, oldValue, newValue) -> {
            if (newValue == Game.GameState.PLAYING)
                System.out.println("Game " + game.getId() + " started!");
            else if (newValue == Game.GameState.WAITING)
                System.out.println("Game " + game.getId() + " stopped!");
        });
        registerGameChangeListener("startTime", Integer.class, (game, oldValue, newValue) -> System.out.println("Game " + game.getId() + " start time changed to " + newValue));



        registerPlayerChangeListener("playerState", Player.PlayerState.class, (player, oldValue, newValue) -> {
            if (newValue == Player.PlayerState.READY && oldValue == Player.PlayerState.WAITING && player.getGame() != null)
                System.out.println("Player " + player.getId() + " ready game " + player.getGame().getId());
            else if (newValue == Player.PlayerState.WAITING && oldValue == Player.PlayerState.READY && player.getGame() != null)
                System.out.println("Player " + player.getId() + " unready game " + player.getGame().getId());
        });
    }

    public static void launch() {
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String nextLine = scanner.nextLine();
            CommandLine.execute(nextLine);
        }
    }

    public static <T> void change(String arg, Game game, T oldValue, T newValue) {
        TerminalGameListener<T> terminalGameListener = (TerminalGameListener<T>) GAME_LISTENERS.get(arg);
        if (terminalGameListener != null)
            terminalGameListener.onChanged(game, oldValue, newValue);
    }

    public static <T> void registerGameChangeListener(String arg, Class<T> ignored, TerminalGameListener<T> listener) {
        GAME_LISTENERS.put(arg, listener);
    }

    public static <T> void registerPlayerChangeListener(String arg, Class<T> ignored, TerminalPlayerListener<T> listener) {
        PLAYER_LISTENERS.put(arg, listener);
    }

    public static <T> void change(String arg, Player player, T oldValue, T newValue) {
        TerminalPlayerListener<T> terminalPlayerListener = (TerminalPlayerListener<T>) PLAYER_LISTENERS.get(arg);
        if (terminalPlayerListener != null)
            terminalPlayerListener.onChanged(player, oldValue, newValue);
    }
}
