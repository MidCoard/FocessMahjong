package top.focess.mahjong.game;

import top.focess.mahjong.game.data.GameData;
import top.focess.mahjong.game.packet.JoinGamePacket;
import top.focess.net.receiver.FocessClientReceiver;
import top.focess.net.socket.FocessClientSocket;
import top.focess.net.socket.FocessUDPClientSocket;

public class RemoteGame extends Game{
    private final FocessUDPClientSocket socket;

    public RemoteGame(FocessUDPClientSocket socket, GameData data) {
        super(data.getId(), data.getRule(), data.getGameState());
        this.socket = socket;
    }

    @Override
    public boolean join(Player player) {
        this.socket.getReceiver().sendPacket(new JoinGamePacket(player.getId(), this.getId()));
    }

    @Override
    public boolean leave(Player player) {

    }
}
