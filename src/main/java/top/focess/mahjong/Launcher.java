package top.focess.mahjong;

import com.google.common.collect.Lists;
import top.focess.command.Command;
import top.focess.command.DataCollection;
import top.focess.mahjong.game.Game;
import top.focess.mahjong.game.LocalGame;
import top.focess.mahjong.game.LocalPlayer;
import top.focess.mahjong.game.data.GameData;
import top.focess.mahjong.game.packet.*;
import top.focess.mahjong.game.packet.codec.*;
import top.focess.mahjong.game.remote.RemoteGame;
import top.focess.mahjong.game.remote.RemotePlayer;
import top.focess.mahjong.game.rule.MahjongRule;
import top.focess.mahjong.terminal.command.CommandLine;
import top.focess.mahjong.terminal.command.GameCommand;
import top.focess.mahjong.terminal.command.PlayerCommand;
import top.focess.mahjong.terminal.command.RemoteCommand;
import top.focess.mahjong.terminal.command.converter.MahjongRuleConverter;
import top.focess.mahjong.terminal.command.data.MahjongRuleBuffer;
import top.focess.net.IllegalPortException;
import top.focess.net.PacketPreCodec;
import top.focess.net.receiver.ServerMultiReceiver;
import top.focess.net.socket.ASocket;
import top.focess.net.socket.FocessMultiSocket;

import java.util.List;
import java.util.Scanner;

public class Launcher {

    public static final int DEFAULT_PORT = 2735;

    public static final Launcher DEFAULT_LAUNCHER;

    static {
        ASocket.enableDebug();
        try {
            DEFAULT_LAUNCHER = new Launcher(DEFAULT_PORT);
        } catch (IllegalPortException e) {
            throw new RuntimeException(e);
        }
        DataCollection.register(new MahjongRuleConverter(), MahjongRuleBuffer::allocate);
        Command.register(new GameCommand());
        Command.register(new RemoteCommand());
        Command.register(new PlayerCommand());
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
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String nextLine = scanner.nextLine();
            CommandLine.execute(nextLine);
        }
    }


}
