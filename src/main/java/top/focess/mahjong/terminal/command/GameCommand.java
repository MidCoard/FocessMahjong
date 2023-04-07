package top.focess.mahjong.terminal.command;

import org.jetbrains.annotations.NotNull;
import top.focess.command.Command;
import top.focess.command.CommandArgument;
import top.focess.command.CommandResult;
import top.focess.command.CommandSender;
import top.focess.mahjong.Launcher;
import top.focess.mahjong.game.Game;
import top.focess.mahjong.game.Player;
import top.focess.mahjong.game.rule.MahjongRule;
import top.focess.mahjong.terminal.command.converter.MahjongRuleConverter;

import java.util.List;
import java.util.UUID;

public class GameCommand extends Command {

    public GameCommand() {
        super("game", "g");
    }

    @Override
    public void init() {
        this.addExecutor((sender, dataCollection, ioHandler) -> {
            MahjongRule rule = dataCollection.get(MahjongRule.class);
            Game game = Launcher.DEFAULT_LAUNCHER.createGame(rule);
            System.out.println("Game " + game.getId() + " created!");
            return CommandResult.ALLOW;
        }, CommandArgument.of("create"), CommandArgument.of(MahjongRuleConverter.MAHJONG_RULE_CONVERTER));

        this.addExecutor((sender, dataCollection, ioHandler) -> {
            UUID gameId = UUID.fromString(dataCollection.get());
            UUID playerId = UUID.fromString(dataCollection.get());
            Game game = Game.getGame(gameId);
            Player player = Player.getPlayer(playerId);
            if (game == null || player == null) {
                System.out.println("Game or player not found!");
                return CommandResult.REFUSE;
            }
            boolean flag = game.join(player);
            if (flag)
                System.out.println("Player " + player.getId() + " joined game " + game.getId() + "!");
            else
                System.out.println("Player " + player.getId() + " can't join game " + game.getId() + "!");
            return CommandResult.ALLOW;
        }, CommandArgument.of("join"), CommandArgument.ofString(), CommandArgument.ofString());

        this.addExecutor((sender, dataCollection, ioHandler) -> {
            UUID gameId = UUID.fromString(dataCollection.get());
            UUID playerId = UUID.fromString(dataCollection.get());
            Game game = Game.getGame(gameId);
            Player player = Player.getPlayer(playerId);
            if (game == null || player == null) {
                System.out.println("Game or player not found!");
                return CommandResult.REFUSE;
            }
            boolean flag = game.leave(player);
            if (flag)
                System.out.println("Player " + player.getId() + " left game " + game.getId() + "!");
            else
                System.out.println("Player " + player.getId() + " can't leave game " + game.getId() + "!");
            return CommandResult.ALLOW;
        }, CommandArgument.of("leave"), CommandArgument.ofString(), CommandArgument.ofString());

        this.addExecutor((sender, dataCollection, ioHandler) -> {
            UUID gameId = UUID.fromString(dataCollection.get());
            UUID playerId = UUID.fromString(dataCollection.get());
            Game game = Game.getGame(gameId);
            Player player = Player.getPlayer(playerId);
            if (game == null || player == null) {
                System.out.println("Game or player not found!");
                return CommandResult.REFUSE;
            }
            boolean flag = game.ready(player);
            if (flag)
                System.out.println("Player " + player.getId() + " is ready in game " + game.getId() + "!");
            else
                System.out.println("Player " + player.getId() + " can't ready in game " + game.getId() + "!");
            return CommandResult.ALLOW;
        }, CommandArgument.of("ready"), CommandArgument.ofString(), CommandArgument.ofString());

        this.addExecutor((sender, dataCollection, ioHandler) -> {
            UUID gameId = UUID.fromString(dataCollection.get());
            UUID playerId = UUID.fromString(dataCollection.get());
            Game game = Game.getGame(gameId);
            Player player = Player.getPlayer(playerId);
            if (game == null || player == null) {
                System.out.println("Game or player not found!");
                return CommandResult.REFUSE;
            }
            boolean flag = game.unready(player);
            if (flag)
                System.out.println("Player " + player.getId() + " is unready in game " + game.getId() + "!");
            else
                System.out.println("Player " + player.getId() + " can't unready in game " + game.getId() + "!");
            return CommandResult.ALLOW;
        }, CommandArgument.of("unready"), CommandArgument.ofString(), CommandArgument.ofString());
    }

    @Override
    public @NotNull List<String> usage(CommandSender sender) {
        return List.of("create <name>");
    }
}
