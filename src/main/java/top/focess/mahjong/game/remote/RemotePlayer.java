package top.focess.mahjong.game.remote;

import com.google.common.collect.Lists;
import top.focess.mahjong.game.Player;
import top.focess.mahjong.game.data.PlayerData;

import java.util.*;

public class RemotePlayer extends Player {

    private static final Map<UUID, RemotePlayer> PLAYERS = Collections.synchronizedMap(new WeakHashMap<>());

    private int clientId;

    public RemotePlayer(int clientId, UUID id) {
        super(id);
        this.clientId = clientId;
    }

    public synchronized static RemotePlayer getOrCreatePlayer(int clientId, UUID id) {
        if (PLAYERS.containsKey(id)) {
            RemotePlayer player = PLAYERS.get(id);
            if (player.clientId == -1 && clientId != -1)
                player.clientId = clientId;
            if (player.clientId == clientId || clientId == -1)
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
            if (entry.getValue().clientId == clientId) {
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
