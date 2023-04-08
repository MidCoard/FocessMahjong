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


    public <T> T request(String action, Runnable task, Object... args) {
        GameRequest gameRequest;
        synchronized (LOCK) {
            if (gameRequests.containsKey(action))
                gameRequest = gameRequests.get(action);
            else {
                gameRequest = new GameRequest(args);
                gameRequests.put(action, gameRequest);
            }
        }
        task.run();
        synchronized (gameRequest.getLock()) {
            try {
                System.out.println("Start lock: " + System.currentTimeMillis());
                gameRequest.getLock().wait(10000, 0);
                System.out.println("End lock: " + System.currentTimeMillis());
            } catch (InterruptedException ignored) {
            }
            return gameRequest.getResponse();
        }
    }

    public void response(String action, Object arg) {
        response(action, arg, objects -> true);
    }

    public void response(String action, Object arg, Predicate<Object[]> predicate) {
        System.out.println("Begin Response: " + System.currentTimeMillis());
        synchronized (LOCK) {
            GameRequest gameRequest = this.gameRequests.get(action);
            if (gameRequest != null && predicate.test(gameRequest.getArgs())) {
                System.out.println("Response: " + System.currentTimeMillis());
                synchronized (gameRequest.getLock()) {
                    gameRequest.setResponse(arg);
                    gameRequest.getLock().notifyAll();
                    System.out.println("End Response: " + System.currentTimeMillis());
                }
            }
        }
    }

    public static class GameRequest {
        private final Object lock = new Object();
        private final Object[] args;
        private Object response;

        public GameRequest(Object... args) {
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
