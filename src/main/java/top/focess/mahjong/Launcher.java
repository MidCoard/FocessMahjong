package top.focess.mahjong;

import com.google.common.collect.Lists;
import top.focess.mahjong.game.Game;
import top.focess.mahjong.game.LocalGame;
import top.focess.mahjong.game.LocalPlayer;
import top.focess.mahjong.game.data.GameData;
import top.focess.mahjong.game.packet.*;
import top.focess.mahjong.game.packet.codec.*;
import top.focess.mahjong.game.remote.RemoteGame;
import top.focess.mahjong.game.remote.RemotePlayer;
import top.focess.mahjong.game.remote.RemoteServer;
import top.focess.mahjong.game.rule.MahjongRule;
import top.focess.net.IllegalPortException;
import top.focess.net.PacketPreCodec;
import top.focess.net.receiver.ServerMultiReceiver;
import top.focess.net.socket.ASocket;
import top.focess.net.socket.FocessMultiSocket;
import top.focess.net.socket.FocessUDPMultiSocket;
import top.focess.util.option.Option;
import top.focess.util.option.OptionParserClassifier;
import top.focess.util.option.Options;
import top.focess.util.option.type.IntegerOptionType;

import java.util.List;

public class Launcher {

    static {
        ASocket.enableDebug();
        PacketPreCodec.register(GameActionPacket.PACKET_ID, new GameActionPacketCodec());
        PacketPreCodec.register(GameActionStatusPacket.PACKET_ID, new GameActionStatusPacketCodec());
        PacketPreCodec.register(GameSyncPacket.PACKET_ID, new GameSyncPacketCodec());
        PacketPreCodec.register(ListGamesPacket.PACKET_ID, new ListGamesPacketCodec());
        PacketPreCodec.register(GamesPacket.PACKET_ID, new GamesPacketCodec());
        PacketPreCodec.register(SyncGamePacket.PACKET_ID, new SyncGamePacketCodec());
        PacketPreCodec.register(GamePacket.PACKET_ID, new GamePacketCodec());
    }
    private final LocalPlayer player = new LocalPlayer();
    private final FocessMultiSocket serverSocket;

    public Launcher() throws IllegalPortException {
        this(0);
    }

    public Launcher(int serverPort) throws IllegalPortException {
        this.serverSocket = new FocessMultiSocket(serverPort);
        ServerMultiReceiver receiver = this.serverSocket.getReceiver();
        receiver.register("mahjong", GameActionPacket.class, (clientId, packet) -> {
            Game game = Game.getGame(packet.getGameId());
            boolean flag = false;
            if (game instanceof LocalGame) {
                RemotePlayer player = RemotePlayer.getOrCreatePlayer(clientId, packet.getPlayerId());
                flag = switch (packet.getGameAction()) {
                    case READY -> game.ready(player);
                    case UNREADY -> game.unready(player);
                    case LEAVE -> game.leave(player);
                    case JOIN -> game.join(player);
                };
            }
            receiver.sendPacket(clientId, new GameActionStatusPacket(packet.getGameId(), packet.getPlayerId(), packet.getGameAction(), flag ? GameActionStatusPacket.GameActionStatus.SUCCESS : GameActionStatusPacket.GameActionStatus.FAILURE));
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
    }

    public LocalGame createGame(MahjongRule rule) {
        return new LocalGame(this.serverSocket, rule);
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
                while(true) {
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println(game.getGameState());
                    if (2 == 1)
                        break;
                }
            } else if (test == 2) {
                RemoteServer remoteServer;
                try {
                    remoteServer = RemoteServer.connect("127.0.0.1", 1234);
                } catch (IllegalPortException e) {
                    throw new RuntimeException(e);
                }
                List<RemoteGame> games = remoteServer.getRemoteGames();
                if (games != null)
                    for (RemoteGame remoteGame : games)
                        if (remoteGame.getGameState() == Game.GameState.WAITING) {
                            boolean flag = remoteGame.join(launcher.getPlayer());
                            System.out.println(flag);
                        }
                remoteServer.close();
            }
        }
    }


}
