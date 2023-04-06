package top.focess.mahjong;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import top.focess.mahjong.game.Game;
import top.focess.mahjong.game.GameState;
import top.focess.mahjong.game.LocalGame;
import top.focess.mahjong.game.LocalPlayer;
import top.focess.mahjong.game.packet.*;
import top.focess.mahjong.game.remote.GameRequester;
import top.focess.mahjong.game.remote.RemoteGame;
import top.focess.mahjong.game.data.GameData;
import top.focess.mahjong.game.remote.RemotePlayer;
import top.focess.mahjong.game.rule.MahjongRule;
import top.focess.net.IllegalPortException;
import top.focess.net.receiver.FocessClientReceiver;
import top.focess.net.receiver.FocessReceiver;
import top.focess.net.receiver.ServerMultiReceiver;
import top.focess.net.socket.ASocket;
import top.focess.net.socket.FocessUDPClientSocket;
import top.focess.net.socket.FocessUDPServerMultiSocket;
import top.focess.scheduler.Callback;
import top.focess.scheduler.ThreadPoolScheduler;
import top.focess.util.Pair;
import top.focess.util.option.Option;
import top.focess.util.option.OptionParserClassifier;
import top.focess.util.option.Options;
import top.focess.util.option.type.IntegerOptionType;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

public class Launcher {

    static {
        ASocket.enableDebug();
    }

    private static final ThreadPoolScheduler FOCESS_SCHEDULER = new ThreadPoolScheduler("Mahjong", 10);
    private final LocalPlayer player = new LocalPlayer();
    private final FocessUDPServerMultiSocket serverSocket;

    private final Map<Pair<String, Integer>, FocessUDPClientSocket> clientSockets = Maps.newConcurrentMap();

    public Launcher() throws IllegalPortException {
        this(0);
    }

    public Launcher(int serverPort) throws IllegalPortException {
        this.serverSocket = new FocessUDPServerMultiSocket(serverPort);
        ServerMultiReceiver receiver = this.serverSocket.getReceiver();
        receiver.register("mahjong", JoinGamePacket.class, (clientId, packet) -> {
            Game game = Game.getGame(packet.getGameId());
            boolean flag = false;
            if (game instanceof LocalGame) {
                RemotePlayer player = RemotePlayer.getOrCreatePlayer(clientId, packet.getPlayerId());
                flag = game.join(player);
            }
            receiver.sendPacket(clientId, new GameActionStatusPacket(packet.getGameId(), packet.getPlayerId(), GameActionStatusPacket.GameAction.JOIN, flag ? GameActionStatusPacket.GameActionStatus.SUCCESS : GameActionStatusPacket.GameActionStatus.FAILURE));
        });
        receiver.register("mahjong", LeaveGamePacket.class, (clientId, packet) -> {
            Game game = Game.getGame(packet.getGameId());
            boolean flag = false;
            if (game instanceof LocalGame) {
                RemotePlayer player = RemotePlayer.getOrCreatePlayer(clientId, packet.getPlayerId());
                flag = game.leave(player);
            }
            receiver.sendPacket(clientId, new GameActionStatusPacket(packet.getGameId(), packet.getPlayerId(), GameActionStatusPacket.GameAction.LEAVE, flag ? GameActionStatusPacket.GameActionStatus.SUCCESS : GameActionStatusPacket.GameActionStatus.FAILURE));
        });
        receiver.register("mahjong", ListGamesPacket.class, (clientId, packet) -> {
            List<GameData> gameDataList = Lists.newArrayList();
            for (Game game : Game.getGames()) {
                if (game instanceof RemoteGame)
                    continue;
                gameDataList.add(game.getGameData());
            }
            receiver.sendPacket(clientId, new GamesPacket(gameDataList));
        });
        receiver.register("mahjong", SyncGamePacket.class, (clientId, packet) -> {
            Game game = Game.getGame(packet.getGameId());
            if (game instanceof LocalGame)
                receiver.sendPacket(clientId, new GamePacket(game.getGameData()));
        });
    }

    public void exit() {
        this.serverSocket.close();
        for (FocessUDPClientSocket clientSocket : this.clientSockets.values())
            clientSocket.close();
    }

    public LocalGame createGame(MahjongRule rule) {
        return new LocalGame(rule);
    }

    public LocalPlayer getPlayer() {
        return player;
    }

    public static void main(String[] args) {
        Options options = Options.parse(args,
                new OptionParserClassifier("port", IntegerOptionType.INTEGER_OPTION_TYPE),
                new OptionParserClassifier("test", IntegerOptionType.INTEGER_OPTION_TYPE));
        Option option = options.get("port");
        Launcher launcher;
        try {
            launcher = new Launcher(option != null ? option.get(IntegerOptionType.INTEGER_OPTION_TYPE) : 0);
        } catch (IllegalPortException e) {
            System.err.println("No available port found!");
            return;
        }
        option = options.get("test");
        if (option != null) {
            int test = option.get(IntegerOptionType.INTEGER_OPTION_TYPE);
            if (test == 1) {
                LocalGame game = launcher.createGame(MahjongRule.SICHUAN);
                launcher.getPlayer().join(game);
                try {
                    new Object().wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            } else if (test == 2) {
                List<RemoteGame> games = launcher.getRemoteGames("127.0.0.1", 1234);
                if (games != null)
                    for (RemoteGame remoteGame : games)
                        if (remoteGame.getGameState() == GameState.WAITING) {
                            boolean flag = remoteGame.join(launcher.getPlayer());
                            System.out.println(flag);
                        }
            }
        }
    }

    public List<RemoteGame> getRemoteGames(String ip, int port) {
        try {
            FocessUDPClientSocket clientSocket = clientSockets.get(Pair.of(ip, port));
            FocessClientReceiver receiver;
            List<RemoteGame> ret = Lists.newArrayList();
            AtomicBoolean flag = new AtomicBoolean(false);
            if (clientSocket == null) {
                clientSocket = new FocessUDPClientSocket("localhost", ip, port, "mahjong", true, true);
                clientSocket.registerReceiver(receiver = new FocessClientReceiver(clientSocket, "localhost", ip, port, "mahjong"));
                FocessUDPClientSocket finalClientSocket = clientSocket;
                receiver.register(GamesPacket.class, (clientId, packet) -> {
                    for (GameData data : packet.getGames())
                        ret.add(new RemoteGame(finalClientSocket, data));
                    flag.set(true);
                });
                receiver.register(GameActionStatusPacket.class, (clientId, packet) -> GameRequester.getGameRequester(packet.getGameId()).response(packet.getGameAction().getName(), packet.getGameActionStatus()));
                receiver.register(GamePacket.class, (clientId, packet) -> GameRequester.getGameRequester(packet.getGameData().getId()).response("sync", packet.getGameData()));
                clientSockets.put(Pair.of(ip, port), clientSocket);
            }
            clientSocket.getReceiver().sendPacket(new ListGamesPacket());
            Callback<List<RemoteGame>> callback = FOCESS_SCHEDULER.submit(() -> {
                while(!flag.get());
                return ret;
            });
            return callback.get(5, TimeUnit.SECONDS);
        } catch (IllegalPortException | InterruptedException | ExecutionException | TimeoutException e) {
            return null;
        }
    }
}
