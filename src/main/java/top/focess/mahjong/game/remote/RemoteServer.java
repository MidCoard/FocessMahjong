package top.focess.mahjong.game.remote;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import top.focess.mahjong.game.Game;
import top.focess.mahjong.game.LocalPlayer;
import top.focess.mahjong.game.Player;
import top.focess.mahjong.game.data.GameData;
import top.focess.mahjong.game.packet.*;
import top.focess.mahjong.terminal.TerminalLauncher;
import top.focess.net.IllegalPortException;
import top.focess.net.receiver.ClientReceiver;
import top.focess.net.socket.FocessClientSocket;
import top.focess.util.Pair;

import java.util.List;
import java.util.Map;

public class RemoteServer {

    private static final Map<Pair<String, Integer>, FocessClientSocket> CLIENT_SOCKET_MAP = Maps.newConcurrentMap();
    private final FocessClientSocket clientSocket;
    private final List<RemoteGame> games = Lists.newArrayList();
    private final Object fetchRemoteGamesLock = new Object();

    private RemoteServer(final String ip, final int port) throws IllegalPortException {
        FocessClientSocket clientSocket = RemoteServer.CLIENT_SOCKET_MAP.get(Pair.of(ip, port));
        if (null == clientSocket) {
            clientSocket = new FocessClientSocket("localhost", ip, port, "mahjong", true, true);
            final ClientReceiver receiver = clientSocket.getReceiver();
            receiver.setDisconnectedHandler(clientId -> {
                synchronized (this.games) {
                    for (final RemoteGame game : this.games)
                        game.remove();
                    this.games.clear();
                }
            });
            final FocessClientSocket finalClientSocket = clientSocket;
            receiver.register(GamesPacket.class, (clientId, packet) -> {
                synchronized (this.games) {
                    final List<RemoteGame> temp = Lists.newArrayList();
                    for (final GameData data : packet.getGames()) {
                        final RemoteGame game = RemoteGame.getOrCreateGame(finalClientSocket, data);
                        temp.add(game);
                    }
                    for (final RemoteGame game : this.games)
                        if (!temp.contains(game))
                            game.remove();
                    this.games.clear();
                    this.games.addAll(temp);
                }
                synchronized (this.fetchRemoteGamesLock) {
                    this.fetchRemoteGamesLock.notifyAll();
                }
            });
            receiver.register(GameActionStatusPacket.class, (clientId, packet) -> {
                final Game game = Game.getGame(packet.getGameId());
                if (null != game)
                    game.getGameRequester().response(packet.getGameAction().getName(), packet.getGameActionStatus(), id -> id[0].equals(packet.getPlayerId()));
            });
            receiver.register(GamePacket.class, (clientId, packet) -> {
                final Game game = Game.getGame(packet.getGameData().id());
                if (null != game)
                    game.getGameRequester().response("sync", packet.getGameData(), id -> id[0].equals(packet.getGameData().id()));
            });
            receiver.register(GameSyncPacket.class, (clientId, packet) -> {
                final Game game = Game.getGame(packet.getGameData().id());
                if (game instanceof RemoteGame)
                    ((RemoteGame) game).update(packet.getGameData());
            });
            receiver.register(SyncPlayerPacket.class, (clientId, packet) -> {
                if (LocalPlayer.localPlayer.getId().equals(packet.getPlayerId())){
                    final PlayerPacket playerPacket = new PlayerPacket(packet.getGameId(), LocalPlayer.localPlayer.getPlayerData());
                    receiver.sendPacket(playerPacket);
                }
            });
            receiver.register(Change3TilesDirectionPacket.class, (clientId, packet) -> {
                final Game game = Game.getGame(packet.getGameId());
                if (null != game)
                    TerminalLauncher.change("changeDirection", game, -1,  packet.getDirection());
            });
            receiver.register(FetchTilePacket.class, (clientId, packet) -> {
                if (LocalPlayer.localPlayer.getId().equals(packet.getPlayerId()))
                    TerminalLauncher.change("fetchTileState", LocalPlayer.localPlayer, null, packet.getTileState());
            });
            receiver.register(GameTileActionNoticePacket.class, (clientId, packet) -> {
                // todo
            });
            receiver.register(GameTileActionConfirmPacket.class, (clientId, packet) -> {
                // todo
            });
            RemoteServer.CLIENT_SOCKET_MAP.put(Pair.of(ip, port), clientSocket);
        }
        this.clientSocket = clientSocket;
    }

    public static RemoteServer connect(final String ip, final int port) throws IllegalPortException {
        return new RemoteServer(ip, port);
    }

    public List<RemoteGame> getRemoteGames() {
        synchronized (this.fetchRemoteGamesLock) {
            this.clientSocket.getReceiver().sendPacket(new ListGamesPacket());
            try {
                this.fetchRemoteGamesLock.wait(5000);
            } catch (final InterruptedException ignored) {}
        }
        return this.games;
    }

    public void close() {
        this.clientSocket.close();
    }
}