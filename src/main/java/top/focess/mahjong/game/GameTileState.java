package top.focess.mahjong.game;

public enum GameTileState {

    SHUFFLING(10), // tileStates are in shuffling
    CHANGING_3_TILES(10), // change 3 tileStates
    LARKING_1_SUIT(5),
    DISCARDING(10), // which means tileStates are discard
    CONDITION(5), // which means tileStates are in condition (like pong, kong, chow)

    WIN(3), // which means tileStates are in player's hand and player win
    ALL_WIN(5), // which means tileStates are in player's hand and player win all
    NO_WIN(5), // which means tileStates are in player's hand and player win no one
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
