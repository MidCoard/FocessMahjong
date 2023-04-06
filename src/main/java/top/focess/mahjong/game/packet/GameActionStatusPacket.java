package top.focess.mahjong.game.packet;

import top.focess.mahjong.game.packet.codec.GameActionStatusPacketCodec;
import top.focess.net.PacketPreCodec;
import top.focess.net.packet.Packet;

import java.util.UUID;

public class GameActionStatusPacket extends Packet {

    public static final int PACKET_ID = 102;

    static {
        PacketPreCodec.register(PACKET_ID, new GameActionStatusPacketCodec());
    }

    private final UUID playerId;
    private final UUID gameId;
    private final GameAction gameAction;
    private final GameActionStatus gameActionStatus;

    public GameActionStatusPacket(UUID playerId, UUID gameId, GameAction gameAction, GameActionStatus gameActionStatus) {
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

    public GameAction getGameAction() {
        return gameAction;
    }

    public GameActionStatus getGameActionStatus() {
        return gameActionStatus;
    }

    @Override
    public int getId() {
        return PACKET_ID;
    }

    public enum GameAction {
        JOIN("join"),
        LEAVE("leave");

        private final String name;

        GameAction(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public enum GameActionStatus {
        SUCCESS,
        FAILURE,
        UNKNOWN
    }
}
