package top.focess.mahjong.game;

public enum GameTileState {

    SHUFFLING(10), // tiles are in shuffling
    CHANGE_3_TILES(10), // change 3 tiles
    DISCARDING(10), // which means tiles are discard
    CONDITION(5), // which means tiles are in condition (like pong, kong, chow)

    WIN(3), // which means tiles are in player's hand and player win
    ALL_WIN(5), // which means tiles are in player's hand and player win all
    NO_WIN(5), // which means tiles are in player's hand and player win no one
    WAITING(3), // which means some essential waiting for UI or other

    ;

    private final int time;

    GameTileState(int time) {
        this.time = time;
    }

    public int getTime() {
        return time;
    }
}
