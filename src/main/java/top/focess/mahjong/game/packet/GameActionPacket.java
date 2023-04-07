package top.focess.mahjong.game.packet;

import top.focess.mahjong.game.packet.codec.GameActionPacketCodec;
import top.focess.net.PacketPreCodec;
import top.focess.net.packet.Packet;

import java.util.UUID;

public class GameActionPacket extends Packet {

    public static final int PACKET_ID = 100;

    private final UUID playerId;
    private final UUID gameId;
    private final GameAction gameAction;

    public GameActionPacket(UUID playerId, UUID gameId, GameAction gameAction) {
        this.playerId = playerId;
        this.gameId = gameId;
        this.gameAction = gameAction;
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

    @Override
    public int getId() {
        return PACKET_ID;
    }

    public enum GameAction {
        JOIN("join"),
        LEAVE("leave"),
        READY("ready"),
        UNREADY("unready");

        private final String name;

        GameAction(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
