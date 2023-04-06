package top.focess.mahjong.game.remote;

import top.focess.mahjong.game.Game;
import top.focess.mahjong.game.Player;
import top.focess.mahjong.game.data.GameData;
import top.focess.mahjong.game.packet.GameActionStatusPacket;
import top.focess.mahjong.game.packet.JoinGamePacket;
import top.focess.mahjong.game.packet.LeaveGamePacket;
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
    public boolean join(Player player) {
        this.socket.getReceiver().sendPacket(new JoinGamePacket(player.getId(), this.getId()));
        GameActionStatusPacket.GameActionStatus status = this.requester.request("join", player.getId());
        if (status == GameActionStatusPacket.GameActionStatus.SUCCESS) {
            player.setGame(this);
            return true;
        }
        return false;
    }

    @Override
    public boolean leave(Player player) {
        this.socket.getReceiver().sendPacket(new LeaveGamePacket(player.getId(), this.getId()));
        GameActionStatusPacket.GameActionStatus status = this.requester.request("leave", player.getId());
        if (status == GameActionStatusPacket.GameActionStatus.SUCCESS) {
            player.setGame(null);
            return true;
        }
        return false;
    }
}
