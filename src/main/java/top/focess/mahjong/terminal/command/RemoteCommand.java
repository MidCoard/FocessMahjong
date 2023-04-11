package top.focess.mahjong.terminal.command;

import com.google.common.collect.Maps;
import org.jetbrains.annotations.NotNull;
import top.focess.command.Command;
import top.focess.command.CommandArgument;
import top.focess.command.CommandResult;
import top.focess.command.CommandSender;
import top.focess.mahjong.game.remote.RemoteGame;
import top.focess.mahjong.game.remote.RemoteServer;
import top.focess.net.IllegalPortException;

import java.util.List;
import java.util.Map;

public class RemoteCommand extends Command {

    private static final Map<String, RemoteServer> REMOTE_SERVER_MAP = Maps.newHashMap();

    public RemoteCommand() {
        super("remote", "r");
    }

    @Override
    public void init() {
        this.addExecutor((sender, dataCollection, ioHandler) -> {
            final String name = dataCollection.get();
            if (RemoteCommand.REMOTE_SERVER_MAP.containsKey(name)) {
                ioHandler.output("Remote server " + name + " already exists!");
                return CommandResult.REFUSE;
            }
            final String ip = dataCollection.get();
            final int port = dataCollection.getInt();
            try {
                final RemoteServer remoteServer = RemoteServer.connect(ip, port);
                RemoteCommand.REMOTE_SERVER_MAP.put(name, remoteServer);
                ioHandler.output("Remote server " + name + " connected!");
                return CommandResult.ALLOW;
            } catch (final IllegalPortException e) {
                return CommandResult.REFUSE;
            }
        }, CommandArgument.of("connect"), CommandArgument.ofString(), CommandArgument.ofString(), CommandArgument.ofInt());

        this.addExecutor((sender, dataCollection, ioHandler) -> {
            final String name = dataCollection.get();
            if (!RemoteCommand.REMOTE_SERVER_MAP.containsKey(name)) {
                ioHandler.output("Remote server " + name + " not exists!");
                return CommandResult.REFUSE;
            }
            final List<RemoteGame> games = RemoteCommand.REMOTE_SERVER_MAP.get(name).getRemoteGames();
            System.out.println("Remote server " + name + " has " + games.size() + " games");
            for (final RemoteGame game : games)
                ioHandler.output(game.getId() + " " + game.getRule().getName());
            return CommandResult.ALLOW;
        }, CommandArgument.of("fetch"), CommandArgument.ofString());
    }

    @Override
    public @NotNull List<String> usage(final CommandSender sender) {
        return List.of("remote connect <name> <ip> <port>", "remote fetch <name>");
    }
}
