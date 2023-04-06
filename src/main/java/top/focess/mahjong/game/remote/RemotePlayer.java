package top.focess.mahjong.game.remote;

import com.google.common.collect.Maps;
import top.focess.mahjong.game.Player;
import top.focess.mahjong.game.data.PlayerData;
import top.focess.util.Pair;

import java.util.Map;
import java.util.UUID;

public class RemotePlayer extends Player {

    private static final Map<UUID, Pair<Integer, RemotePlayer>> PLAYERS = Maps.newConcurrentMap();

    public RemotePlayer(UUID id) {
        super(id);
    }

    public synchronized static RemotePlayer getOrCreatePlayer(int clientId, UUID id) {
        if (PLAYERS.containsKey(id)) {
            Pair<Integer, RemotePlayer> pair = PLAYERS.get(id);
            if (clientId != -1 && pair.getKey() == -1)
                PLAYERS.put(id, Pair.of(clientId, pair.getValue()));
            if (pair.getKey() == clientId || pair.getKey() == -1)
                return pair.getValue();
            return null;
        }
        RemotePlayer player = new RemotePlayer(id);
        PLAYERS.put(id, Pair.of(clientId, player));
        return player;
    }

    public void update(PlayerData playerData) {
        if (!this.getId().equals(playerData.getId()))
            throw new IllegalArgumentException("The player id is not equal to the player data id.");
        this.playerState = playerData.getPlayerState();
    }
}
