package top.focess.mahjong.terminal.command;

import org.jetbrains.annotations.NotNull;
import top.focess.command.Command;
import top.focess.command.CommandArgument;
import top.focess.command.CommandResult;
import top.focess.command.CommandSender;
import top.focess.mahjong.game.GameTileState;
import top.focess.mahjong.game.LocalGame;
import top.focess.mahjong.game.LocalPlayer;
import top.focess.mahjong.game.Player;

import java.util.List;

public class TileCommand extends Command {
    public TileCommand() {
        super("tile", "t");
    }

    @Override
    public void init() {
        this.addExecutor((sender, dataCollection, ioHandler) -> {
            int tile1 = dataCollection.getInt();
            int tile2 = dataCollection.getInt();
            int tile3 = dataCollection.getInt();
            Player player = LocalPlayer.localPlayer;
            if (player.getGame() == null || player.getGame().getGameTileState() != GameTileState.CHANGE_3_TILES) {
                ioHandler.output("You can't change tiles now!");
                return CommandResult.REFUSE;
            }
            player.getGame().doTileAction(LocalGame.TileAction.CHANGE_3_TILES, player, tile1, tile2, tile3);
            ioHandler.output("You have changed tiles!");
            return CommandResult.ALLOW;
        }, CommandArgument.of("change"), CommandArgument.ofInt(), CommandArgument.ofInt(), CommandArgument.ofInt());
    }

    @Override
    public @NotNull List<String> usage(CommandSender sender) {
        return List.of("tile change <1> <2> <3>", "tile discard <1>", "tile condition <condition>");
    }
}
