package top.focess.mahjong.game.rule;

public enum MahjongRule {

    SICHUAN(108);

    private final int tileSize;

    MahjongRule(int tileSize) {
        this.tileSize = tileSize;
    }

    public int getTileSize() {
        return this.tileSize;
    }

    public boolean checkPlayerSize(int size) {
        return size <= 4;
    }
}
