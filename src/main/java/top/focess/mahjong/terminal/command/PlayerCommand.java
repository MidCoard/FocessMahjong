package top.focess.mahjong.terminal.command;

import org.jetbrains.annotations.NotNull;
import top.focess.command.Command;
import top.focess.command.CommandArgument;
import top.focess.command.CommandResult;
import top.focess.command.CommandSender;
import top.focess.mahjong.Launcher;
import top.focess.mahjong.game.Player;

import java.util.List;

public class PlayerCommand extends Command {

    public PlayerCommand() {
        super("player", "p");
    }

    @Override
    public void init() {
        this.addExecutor((sender, dataCollection, ioHandler) -> {
            Player player = Launcher.DEFAULT_LAUNCHER.getPlayer();
            System.out.println("Local player is " + player.getId() + ".");
            return CommandResult.ALLOW;
        }, CommandArgument.of("local"));
    }

    @Override
    public @NotNull List<String> usage(CommandSender sender) {
        return List.of();
    }
}
