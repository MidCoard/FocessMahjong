package top.focess.mahjong;

import com.google.common.collect.Lists;
import top.focess.command.Command;
import top.focess.command.DataCollection;
import top.focess.mahjong.game.Game;
import top.focess.mahjong.game.LocalGame;
import top.focess.mahjong.game.LocalPlayer;
import top.focess.mahjong.game.data.GameData;
import top.focess.mahjong.game.data.PlayerData;
import top.focess.mahjong.game.packet.*;
import top.focess.mahjong.game.packet.codec.*;
import top.focess.mahjong.game.remote.RemoteGame;
import top.focess.mahjong.game.remote.RemotePlayer;
import top.focess.mahjong.game.rule.MahjongRule;
import top.focess.mahjong.terminal.TerminalLauncher;
import top.focess.mahjong.terminal.command.GameCommand;
import top.focess.mahjong.terminal.command.PlayerCommand;
import top.focess.mahjong.terminal.command.RemoteCommand;
import top.focess.mahjong.terminal.command.TileCommand;
import top.focess.mahjong.terminal.command.converter.MahjongRuleConverter;
import top.focess.mahjong.terminal.command.data.MahjongRuleBuffer;
import top.focess.net.IllegalPortException;
import top.focess.net.PacketPreCodec;
import top.focess.net.receiver.ServerMultiReceiver;
import top.focess.net.socket.ASocket;
import top.focess.net.socket.FocessMultiSocket;
import top.focess.util.option.Option;
import top.focess.util.option.OptionParserClassifier;
import top.focess.util.option.Options;
import top.focess.util.option.type.IntegerOptionType;
import top.focess.util.option.type.OptionType;

import java.util.List;

public class Launcher {

	public static final int DEFAULT_PORT = 2735;

	public static Launcher defaultLauncher;

	static {
		DataCollection.register(MahjongRuleConverter.MAHJONG_RULE_CONVERTER, MahjongRuleBuffer::allocate);
		Command.register(new GameCommand());
		Command.register(new RemoteCommand());
		Command.register(new PlayerCommand());
		Command.register(new TileCommand());
		PacketPreCodec.register(GameActionPacket.PACKET_ID, new GameActionPacketCodec());
		PacketPreCodec.register(GameActionStatusPacket.PACKET_ID, new GameActionStatusPacketCodec());
		PacketPreCodec.register(GameSyncPacket.PACKET_ID, new GameSyncPacketCodec());
		PacketPreCodec.register(ListGamesPacket.PACKET_ID, new ListGamesPacketCodec());
		PacketPreCodec.register(GamesPacket.PACKET_ID, new GamesPacketCodec());
		PacketPreCodec.register(SyncGamePacket.PACKET_ID, new SyncGamePacketCodec());
		PacketPreCodec.register(GamePacket.PACKET_ID, new GamePacketCodec());
		PacketPreCodec.register(SyncPlayerPacket.PACKET_ID, new SyncPlayerPacketCodec());
		PacketPreCodec.register(PlayerPacket.PACKET_ID, new PlayerPacketCodec());
		PacketPreCodec.register(GameTileActionPacket.PACKET_ID, new GameTileActionPacketCodec());
		PacketPreCodec.register(Change3TilesDirectionPacket.PACKET_ID, new Change3TilesDirectionPacketCodec());
		PacketPreCodec.register(FetchTilePacket.PACKET_ID, new FetchTilePacketCodec());
		PacketPreCodec.register(GameTileActionNoticePacket.PACKET_ID, new GameTileActionNoticePacketCodec());
		PacketPreCodec.register(LarkSuitPacket.PACKET_ID, new LarkSuitPacketCodec());
		PacketPreCodec.register(GameTileActionConfirmPacket.PACKET_ID, new GameTileActionConfirmPacketCodec());
	}

	private final FocessMultiSocket serverSocket;

	public Launcher() throws IllegalPortException {
		this(0);
	}

