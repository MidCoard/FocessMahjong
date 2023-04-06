package top.focess.mahjong.game;

import com.google.common.collect.Lists;
import top.focess.mahjong.game.data.GameData;
import top.focess.mahjong.game.rule.MahjongRule;

import java.util.List;

public class LocalGame extends Game {

    private final List<Player> players = Lists.newArrayList();

    public LocalGame(MahjongRule rule) {
        super(rule);
    }

    public boolean join(Player player) {
        if (this.rule.checkPlayerSize(players.size() + 1))
            return false;
        if (player.getGame() != null)
            return false;
        this.players.add(player);
        player.setGame(this);
        return true;
    }

    public boolean leave(Player player) {
        if (player.getGame() != this)
            return false;
        this.players.remove(player);
        player.setGame(null);
        return true;
    }

    @Override
    public GameData getGameData() {
        return new GameData(this.getId(), this.rule, this.gameState, null, this.players.stream().map(Player::getPlayerData).toList());
    }

    @Override
    protected void update(GameData gameData) {
        // ignore all the data update
    }
}
