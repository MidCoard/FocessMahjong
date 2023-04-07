package top.focess.mahjong.game.remote;

import top.focess.mahjong.game.Game;
import top.focess.mahjong.game.Player;
import top.focess.mahjong.game.data.GameData;
import top.focess.mahjong.game.data.PlayerData;
import top.focess.mahjong.game.packet.*;
import top.focess.net.socket.FocessUDPClientSocket;

public class RemoteGame extends Game {
    private final FocessUDPClientSocket socket;
    private final GameRequester requester;

    public RemoteGame(FocessUDPClientSocket socket, GameData data) {
        super(data.getId(), data.getRule(), data.getGameState());
        this.socket = socket;
        this.requester = new GameRequester(this.getId());
    }

    @Override
    public synchronized boolean join(Player player) {
        this.socket.getReceiver().sendPacket(new GameActionPacket(player.getId(), this.getId(), GameActionPacket.GameAction.JOIN));
        GameActionStatusPacket.GameActionStatus status = this.requester.request("join", player.getId());
        if (status == GameActionStatusPacket.GameActionStatus.SUCCESS) {
            player.setGame(this);
            return true;
        }
        return false;
    }

    @Override
    public synchronized boolean leave(Player player) {
        this.socket.getReceiver().sendPacket(new GameActionPacket(player.getId(), this.getId(), GameActionPacket.GameAction.LEAVE));
        GameActionStatusPacket.GameActionStatus status = this.requester.request("leave", player.getId());
        if (status == GameActionStatusPacket.GameActionStatus.SUCCESS) {
            player.setGame(null);
            player.setPlayerState(Player.PlayerState.WAITING);
            return true;
        }
        return false;
    }

    @Override
    public synchronized boolean ready(Player player) {
        this.socket.getReceiver().sendPacket(new GameActionPacket(player.getId(), this.getId(), GameActionPacket.GameAction.READY));
        GameActionStatusPacket.GameActionStatus status = this.requester.request("ready", player.getId());
        if (status == GameActionStatusPacket.GameActionStatus.SUCCESS) {
            player.setPlayerState(Player.PlayerState.READY);
            return true;
        }
        return false;
    }

    @Override
    public synchronized boolean unready(Player player) {
        this.socket.getReceiver().sendPacket(new GameActionPacket(player.getId(), this.getId(), GameActionPacket.GameAction.UNREADY));
        GameActionStatusPacket.GameActionStatus status = this.requester.request("unready", player.getId());
        if (status == GameActionStatusPacket.GameActionStatus.SUCCESS) {
            player.setPlayerState(Player.PlayerState.WAITING);
            return true;
        }
        return false;
    }

    public synchronized void syncGameData() {
        this.socket.getReceiver().sendPacket(new SyncGamePacket(this.getId()));
        GameData gameData = this.requester.request("sync");
        this.update(gameData);
    }

    @Override
    public synchronized GameData getGameData() {
        syncGameData();
        return super.getGameData();
    }

    public synchronized void update(GameData gameData) {
        if (!this.getId().equals(gameData.getId()))
            throw new IllegalArgumentException("The game id is not equal to the game data id.");
        this.rule = gameData.getRule();
        this.gameState = gameData.getGameState();

        // todo update tiles
        this.players.clear();
        for (PlayerData playerData : gameData.getPlayerData()) {
            Player player = Player.getPlayer(playerData.getId());
            if (player == null)
                throw new IllegalArgumentException("The player is not exist.");
            player.update(playerData);
            this.players.add(player);
        }
    }

}
