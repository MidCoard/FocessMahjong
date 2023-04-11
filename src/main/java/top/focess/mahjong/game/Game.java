package top.focess.mahjong.game;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import top.focess.mahjong.game.data.GameData;
import top.focess.mahjong.game.packet.GameTileActionPacket;
import top.focess.mahjong.game.remote.GameRequester;
import top.focess.mahjong.game.remote.RemotePlayer;
import top.focess.mahjong.game.rule.MahjongRule;
import top.focess.mahjong.game.tile.TileState;
import top.focess.mahjong.terminal.TerminalLauncher;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public abstract class Game {

    protected static final Map<UUID, Game> GAMES = Maps.newConcurrentMap();
    protected final GameRequester gameRequester = new GameRequester();
    private int startTime = -1;
    private int gameTime;
    private int countdown = -1;
    private GameState gameState = GameState.WAITING;

    private final MahjongRule rule;
    private final UUID id;

    protected final List<Player> players = Lists.newArrayList();

    public Game(final MahjongRule rule) {
        this(UUID.randomUUID(), rule);
    }

    public Game(final UUID id, final MahjongRule rule) {
        this.id = id;
        this.rule = rule;
        Game.GAMES.put(id, this);
    }

    public static Game getGame(final UUID gameId) {
        return Game.GAMES.get(gameId);
    }

    public static List<Game> getGames() {
        return Collections.unmodifiableList(Lists.newArrayList(Game.GAMES.values()));
    }

    public abstract boolean join(Player player);

    public abstract boolean leave(Player player);

    public abstract boolean ready(Player player);

    public abstract boolean unready(Player player);

    public GameState getGameState() {
        return this.gameState;
    }

    public int getStartTime() {
        return this.startTime;
    }

    public void setGameState(final GameState gameState) {
        if (this.gameState != gameState) {
            TerminalLauncher.change("gameState", this, this.gameState, gameState);
            this.gameState = gameState;
        }
    }

    public UUID getId() {
        return this.id;
    }

    public GameData getGameData() {
        return new GameData(this.getId(), this.rule, this.gameState, this.startTime, this.getGameTime(), this.getCountdown(), null, this.players.stream().map(Player::getPlayerData).toList());
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

    protected void setStartTime(final int startTime) {
        if (this.startTime != startTime) {
            TerminalLauncher.change("startTime",this, this.startTime, startTime);
            this.startTime = startTime;
        }
    }

    public int getGameTime() {
        return this.gameTime;
    }

    public void setGameTime(final int gameTime) {
        if (this.gameTime != gameTime) {
            TerminalLauncher.change("gameTime", this, this.gameTime, gameTime);
            this.gameTime = gameTime;
        }
    }

    public int getCountdown() {
        return this.countdown;
    }

    public void setCountdown(final int countdown) {
        if (this.countdown != countdown) {
            TerminalLauncher.change("countdown", this, this.countdown, countdown);
            this.countdown = countdown;
        }
    }

    protected void tickGameTime() {
        this.setGameTime(this.getGameTime() + 1);
    }

    public abstract GameTileState getGameTileState();

    public abstract void doTileAction(GameTileActionPacket.TileAction tileAction, Player player, TileState... tileStates);

    public abstract void larkSuit(RemotePlayer player, TileState.TileStateCategory category);

    public enum GameState {

        WAITING, // setup is done, waiting for players to join and ready.
        PLAYING // game is playing. including the shuffling tileStates, dealing tileStates, playing tileStates, and game over.

    }

    @Override
    public String toString() {
        return "Game{" +
                "gameState=" + this.gameState +
                ", rule=" + this.rule +
                ", id=" + this.id +
                '}' + super.toString();
    }
}
