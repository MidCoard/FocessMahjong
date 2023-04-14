package top.focess.mahjong.game.tile;

import org.jetbrains.annotations.NotNull;

public class Tile implements Comparable<Tile> {

	public static final int AFTER_KONG_DISCARDED_TILE = 0b100;
	public static final int AFTER_KONG_FETCHED_TILE = 0b10;
	public static final int HU_TILE =    0b100000;
	public static final int KONG_TILE =    0b1000;
	public static final int LAST_TILE = 0b1000000;
	public static final int NORMAL_KONG_TILE = 0b1;
	public static final int PUNG_TILE = 0b10000;
	private final int number;

	private TileState tileState;

	private int detail;

	public Tile(final int number) {
		this.number = number;
	}

	public void addDetail(final int detail) {
		this.detail |= detail;
	}

	@Override
	public int compareTo(@NotNull final Tile o) {
		return this.getTileState().compareTo(o.getTileState());
	}

	public TileState getTileState() {
		return this.tileState;
	}

	public void setTileState(final TileState tileState) {
		this.tileState = tileState;
	}

	public int getNumber() {
		return this.number;
	}

	@Override
	public int hashCode() {
		return this.number;
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) return true;
		if (null == o || this.getClass() != o.getClass()) return false;

		final Tile tile = (Tile) o;

		return this.number == tile.number;
	}

	public boolean isDetail(final int detail) {
		return 0 != (this.detail & detail);
	}
}
