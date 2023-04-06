package top.focess.mahjong.game.remote;

import com.google.common.collect.Maps;
import top.focess.mahjong.game.packet.GameActionStatusPacket;
import top.focess.scheduler.ThreadPoolScheduler;

import java.util.Map;
import java.util.UUID;

public class GameRequester {

    private static final Map<UUID, GameRequester> GAME_REQUESTER_MAP = Maps.newConcurrentMap();
    private final UUID gameId;
    private final Map<String, GameRequest> gameRequests = Maps.newHashMap();

    private static final Object LOCK = new Object();

    public GameRequester(UUID gameId) {
        this.gameId = gameId;
        GAME_REQUESTER_MAP.put(gameId, this);
    }

    public static GameRequester getGameRequester(UUID gameId) {
        return GAME_REQUESTER_MAP.get(gameId);
    }

    public UUID getGameId() {
        return gameId;
    }

    public GameActionStatusPacket.GameActionStatus request(String action, UUID playerId) {
        GameRequest gameRequest;
        synchronized (LOCK) {
            if (gameRequests.containsKey(action))
                gameRequest = gameRequests.get(action);
            else {
                gameRequest = new GameRequest(new Object(), playerId);
                gameRequests.put(action, gameRequest);
            }
        }
        synchronized (gameRequest.getLock()) {
            try {
                gameRequest.getLock().wait(5000, 0);
            } catch (InterruptedException ignored) {
            }
            return gameRequest.getStatus();
        }
    }

    public void response(String action, UUID playerId, GameActionStatusPacket.GameActionStatus status) {
        synchronized (LOCK) {
            GameRequest gameRequest = this.gameRequests.get(action);
            if (gameRequest != null && gameRequest.getPlayerId().equals(playerId))
                synchronized (gameRequest.getLock()) {
                    gameRequest.setStatus(status);
                    gameRequest.getLock().notifyAll();
                }
        }
    }

    public static class GameRequest {

        private final Object lock;
        private final UUID playerId;
        private GameActionStatusPacket.GameActionStatus status = GameActionStatusPacket.GameActionStatus.UNKNOWN;

        public GameRequest(Object lock, UUID playerId) {
            this.lock = lock;
            this.playerId = playerId;
        }

        public Object getLock() {
            return lock;
        }

        public GameActionStatusPacket.GameActionStatus getStatus() {
            return status;
        }

        public void setStatus(GameActionStatusPacket.GameActionStatus status) {
            this.status = status;
        }

        public UUID getPlayerId() {
            return playerId;
        }
    }
}
