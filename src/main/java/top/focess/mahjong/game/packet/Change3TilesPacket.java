package top.focess.mahjong.game.packet;

import top.focess.net.packet.Packet;

import java.util.UUID;

public class Change3TilesPacket extends Packet {

    public static final int PACKET_ID = 130;
    private final UUID playerId;
    private final UUID gameId;
    private final int tile1;
    private final int tile2;
    private final int tile3;

    public Change3TilesPacket(UUID playerId, UUID gameId, int tile1, int tile2, int tile3) {
        this.playerId = playerId;
        this.gameId = gameId;
        this.tile1 = tile1;
        this.tile2 = tile2;
        this.tile3 = tile3;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public UUID getGameId() {
        return gameId;
    }

    public int getTile1() {
        return tile1;
    }

    public int getTile2() {
        return tile2;
    }

    public int getTile3() {
        return tile3;
    }

    @Override
    public int getId() {
        return PACKET_ID;
    }
}
