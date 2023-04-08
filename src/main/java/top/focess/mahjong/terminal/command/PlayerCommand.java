package top.focess.mahjong.terminal.command;

import org.jetbrains.annotations.NotNull;
import top.focess.command.Command;
import top.focess.command.CommandArgument;
import top.focess.command.CommandResult;
import top.focess.command.CommandSender;
import top.focess.mahjong.game.Game;
import top.focess.mahjong.game.LocalPlayer;
import top.focess.mahjong.game.Player;

import java.util.List;
import java.util.UUID;

public class PlayerCommand extends Command {

    public PlayerCommand() {
        super("player", "p");
    }

    @Override
    public void init() {
        this.addExecutor((sender, dataCollection, ioHandler) -> {
            Player player = LocalPlayer.localPlayer;
            System.out.println("Local player is " + player.getName() + ".");
            return CommandResult.ALLOW;
        }, CommandArgument.of("local"));

        this.addExecutor((sender, dataCollection, ioHandler) -> {
            UUID gameId = UUID.fromString(dataCollection.get());
            Player player = LocalPlayer.localPlayer;
            Game game = Game.getGame(gameId);
            if (game == null) {
                System.out.println("Game " + gameId + " not found!");
                return CommandResult.REFUSE;
            }
            boolean flag = game.join(player);
            if (flag)
                System.out.println("Player " + player.getName() + " joined game " + game.getId() + ".");
            else
                System.out.println("Player " + player.getName() + " can't join game " + game.getId() + ".");
            return CommandResult.ALLOW;
        }, CommandArgument.of("join"), CommandArgument.ofString());

        this.addExecutor((sender, dataCollection, ioHandler) -> {
            UUID gameId = UUID.fromString(dataCollection.get());
            Player player = LocalPlayer.localPlayer;
            Game game = Game.getGame(gameId);
            if (game == null) {
                System.out.println("Game " + gameId + " not found!");
                return CommandResult.REFUSE;
            }
            boolean flag = game.leave(player);
            if (flag)
                System.out.println("Player " + player.getName() + " left game " + game.getId() + ".");
            else
                System.out.println("Player " + player.getName() + " can't leave game " + game.getId() + ".");
            return CommandResult.ALLOW;
        }, CommandArgument.of("leave"), CommandArgument.ofString());

        this.addExecutor((sender, dataCollection, ioHandler) -> {
            UUID gameId = UUID.fromString(dataCollection.get());
            Player player = LocalPlayer.localPlayer;
            Game game = Game.getGame(gameId);
            if (game == null) {
                System.out.println("Game " + gameId + " not found!");
                return CommandResult.REFUSE;
            }
            boolean flag = game.ready(player);
            if (flag)
                System.out.println("Player " + player.getName() + " is ready in game " + game.getId() + ".");
            else
                System.out.println("Player " + player.getName() + " can't ready in game " + game.getId() + ".");
            return CommandResult.ALLOW;
        }, CommandArgument.of("ready"), CommandArgument.ofString());

        this.addExecutor((sender, dataCollection, ioHandler) -> {
            UUID gameId = UUID.fromString(dataCollection.get());
            Player player = LocalPlayer.localPlayer;
            Game game = Game.getGame(gameId);
            if (game == null) {
                System.out.println("Game " + gameId + " not found!");
                return CommandResult.REFUSE;
            }
            boolean flag = game.unready(player);
            if (flag)
                System.out.println("Player " + player.getName() + " is unready in game " + game.getId() + ".");
            else
                System.out.println("Player " + player.getName() + " can't unready in game " + game.getId() + ".");
            return CommandResult.ALLOW;
        }, CommandArgument.of("unready"), CommandArgument.ofString());
    }

    @Override
    public @NotNull List<String> usage(CommandSender sender) {
        return List.of("player local", "player join <game>", "player leave <game>", "player ready <game>", "player unready <game>");
    }
}
