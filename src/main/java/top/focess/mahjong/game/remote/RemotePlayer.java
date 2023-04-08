package top.focess.mahjong.game.remote;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import top.focess.mahjong.game.Player;
import top.focess.mahjong.game.data.PlayerData;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class RemotePlayer extends Player {

    private static final Map<UUID, RemotePlayer> PLAYERS = Maps.newConcurrentMap();

    private final int clientId;

    public RemotePlayer(int clientId, UUID id) {
        super(id);
        this.clientId = clientId;
    }

    public synchronized static RemotePlayer getOrCreatePlayer(int clientId, UUID id) {
        if (PLAYERS.containsKey(id)) {
            RemotePlayer player = PLAYERS.get(id);
            if (player.getClientId() == clientId)
                return player;
            return null;
        }
        RemotePlayer player = new RemotePlayer(clientId, id);
        PLAYERS.put(id, player);
        return player;
    }

    public static List<RemotePlayer> removePlayers(int clientId) {
        List<RemotePlayer> ret = Lists.newArrayList();
        for (Map.Entry<UUID, RemotePlayer> entry : PLAYERS.entrySet())
            if (entry.getValue().getClientId() == clientId) {
                ret.add(entry.getValue());
                PLAYERS.remove(entry.getKey());
            }
        return ret;
    }

    public int getClientId() {
        return clientId;
    }

    public void update(PlayerData playerData) {
        if (!this.getId().equals(playerData.getId()))
            throw new IllegalArgumentException("The player base data is not match!");
        this.setPlayerState(playerData.getPlayerState());
    }
}
