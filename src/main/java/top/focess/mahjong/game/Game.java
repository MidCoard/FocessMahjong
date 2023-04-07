package top.focess.mahjong.game;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import top.focess.mahjong.game.data.GameData;
import top.focess.mahjong.game.remote.RemotePlayer;
import top.focess.mahjong.game.rule.MahjongRule;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public abstract class Game {

    private static final Map<UUID, Game> GAMES = Maps.newConcurrentMap();

    protected MahjongRule rule;
    protected GameState gameState;
    private final UUID id;

    protected final List<Player> players = Lists.newArrayList();

    public Game(MahjongRule rule) {
        this(UUID.randomUUID(), rule, GameState.NEW);
    }

    public Game(UUID id,MahjongRule rule, GameState gameState) {
        this.id = id;
        this.rule = rule;
        this.gameState = gameState;
        GAMES.put(id, this);
    }

    public static Game getGame(UUID gameId) {
        return GAMES.get(gameId);
    }

    public static List<Game> getGames() {
        return Collections.unmodifiableList(Lists.newArrayList(GAMES.values()));
    }

    public abstract boolean join(Player player);

    public abstract boolean leave(Player player);

    public abstract boolean ready(Player player);

    public abstract boolean unready(Player player);

    public GameState getGameState() {
        return gameState;
    }

    public UUID getId() {
        return this.id;
    }

    public GameData getGameData() {
        return new GameData(this.getId(), this.rule, this.gameState, null, this.players.stream().map(Player::getPlayerData).toList());
    }

    public enum GameState {

        NEW, // The game is just created, no setup is done.
        WAITING, // setup is done, waiting for players to join and ready.
        PLAYING; // game is playing. including the shuffling tiles, dealing tiles, playing tiles, and game over.

    }
}
