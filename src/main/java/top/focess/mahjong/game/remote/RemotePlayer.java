package top.focess.mahjong.game.remote;

import com.google.common.collect.Maps;
import top.focess.mahjong.game.Player;
import top.focess.util.Pair;

import java.util.Map;
import java.util.UUID;

public class RemotePlayer extends Player {

    private static final Map<UUID, Pair<Integer, RemotePlayer>> PLAYERS = Maps.newConcurrentMap();

    public RemotePlayer(UUID id) {
        super(id);
    }

    public static RemotePlayer getOrCreatePlayer(int clientId, UUID id) {
        if (PLAYERS.containsKey(id)) {
            Pair<Integer, RemotePlayer> pair = PLAYERS.get(id);
            if (pair.getKey() == clientId)
                return pair.getValue();
            return null;
        }
        RemotePlayer player = new RemotePlayer(id);
        PLAYERS.put(id, Pair.of(clientId, player));
        return player;
    }
}
