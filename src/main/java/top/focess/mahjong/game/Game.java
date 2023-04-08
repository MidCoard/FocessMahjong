package top.focess.mahjong.game;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import top.focess.mahjong.game.data.GameData;
import top.focess.mahjong.game.remote.GameRequester;
import top.focess.mahjong.game.rule.MahjongRule;
import top.focess.mahjong.terminal.TerminalLauncher;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public abstract class Game {

    protected static final Map<UUID, Game> GAMES = Maps.newConcurrentMap();
    protected final GameRequester gameRequester = new GameRequester();
    private int startTime = -1;
    private int gameTime = 0;
    private int countdown = -1;
    private GameState gameState = GameState.WAITING;

    private final MahjongRule rule;
    private final UUID id;

    protected final List<Player> players = Lists.newArrayList();

    public Game(MahjongRule rule) {
        this(UUID.randomUUID(), rule);
    }

    public Game(UUID id,MahjongRule rule) {
        this.id = id;
        this.rule = rule;
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

    public int getStartTime() {
        return startTime;
    }

    public void setGameState(GameState gameState) {
        if (this.gameState != gameState) {
            TerminalLauncher.change("gameState", this, this.gameState, gameState);
            this.gameState = gameState;
        }
    }

    public UUID getId() {
        return this.id;
    }

    public GameData getGameData() {
        return new GameData(this.getId(), this.rule, this.gameState, this.startTime,null, this.players.stream().map(Player::getPlayerData).toList());
    }

    public MahjongRule getRule() {
        return this.rule;
    }

    public GameRequester getGameRequester() {
        return this.gameRequester;
    }

    protected void countdownStartTime() {
        this.setStartTime(this.getStartTime() - 1);
    }

    protected void setStartTime(int startTime) {
        if (this.startTime != startTime) {
            TerminalLauncher.change("startTime",this, this.startTime, startTime);
            this.startTime = startTime;
        }
    }

    public int getGameTime() {
        return gameTime;
    }

    public void setGameTime(int gameTime) {
        if (this.gameTime != gameTime) {
            TerminalLauncher.change("gameTime", this, this.gameTime, gameTime);
            this.gameTime = gameTime;
        }
    }

    public int getCountdown() {
        return countdown;
    }

    public void setCountdown(int countdown) {
        if (this.countdown != countdown) {
            TerminalLauncher.change("countdown", this, this.countdown, countdown);
            this.countdown = countdown;
        }
    }

    public enum GameState {

        WAITING, // setup is done, waiting for players to join and ready.
        PLAYING; // game is playing. including the shuffling tiles, dealing tiles, playing tiles, and game over.

    }

    @Override
    public String toString() {
        return "Game{" +
                "gameState=" + gameState +
                ", rule=" + rule +
                ", id=" + id +
                '}' + super.toString();
    }
}
