package top.focess.mahjong.game.data;

import top.focess.mahjong.game.Game;
import top.focess.mahjong.game.rule.MahjongRule;

import java.util.List;
import java.util.UUID;

public class GameData {

    private final UUID id;
    private final MahjongRule rule;
    private final Game.GameState gameState;
    private final TilesData tilesData;
    private final List<PlayerData> playerData;

    public GameData(UUID id, MahjongRule rule, Game.GameState gameState, TilesData tilesData, List<PlayerData> playerData) {
        this.id = id;
        this.rule = rule;
        this.gameState = gameState;
        this.tilesData = tilesData;
        this.playerData = playerData;
    }

    public UUID getId() {
        return id;
    }

    public MahjongRule getRule() {
        return rule;
    }

    public Game.GameState getGameState() {
        return gameState;
    }

    public TilesData getTilesData() {
        return tilesData;
    }

    public List<PlayerData> getPlayerData() {
        return playerData;
    }
}
