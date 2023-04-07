package top.focess.mahjong.game.remote;

import com.google.common.collect.Maps;
import top.focess.mahjong.game.packet.GameActionStatusPacket;

import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.function.Supplier;

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

    public <T> T request(String action, Object... args) {
        GameRequest gameRequest;
        synchronized (LOCK) {
            if (gameRequests.containsKey(action))
                gameRequest = gameRequests.get(action);
            else {
                gameRequest = new GameRequest(new Object(), args);
                gameRequests.put(action, gameRequest);
            }
        }
        synchronized (gameRequest.getLock()) {
            try {
                gameRequest.getLock().wait(5000, 0);
            } catch (InterruptedException ignored) {
            }
            return gameRequest.getResponse();
        }
    }

    public void response(String action, Object arg) {
        response(action, arg, objects -> true);
    }

    public void response(String action, Object arg, Predicate<Object[]> predicate) {
        synchronized (LOCK) {
            GameRequest gameRequest = this.gameRequests.get(action);
            if (gameRequest != null && predicate.test(gameRequest.getArgs()))
                synchronized (gameRequest.getLock()) {
                    gameRequest.setResponse(arg);
                    gameRequest.getLock().notifyAll();
                }
        }
    }

    public static class GameRequest {
        private final Object lock;
        private final Object[] args;
        private Object response;

        public GameRequest(Object lock, Object... args) {
            this.lock = lock;
            this.args = args;
        }

        public Object getLock() {
            return lock;
        }
        public void setResponse(Object arg) {
            this.response = arg;
        }

        public <T> T getResponse() {
            return (T) response;
        }

        public Object[] getArgs() {
            return args;
        }
    }
}
