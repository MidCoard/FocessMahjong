package top.focess.mahjong.game.packet;

import top.focess.mahjong.game.data.PlayerData;
import top.focess.net.packet.Packet;

import java.util.UUID;

public class PlayerPacket extends Packet {
    public static final int PACKET_ID = 125;

    private final UUID gameId;
    private final PlayerData playerData;

    public PlayerPacket(UUID gameId, PlayerData playerData) {
        this.gameId = gameId;
        this.playerData = playerData;
    }

    @Override
    public int getId() {
        return PACKET_ID;
    }

    public PlayerData getPlayerData() {
        return playerData;
    }

    public UUID getGameId() {
        return gameId;
    }
}
