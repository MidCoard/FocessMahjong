package top.focess.mahjong.game.remote;

import com.google.common.collect.Maps;
import top.focess.mahjong.game.packet.GameActionStatusPacket;

import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class GameRequester {

    private final Map<String, GameRequest> gameRequests = Maps.newHashMap();

    private static final Object LOCK = new Object();


    public <T> T request(final String action, final Runnable task, final Object... args) {
        final GameRequest gameRequest;
        synchronized (GameRequester.LOCK) {
            if (this.gameRequests.containsKey(action))
                gameRequest = this.gameRequests.get(action);
            else {
                gameRequest = new GameRequest(args);
                this.gameRequests.put(action, gameRequest);
            }
        }
        task.run();
        synchronized (gameRequest.getLock()) {
            try {
                gameRequest.getLock().wait(5000, 0);
            } catch (final InterruptedException ignored) {
            }
            return gameRequest.getResponse();
        }
    }

    public void response(final String action, final Object arg) {
        this.response(action, arg, objects -> true);
    }

    public void response(final String action, final Object arg, final Predicate<Object[]> predicate) {
        synchronized (GameRequester.LOCK) {
            final GameRequest gameRequest = this.gameRequests.get(action);
            if (null != gameRequest && predicate.test(gameRequest.getArgs())) {
                this.gameRequests.remove(action);
                synchronized (gameRequest.getLock()) {
                    gameRequest.setResponse(arg);
                    gameRequest.getLock().notifyAll();
                }
            }
        }
    }

    public static class GameRequest {
        private final Object lock = new Object();
        private final Object[] args;
        private Object response;

        public GameRequest(final Object... args) {
            this.args = args;
        }

        public Object getLock() {
            return this.lock;
        }
        public void setResponse(final Object arg) {
            this.response = arg;
        }

        public <T> T getResponse() {
            return (T) this.response;
        }

        public Object[] getArgs() {
            return this.args;
        }
    }
}
