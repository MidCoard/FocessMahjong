package top.focess.mahjong.game.remote;

import com.google.common.collect.Maps;
import top.focess.mahjong.Launcher;
import top.focess.mahjong.game.Player;
import top.focess.mahjong.game.packet.SyncPlayerPacket;

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

    public int getClientId() {
        return clientId;
    }
}
