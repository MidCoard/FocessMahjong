package top.focess.mahjong.game.remote;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import top.focess.mahjong.game.Game;
import top.focess.mahjong.game.data.GameData;
import top.focess.mahjong.game.packet.*;
import top.focess.net.IllegalPortException;
import top.focess.net.receiver.ClientReceiver;
import top.focess.net.receiver.FocessClientReceiver;
import top.focess.net.socket.FocessUDPClientSocket;
import top.focess.util.Pair;

import java.util.List;
import java.util.Map;

public class RemoteServer {

    private static final Map<Pair<String, Integer>, FocessUDPClientSocket> CLIENT_SOCKET_MAP = Maps.newConcurrentMap();
    private final FocessUDPClientSocket clientSocket;
    private final List<RemoteGame> games = Lists.newArrayList();

    private final Object fetchRemoteGamesLock = new Object();

    private RemoteServer(String ip, int port) throws IllegalPortException {
        FocessUDPClientSocket clientSocket = CLIENT_SOCKET_MAP.get(Pair.of(ip, port));
        if (clientSocket == null) {
            clientSocket = new FocessUDPClientSocket("localhost", ip, port, "mahjong", true, true);
            ClientReceiver receiver = clientSocket.getReceiver();
            FocessUDPClientSocket finalClientSocket = clientSocket;
            receiver.register(GamesPacket.class, (clientId, packet) -> {
                synchronized (this.games) {
                    this.games.clear();
                    for (GameData data : packet.getGames())
                        games.add(new RemoteGame(finalClientSocket, data));
                }
                fetchRemoteGamesLock.notifyAll();
            });
            receiver.register(GameActionStatusPacket.class, (clientId, packet) -> GameRequester.getGameRequester(packet.getGameId()).response(packet.getGameAction().getName(), packet.getGameActionStatus()));
            receiver.register(GamePacket.class, (clientId, packet) -> GameRequester.getGameRequester(packet.getGameData().getId()).response("sync", packet.getGameData()));
            receiver.register(GameSyncPacket.class, (clientId, packet) -> {
                Game game = Game.getGame(packet.getGameData().getId());
                if (game instanceof RemoteGame)
                    ((RemoteGame) game).update(packet.getGameData());
            });
            CLIENT_SOCKET_MAP.put(Pair.of(ip, port), clientSocket);
        }
        this.clientSocket = clientSocket;
    }

    public static RemoteServer connect(String ip, int port) throws IllegalPortException {
        return new RemoteServer(ip, port);
    }

    public List<RemoteGame> getRemoteGames() {
        synchronized (fetchRemoteGamesLock) {
            clientSocket.getReceiver().sendPacket(new ListGamesPacket());
            try {
                fetchRemoteGamesLock.wait(5000);
            } catch (InterruptedException ignored) {}
        }
        return games;
    }

    public void close() {
        clientSocket.close();
    }
}
