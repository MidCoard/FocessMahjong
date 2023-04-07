package top.focess.mahjong.game.remote;

import com.google.common.collect.Maps;
import top.focess.mahjong.game.Player;
import top.focess.mahjong.game.data.PlayerData;
import top.focess.net.packet.Packet;
import top.focess.util.Pair;

import java.util.Map;
import java.util.UUID;

public class RemotePlayer extends Player {

    private static final Map<UUID, RemotePlayer> PLAYERS = Maps.newConcurrentMap();

    private int clientId;

    public RemotePlayer(int clientId, UUID id) {
        super(id);
        this.clientId = clientId;
    }

    public synchronized static RemotePlayer getOrCreatePlayer(int clientId, UUID id) {
        if (PLAYERS.containsKey(id)) {
            RemotePlayer player = PLAYERS.get(id);
            if (clientId != -1 && player.getClientId() == -1)
                player.clientId = clientId;
            if (player.getClientId() == clientId || clientId == -1)
                return player;
            return null;
        }
        RemotePlayer player = new RemotePlayer(clientId, id);
        PLAYERS.put(id, player);
        return player;
    }

    public void update(PlayerData playerData) {
        if (!this.getId().equals(playerData.getId()))
            throw new IllegalArgumentException("The player id is not equal to the player data id.");
        this.playerState = playerData.getPlayerState();
    }

    public int getClientId() {
        return clientId;
    }
}
