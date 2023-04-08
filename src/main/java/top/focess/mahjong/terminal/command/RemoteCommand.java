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
            String name = dataCollection.get();
            if (REMOTE_SERVER_MAP.containsKey(name)) {
                System.out.println("Remote server " + name + " already exists!");
                return CommandResult.REFUSE;
            }
            String ip = dataCollection.get();
            int port = dataCollection.getInt();
            try {
                RemoteServer remoteServer = RemoteServer.connect(ip, port);
                REMOTE_SERVER_MAP.put(name, remoteServer);
                System.out.println("Remote server " + name + " connected!");
                return CommandResult.ALLOW;
            } catch (IllegalPortException e) {
                return CommandResult.REFUSE;
            }
        }, CommandArgument.of("connect"), CommandArgument.ofString(), CommandArgument.ofString(), CommandArgument.ofInt());

        this.addExecutor((sender, dataCollection, ioHandler) -> {
            String name = dataCollection.get();
            if (!REMOTE_SERVER_MAP.containsKey(name)) {
                System.out.println("Remote server " + name + " not exists!");
                return CommandResult.REFUSE;
            }
            List<RemoteGame> games = REMOTE_SERVER_MAP.get(name).getRemoteGames();
            System.out.println("Remote server " + name + " has " + games.size() + " games");
            for (RemoteGame game : games)
                System.out.println(game.getId() + " " + game.getRule().getName());
            return CommandResult.ALLOW;
        }, CommandArgument.of("fetch"), CommandArgument.ofString());
    }

    @Override
    public @NotNull List<String> usage(CommandSender sender) {
        return List.of("remote connect <name> <ip> <port>", "remote fetch <name>");
    }
}
