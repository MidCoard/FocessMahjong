package top.focess.mahjong.terminal.command;

import org.jetbrains.annotations.NotNull;
import top.focess.command.Command;
import top.focess.command.CommandArgument;
import top.focess.command.CommandResult;
import top.focess.command.CommandSender;
import top.focess.mahjong.Launcher;
import top.focess.mahjong.game.Game;
import top.focess.mahjong.game.rule.MahjongRule;
import top.focess.mahjong.terminal.command.converter.MahjongRuleConverter;

import java.util.List;

public class GameCommand extends Command {

    public GameCommand() {
        super("game", "g");
    }

    @Override
    public void init() {
        this.addExecutor((sender, dataCollection, ioHandler) -> {
            MahjongRule rule = dataCollection.get(MahjongRule.class);
            Game game = Launcher.defaultLauncher.createGame(rule);
            System.out.println("Game " + game.getId() + " created!");
            return CommandResult.ALLOW;
        }, CommandArgument.of("create"), CommandArgument.of(MahjongRuleConverter.MAHJONG_RULE_CONVERTER));
    }

    @Override
    public @NotNull List<String> usage(CommandSender sender) {
        return List.of("game create <rule>");
    }
}
