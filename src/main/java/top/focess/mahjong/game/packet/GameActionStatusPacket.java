package top.focess.mahjong.game.packet;

import top.focess.mahjong.game.packet.codec.GameActionStatusPacketCodec;
import top.focess.net.PacketPreCodec;
import top.focess.net.packet.Packet;

import java.util.UUID;

public class GameActionStatusPacket extends Packet {

    public static final int PACKET_ID = 101;

    static {
        PacketPreCodec.register(PACKET_ID, new GameActionStatusPacketCodec());
    }

    private final UUID playerId;
    private final UUID gameId;
    private final GameActionPacket.GameAction gameAction;
    private final GameActionStatus gameActionStatus;

    public GameActionStatusPacket(UUID playerId, UUID gameId, GameActionPacket.GameAction gameAction, GameActionStatus gameActionStatus) {
        this.playerId = playerId;
        this.gameId = gameId;
        this.gameAction = gameAction;
        this.gameActionStatus = gameActionStatus;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public UUID getGameId() {
        return gameId;
    }

    public GameActionPacket.GameAction getGameAction() {
        return gameAction;
    }

    public GameActionStatus getGameActionStatus() {
        return gameActionStatus;
    }

    @Override
    public int getId() {
        return PACKET_ID;
    }

    public enum GameActionStatus {
        SUCCESS,
        FAILURE,
        UNKNOWN
    }
}
