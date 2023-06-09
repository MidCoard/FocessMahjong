package top.focess.mahjong.game.tile;

import com.google.common.collect.Sets;

import java.util.Set;

public class Tiles {


	private final Tile[] tiles;

	private int pointer;

	public Tiles(final int tileSize) {
		this.tiles = new Tile[tileSize];
		for (int i = 0; i < tileSize; i++)
			this.tiles[i] = new Tile(i + 1);
	}

	public Set<Tile> fetch(final int size) {
		if (this.pointer + size > this.tiles.length)
			throw new IndexOutOfBoundsException("The pointer is out of the tileStates");
		final Set<Tile> tiles = Sets.newHashSet();
		for (int i = 0; i < size; i++)
			tiles.add(this.tiles[this.pointer++]);
		return tiles;
	}

	public Tile fetch() {
		final Tile tile = this.tiles[this.pointer++];
		if (this.pointer == this.tiles.length)
			tile.addDetail(Tile.LAST_TILE);
		return tile;
	}

	public int getRemainSize() {
		return this.tiles.length - this.pointer;
	}

	public Tile getTile(final int i) {
		return this.tiles[i];
	}
}
