package top.focess.mahjong.terminal.command;

import org.jetbrains.annotations.NotNull;
import top.focess.command.Command;
import top.focess.command.CommandArgument;
import top.focess.command.CommandResult;
import top.focess.command.CommandSender;
import top.focess.mahjong.game.GameTileState;
import top.focess.mahjong.game.LocalPlayer;
import top.focess.mahjong.game.Player;
import top.focess.mahjong.game.packet.GameTileActionPacket;
import top.focess.mahjong.game.tile.TileState;
import top.focess.mahjong.terminal.command.converter.TileStateConverter;

import java.util.List;

public class TileCommand extends Command {
	public TileCommand() {
		super("tile", "t");
	}

	@Override
	public void init() {
		this.addExecutor((sender, dataCollection, ioHandler) -> {
			final TileState tileState1 = dataCollection.get(TileState.class);
			final TileState tileState2 = dataCollection.get(TileState.class);
			final TileState tileState3 = dataCollection.get(TileState.class);
			final Player player = LocalPlayer.localPlayer;
			if (player.getGame() == null || player.getGame().getGameTileState() != GameTileState.CHANGING_3_TILES) {
				ioHandler.output("You can't change tileStates now!");
				return CommandResult.REFUSE;
			}
			player.getGame().doTileAction(GameTileActionPacket.TileAction.CHANGE_3_TILES, player, tileState1, tileState2, tileState3);
			ioHandler.output("You have changed tileStates!");
			return CommandResult.ALLOW;
		}, CommandArgument.of("change"), CommandArgument.of(TileStateConverter.TILE_STATE_CONVERTER), CommandArgument.of(TileStateConverter.TILE_STATE_CONVERTER), CommandArgument.of(TileStateConverter.TILE_STATE_CONVERTER));

		this.addExecutor((sender, dataCollection, ioHandler) -> {
			final TileState tileState = dataCollection.get(TileState.class);
			final Player player = LocalPlayer.localPlayer;
			if (player.getGame() == null || player.getGame().getGameTileState() != GameTileState.DISCARDING) {
				ioHandler.output("You can't discard tileState now!");
				return CommandResult.REFUSE;
			}
			player.getGame().doTileAction(GameTileActionPacket.TileAction.DISCARD, player, tileState);
			ioHandler.output("You have discarded tileState!");
			return CommandResult.ALLOW;
		}, CommandArgument.of("discard"), CommandArgument.of(TileStateConverter.TILE_STATE_CONVERTER));

		this.addExecutor((sender, dataCollection, ioHandler) -> {
			final TileState tileState = dataCollection.get(TileState.class);
			final Player player = LocalPlayer.localPlayer;
			if (player.getGame() == null || (player.getGame().getGameTileState() != GameTileState.CONDITION && player.getGame().getGameTileState() != GameTileState.DISCARDING)) {
				ioHandler.output("You can't kong tileState now!");
				return CommandResult.REFUSE;
			}
			player.getGame().doTileAction(GameTileActionPacket.TileAction.KONG, player, tileState);
			ioHandler.output("You have konged tileState!");
			return CommandResult.ALLOW;
		}, CommandArgument.of("kong"), CommandArgument.of(TileStateConverter.TILE_STATE_CONVERTER));

		this.addExecutor((sender, dataCollection, ioHandler) -> {
			final Player player = LocalPlayer.localPlayer;
			if (player.getGame() == null || player.getGame().getGameTileState() != GameTileState.CONDITION) {
				ioHandler.output("You can't pung tileState now!");
				return CommandResult.REFUSE;
			}
			player.getGame().doTileAction(GameTileActionPacket.TileAction.PUNG, player);
			ioHandler.output("You have punged tileState!");
			return CommandResult.ALLOW;
		}, CommandArgument.of("pung"));

		this.addExecutor((sender, dataCollection, ioHandler) -> {
			final Player player = LocalPlayer.localPlayer;
			if (player.getGame() == null || (player.getGame().getGameTileState() != GameTileState.CONDITION && player.getGame().getGameTileState() != GameTileState.CONDITION_HU && player.getGame().getGameTileState() != GameTileState.DISCARDING)) {
				ioHandler.output("You can't hu tileState now!");
				return CommandResult.REFUSE;
			}
			player.getGame().doTileAction(GameTileActionPacket.TileAction.HU, player);
			return CommandResult.ALLOW;
		}, CommandArgument.of("hu"));
	}

	@Override
	public @NotNull List<String> usage(final CommandSender sender) {
		return List.of("tile change <1> <2> <3>", "tile discard <1>", "tile kong <1>", "tile pung", "tile hu");
	}
}
