package top.focess.mahjong.game.remote;

import com.google.common.collect.Lists;
import top.focess.mahjong.game.Game;
import top.focess.mahjong.game.GameTileState;
import top.focess.mahjong.game.LocalGame;
import top.focess.mahjong.game.Player;
import top.focess.mahjong.game.data.GameData;
import top.focess.mahjong.game.data.PlayerData;
import top.focess.mahjong.game.data.TilesData;
import top.focess.mahjong.game.packet.*;
import top.focess.mahjong.game.tile.TileState;
import top.focess.mahjong.terminal.TerminalLauncher;
import top.focess.net.socket.FocessClientSocket;

import java.util.List;

public class RemoteGame extends Game {
    private final FocessClientSocket socket;

    private TilesData tilesData;

    public RemoteGame(FocessClientSocket socket, GameData data) {
        super(data.id(), data.rule());
        this.socket = socket;
        this.update(data);
    }

    public static RemoteGame getOrCreateGame(FocessClientSocket socket, GameData data) {
        Game game = Game.getGame(data.id());
        if (game instanceof RemoteGame) {
            ((RemoteGame) game).update(data);
            return (RemoteGame) game;
        }
        if (game != null)
            throw new IllegalArgumentException("Game " + data.id() + " is not a remote game");
        return new RemoteGame(socket, data);
    }

    @Override
    public synchronized boolean join(Player player) {
        GameActionStatusPacket.GameActionStatus status = this.gameRequester.request("join",
                ()-> this.socket.getReceiver().sendPacket(new GameActionPacket(player.getId(), this.getId(), GameActionPacket.GameAction.JOIN)),
                player.getId());
        if (status == GameActionStatusPacket.GameActionStatus.SUCCESS) {
            this.syncGameData(player);
            if (this.getGameState() == GameState.PLAYING)
                player.setPlayerState(Player.PlayerState.PLAYING);
            player.setGame(this);
            return true;
        }
        return false;
    }

    @Override
    public synchronized boolean leave(Player player) {
        GameActionStatusPacket.GameActionStatus status = this.gameRequester.request("leave",
                () -> this.socket.getReceiver().sendPacket(new GameActionPacket(player.getId(), this.getId(), GameActionPacket.GameAction.LEAVE)),
                player.getId());
        if (status == GameActionStatusPacket.GameActionStatus.SUCCESS) {
            player.setGame(null);
            player.setPlayerState(Player.PlayerState.WAITING);
            return true;
        }
        return false;
    }

    @Override
    public synchronized boolean ready(Player player) {
        GameActionStatusPacket.GameActionStatus status = this.gameRequester.request("ready",
                ()->this.socket.getReceiver().sendPacket(new GameActionPacket(player.getId(), this.getId(), GameActionPacket.GameAction.READY)),
                player.getId());
        if (status == GameActionStatusPacket.GameActionStatus.SUCCESS) {
            player.setPlayerState(Player.PlayerState.READY);
            return true;
        }
        return false;
    }

    @Override
    public synchronized boolean unready(Player player) {
        GameActionStatusPacket.GameActionStatus status = this.gameRequester.request("unready",
                ()-> this.socket.getReceiver().sendPacket(new GameActionPacket(player.getId(), this.getId(), GameActionPacket.GameAction.UNREADY)),
                player.getId());
        if (status == GameActionStatusPacket.GameActionStatus.SUCCESS) {
            player.setPlayerState(Player.PlayerState.WAITING);
            return true;
        }
        return false;
    }

    @Override
    public GameTileState getGameTileState() {
        if (this.getGameState() != GameState.PLAYING || this.tilesData == null)
            return null;
        return this.tilesData.gameTileState();
    }

    @Override
    public void doTileAction(LocalGame.TileAction tileAction, Player player, Object... objects) {
        if (this.getGameState() != GameState.PLAYING)
            return;
        if (tileAction == LocalGame.TileAction.CHANGE_3_TILES)
            this.socket.getReceiver().sendPacket(new Change3TilesPacket(player.getId(), this.getId(), (int) objects[0], (int) objects[1], (int) objects[2]));
        if (tileAction == LocalGame.TileAction.KONG)
            this.socket.getReceiver().sendPacket(new KongPacket(player.getId(), this.getId(), (TileState) objects[0]));
        if (tileAction == LocalGame.TileAction.DISCARD_TILE)
            this.socket.getReceiver().sendPacket(new DiscardTilePacket(player.getId(), this.getId(), (TileState) objects[0]));
        if (tileAction == LocalGame.TileAction.HU)
            this.socket.getReceiver().sendPacket(new HuPacket(player.getId(), this.getId()));
    }

    public synchronized void syncGameData(Player player) {
        GameData gameData = this.gameRequester.request("sync",
                () -> this.socket.getReceiver().sendPacket(new SyncGamePacket(player.getId(), this.getId())),
                this.getId());
        this.update(gameData);
    }

    public synchronized void update(GameData gameData) {
        if (!this.getId().equals(gameData.id()) || !this.getRule().equals(gameData.rule()))
            throw new IllegalArgumentException("The game base data is not match");
        this.setGameState(gameData.gameState());
        this.setStartTime(gameData.startTime());
        this.setGameTime(gameData.gameTime());
        this.setCountdown(gameData.countdown());


        // todo update tiles
        this.setTilesData(gameData.tilesData());
        List<Player> temp = Lists.newArrayList();
        for (PlayerData playerData : gameData.playerData()) {
            Player player = Player.getPlayer(-1, playerData);
            if (player == null)
                throw new IllegalArgumentException("The player is not exist.");
            temp.add(player);
        }
        TerminalLauncher.change("players", this, this.players, temp);
        this.players.clear();
        this.players.addAll(temp);
    }

    private void setTilesData(TilesData tilesData) {
        if (this.tilesData != null && tilesData != null) {
            if (this.tilesData.gameTileState() != tilesData.gameTileState())
                TerminalLauncher.change("gameTileState", this, this.tilesData.gameTileState(), tilesData.gameTileState());
            if (this.tilesData.remainTiles() != tilesData.remainTiles())
                TerminalLauncher.change("remainTiles", this, this.tilesData.remainTiles(), tilesData.remainTiles());
            if (!this.tilesData.tiles().equals(tilesData.tiles()))
                TerminalLauncher.change("tiles", this, this.tilesData.tiles(), tilesData.tiles());
            if (!this.tilesData.discardTiles().equals(tilesData.discardTiles()))
                TerminalLauncher.change("doraIndicators", this, this.tilesData.discardTiles(), tilesData.discardTiles());
        } else if (this.tilesData != null || tilesData != null) {
            TerminalLauncher.change("gameTileState", this, this.tilesData == null ? null : this.tilesData.gameTileState(), tilesData == null ? null : tilesData.gameTileState());
            TerminalLauncher.change("remainTiles", this, this.tilesData == null ? null : this.tilesData.remainTiles(), tilesData == null ? null : tilesData.remainTiles());
            TerminalLauncher.change("tiles", this, this.tilesData == null ? null : this.tilesData.tiles(), tilesData == null ? null : tilesData.tiles());
            TerminalLauncher.change("doraIndicators", this, this.tilesData == null ? null : this.tilesData.discardTiles(), tilesData == null ? null : tilesData.discardTiles());
        }
        this.tilesData = tilesData;
    }

    public void remove() {
        GAMES.remove(this.getId());
    }

    public TilesData getTilesData() {
        return tilesData;
    }
}