	public Launcher(final int serverPort) throws IllegalPortException {
		this.serverSocket = new FocessMultiSocket(serverPort);
		final ServerMultiReceiver receiver = this.serverSocket.getReceiver();
		receiver.setDisconnectedHandler(clientId -> {
			final List<RemotePlayer> players = RemotePlayer.removePlayers(clientId);
			for (final RemotePlayer player : players)
				player.getGame().leave(player);
		});
		receiver.register("mahjong", GameActionPacket.class, (clientId, packet) -> {
			final Game game = Game.getGame(packet.getGameId());
			if (game instanceof LocalGame) {
				final PlayerData playerData = game.getGameRequester().request("syncPlayer",
						() -> this.serverSocket.getReceiver().sendPacket(clientId, new SyncPlayerPacket(packet.getPlayerId(), packet.getGameId())),
						packet.getPlayerId());
				if (playerData == null)
					return;
				final RemotePlayer player = RemotePlayer.getOrCreatePlayer(clientId, playerData);
				if (player == null)
					return;
				final boolean flag = switch (packet.getGameAction()) {
					case READY -> game.ready(player);
					case UNREADY -> game.unready(player);
					case LEAVE -> game.leave(player);
					case JOIN -> game.join(player);
				};
				receiver.sendPacket(clientId, new GameActionStatusPacket(packet.getPlayerId(), packet.getGameId(), packet.getGameAction(), flag ? GameActionStatusPacket.GameActionStatus.SUCCESS : GameActionStatusPacket.GameActionStatus.FAILURE));
			} else
				receiver.sendPacket(clientId, new GameActionStatusPacket(packet.getPlayerId(), packet.getGameId(), packet.getGameAction(), GameActionStatusPacket.GameActionStatus.FAILURE));
		});
		receiver.register("mahjong", ListGamesPacket.class, (clientId, packet) -> {
			final List<GameData> gameDataList = Lists.newArrayList();
			for (final Game game : Game.getGames()) {
				if (game instanceof RemoteGame)
					continue;
				gameDataList.add(game.getGameData());
			}
			receiver.sendPacket(clientId, new GamesPacket(gameDataList));
		});
		receiver.register("mahjong", SyncGamePacket.class, (clientId, packet) -> {
			final Game game = Game.getGame(packet.getGameId());
			if (game instanceof LocalGame) {
				final RemotePlayer player = RemotePlayer.getPlayer(clientId, packet.getPlayerId());
				if (player == null)
					return;
				receiver.sendPacket(clientId, new GamePacket(((LocalGame) game).getPartGameData(player)));
			}
		});
		receiver.register("mahjong", PlayerPacket.class, (clientId, packet) -> {
			final Game game = Game.getGame(packet.getGameId());
			if (game instanceof LocalGame)
				game.getGameRequester().response("syncPlayer", packet.getPlayerData(), id -> id[0].equals(packet.getPlayerData().id()));
		});
		receiver.register("mahjong", GameTileActionPacket.class, (clientId, packet) -> {
			final Game game = Game.getGame(packet.getGameId());
			if (game instanceof LocalGame) {
				final RemotePlayer player = RemotePlayer.getPlayer(clientId, packet.getPlayerId());
				game.doTileAction(packet.getTileAction(), player, packet.getTileStates());
			}
		});
		receiver.register("mahjong", LarkSuitPacket.class, (clientId, packet) -> {
			final Game game = Game.getGame(packet.getGameId());
			if (game instanceof LocalGame) {
				final RemotePlayer player = RemotePlayer.getPlayer(clientId, packet.getPlayerId());
				game.larkSuit(player, packet.getCategory());
			}
		});
	}

	//
	public static void main(final String[] args) {
		final Options options = Options.parse(args,
				new OptionParserClassifier("name", OptionType.DEFAULT_OPTION_TYPE),
				new OptionParserClassifier("port", IntegerOptionType.INTEGER_OPTION_TYPE),
				new OptionParserClassifier("debug"),
				new OptionParserClassifier("gui"));
		Option option = options.get("name");
		LocalPlayer.localPlayer = new LocalPlayer(option != null ? option.get(OptionType.DEFAULT_OPTION_TYPE) : "LocalPlayer");
		option = options.get("debug");
		if (option != null)
			ASocket.enableDebug();

		option = options.get("port");
		try {
			Launcher.defaultLauncher = new Launcher(option != null ? option.get(IntegerOptionType.INTEGER_OPTION_TYPE) : Launcher.DEFAULT_PORT);
		} catch (final IllegalPortException e) {
			throw new RuntimeException(e);
		}
		option = options.get("gui");
		if (option == null)
			TerminalLauncher.launch();
		Runtime.getRuntime().addShutdownHook(new Thread(Launcher.defaultLauncher::exit));
	}

	public LocalGame createGame(final MahjongRule rule) {
		return new LocalGame(this.serverSocket, rule);
	}

	public void exit() {
		this.serverSocket.close();
	}

}
