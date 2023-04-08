package top.focess.mahjong.game.data;

import top.focess.mahjong.game.Game;
import top.focess.mahjong.game.rule.MahjongRule;

import java.util.List;
import java.util.UUID;

public record GameData(UUID id, MahjongRule rule, Game.GameState gameState, int startTime, TilesData tilesData,
                       List<PlayerData> playerData) {

}
