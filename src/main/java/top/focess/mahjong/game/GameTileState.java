package top.focess.mahjong.game;

public enum GameTileState {

	SHUFFLING(10), // tileStates are in shuffling
	CHANGING_3_TILES(10), // change 3 tileStates
	LARKING_1_SUIT(5),
	DISCARDING(10), // which means tileStates are discard
	CONDITION(5), // which means tileStates are in condition (like pong, kong, chow, hu)
	CONDITION_HU(5), // which means player can hu
	FINISHED(20),
	WAITING(3), // which means some essential waiting for UI or other

	WAITING_HU(3);

	private final int time;

	GameTileState(final int time) {
		this.time = time;
	}

	public int getTime() {
		return this.time;
	}
}
