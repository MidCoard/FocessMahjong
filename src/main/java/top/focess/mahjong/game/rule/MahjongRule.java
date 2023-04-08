package top.focess.mahjong.game.rule;

public enum MahjongRule {

    SICHUAN("SiChuanMahjong",108) {
        @Override
        public int getReadyTime(int size) {
            return switch (size) {
                case 2 -> 10;
                case 3 -> 15;
                case 4 -> 20;
                default -> -1;
            };
        }
    };

    private final String name;
    private final int tileSize;

    MahjongRule(String name, int tileSize) {
        this.name = name;
        this.tileSize = tileSize;
    }

    public int getTileSize() {
        return this.tileSize;
    }

    public boolean checkPlayerSize(int size) {
        return size <= 4;
    }

    public abstract int getReadyTime(int size);

    public String getName() {
        return name;
    }
}
