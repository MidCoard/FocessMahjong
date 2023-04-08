package top.focess.mahjong.game.remote;

import top.focess.mahjong.game.Game;
import top.focess.mahjong.game.Player;
import top.focess.mahjong.game.data.GameData;
import top.focess.mahjong.game.data.PlayerData;
import top.focess.mahjong.game.packet.GameActionPacket;
import top.focess.mahjong.game.packet.GameActionStatusPacket;
import top.focess.mahjong.game.packet.SyncGamePacket;
import top.focess.net.socket.FocessClientSocket;

public class RemoteGame extends Game {
    private final FocessClientSocket socket;

    public RemoteGame(FocessClientSocket socket, GameData data) {
        super(data.getId(), data.getRule());
        this.socket = socket;
        this.update(data);
    }

    public static RemoteGame getOrCreateGame(FocessClientSocket socket, GameData data) {
        Game game = Game.getGame(data.getId());
        if (game instanceof RemoteGame) {
            ((RemoteGame) game).update(data);
            return (RemoteGame) game;
        }
        if (game != null)
            throw new IllegalArgumentException("Game " + data.getId() + " is not a remote game");
        return new RemoteGame(socket, data);
    }

    @Override
    public synchronized boolean join(Player player) {
        GameActionStatusPacket.GameActionStatus status = this.gameRequester.request("join",
                ()-> this.socket.getReceiver().sendPacket(new GameActionPacket(player.getId(), this.getId(), GameActionPacket.GameAction.JOIN)),
                player.getId());
        if (status == GameActionStatusPacket.GameActionStatus.SUCCESS) {
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

    public synchronized void syncGameData() {
        GameData gameData = this.gameRequester.request("sync",
                () -> this.socket.getReceiver().sendPacket(new SyncGamePacket(this.getId())),
                this.getId());
        this.update(gameData);
    }

    @Override
    public synchronized GameData getGameData() {
        syncGameData();
        return super.getGameData();
    }

    public synchronized void update(GameData gameData) {
        if (!this.getId().equals(gameData.getId()) || !this.getRule().equals(gameData.getRule()))
            throw new IllegalArgumentException("The game base data is not match");
        this.setGameState(gameData.getGameState());
        this.setStartTime(gameData.getStartTime());


        // todo update tiles

        this.players.clear();
        for (PlayerData playerData : gameData.getPlayerData()) {
            Player player = Player.getPlayer(-1, playerData.getId());
            if (player == null)
                throw new IllegalArgumentException("The player is not exist.");
            if (player instanceof RemotePlayer)
                ((RemotePlayer) player).update(playerData);
            this.players.add(player);
        }
    }

    public void remove() {
        GAMES.remove(this.getId());
    }
}
