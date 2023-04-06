package top.focess.mahjong.game;

import top.focess.mahjong.game.rule.MahjongRule;

import java.util.UUID;

public abstract class Game {

    protected final MahjongRule rule;
    protected GameState gameState;
    private final UUID id;

    public Game(MahjongRule rule) {
        this(UUID.randomUUID(), rule, GameState.NEW);
    }

    public Game(UUID id,MahjongRule rule, GameState gameState) {
        this.id = id;
        this.rule = rule;
        this.gameState = gameState;
    }

    public abstract boolean join(Player player);

    public abstract boolean leave(Player player);

    public GameState getGameState() {
        return gameState;
    }

    public UUID getId() {
        return this.id;
    }
}
