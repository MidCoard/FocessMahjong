package top.focess.mahjong.game.rule;

import top.focess.mahjong.game.rule.manager.GameManager;
import top.focess.mahjong.game.rule.manager.SiChuanGameManager;

public enum MahjongRule {

    SICHUAN("SiChuanMahjong") {
        @Override
        public int getReadyTime(int size) {
            return switch (size) {
                case 2 -> 10;
                case 3 -> 15;
                case 4 -> 20;
                default -> -1;
            };
        }

        @Override
        public GameManager getGameManager(int playerSize) {
            return new SiChuanGameManager(playerSize);
        }
    };

    private final String name;

    MahjongRule(String name) {
        this.name = name;
    }

    public boolean checkPlayerSize(int size) {
        return size <= 4;
    }

    public abstract int getReadyTime(int size);

    public abstract GameManager getGameManager(int playerSize);

    public String getName() {
        return name;
    }
}
