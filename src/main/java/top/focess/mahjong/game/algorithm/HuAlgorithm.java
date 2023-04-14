package top.focess.mahjong.game.algorithm;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import top.focess.mahjong.game.tile.Tile;
import top.focess.mahjong.game.tile.TileState;
import top.focess.util.Pair;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HuAlgorithm {

	public static final int HU_TYPE_LOW = 0b1;
	public static final int HU_TYPE_ALL_TRIPLETS = 0b10;
	public static final int HU_TYPE_SEVEN_PAIRS = 0b100;
	public static final int HU_TYPE_ONE_FISH = 0b1000;
	public static final int HU_TYPE_ONE_SUIT = 0b10000;
	public static final int HU_TYPE_ROBBING_THE_KONG = 0b100000;
	public static final int HU_TYPE_DRAWING_THE_KONG = 0b1000000;
	public static final int HU_TYPE_LAST_TILE =       0b10000000;
	public static final int HU_TYPE_ROOT_AREA =    0b11100000000;


	public static int calculateHuScore(int type) {
		int score = 0;
		if (0 != (type & HuAlgorithm.HU_TYPE_LOW))
			score = 1;
		if (0 != (type & HuAlgorithm.HU_TYPE_ALL_TRIPLETS))
			score = 2;
		if (0 != (type & HuAlgorithm.HU_TYPE_SEVEN_PAIRS))
			score = 4;
		if (0 != (type & HuAlgorithm.HU_TYPE_ONE_FISH))
			score = 4;
		if (0 != (type & HuAlgorithm.HU_TYPE_ONE_SUIT))
			score *= 4;
		if (0 != (type & HuAlgorithm.HU_TYPE_ROBBING_THE_KONG))
			score *= 2;
		if (0 != (type & HuAlgorithm.HU_TYPE_DRAWING_THE_KONG))
			score *= 2;
		final int rootCount = type & HuAlgorithm.HU_TYPE_ROOT_AREA;
		for (int i = 0; i < rootCount; i++)
			score *= 2;
		return score;
	}

	public static int calculateHuType(final Set<Tile> tiles, final Set<Tile> noDiscardTiles, @Nullable final Tile tile) {
		int type = 0;
		final List<TileState> tileStateList = Lists.newArrayList();
		final List<TileState> handTileStateList = Lists.newArrayList();
		for (final Tile t : tiles) {
			tileStateList.add(t.getTileState());
			handTileStateList.add(t.getTileState());
		}
		for (final Tile t : noDiscardTiles)
			tileStateList.add(t.getTileState());
		if (null != tile) {
			tileStateList.add(tile.getTileState());
			handTileStateList.add(tile.getTileState());
		}
		if (3 == tileStateList.stream().map(TileState::getCategory).distinct().count())
			return type;
		final Map<TileState, Integer> map = Maps.newHashMap();
		final Map<TileState, Integer> handMap = Maps.newTreeMap();
		for (final TileState tileState : tileStateList)
			map.compute(tileState, (__, integer) -> null == integer ? 1 : integer + 1);
		for (final TileState tileState : handTileStateList)
			handMap.compute(tileState, (__, integer) -> null == integer ? 1 : integer + 1);
		if (!HuAlgorithm.calcHuable(handMap))
			if (14 != handTileStateList.size() || !handMap.values().stream().allMatch(i -> 4 == i || 2 == i))
				return type;
		if (1 == handMap.size() && handMap.values().stream().allMatch(i -> 2 == i))
			type |= HuAlgorithm.HU_TYPE_ONE_FISH;
		else if (handMap.values().stream().allMatch(i -> 3 == i || 2 == i))
			type |= HuAlgorithm.HU_TYPE_ALL_TRIPLETS;
		else if (14 == handMap.size() && handMap.values().stream().allMatch(i -> 4 == i || 2 == i))
			type |= HuAlgorithm.HU_TYPE_SEVEN_PAIRS;
		else type |= HuAlgorithm.HU_TYPE_LOW;
		final TileState.TileStateCategory category = tileStateList.stream().findAny().get().getCategory();
		if (tileStateList.stream().allMatch(i -> i.getCategory() == category))
			type |= HuAlgorithm.HU_TYPE_ONE_SUIT;
		if (null != tile) {
			if (tile.isDetail(Tile.AFTER_KONG_FETCHED_TILE) || tile.isDetail(Tile.AFTER_KONG_DISCARDED_TILE))
				type |= HuAlgorithm.HU_TYPE_DRAWING_THE_KONG;
			if (tile.isDetail(Tile.NORMAL_KONG_TILE))
				type |= HuAlgorithm.HU_TYPE_ROBBING_THE_KONG;
			if (tile.isDetail(Tile.LAST_TILE))
				type |= HuAlgorithm.HU_TYPE_LAST_TILE;
		}
		final int rootCount = (int) map.values().stream().filter(i -> 4 == i).count();
		type |= (rootCount << 7);
		return type;
	}

	private static boolean calcHuable(final Map<TileState, Integer> map) {
		final List<MutablePair<TileState, Integer>> list = Lists.newArrayList();
		int hand = 0;
		for (final Map.Entry<TileState, Integer> entry : map.entrySet()) {
			list.add(MutablePair.of(entry.getKey(), entry.getValue()));
			hand += entry.getValue();
		}
		return HuAlgorithm.dfsCalcHuable(list, 1, (hand - 2) / 3);
	}

	private static boolean dfsCalcHuable(final List<MutablePair<TileState, Integer>> list, final int pairs, final int sequences) {
		if (0 == pairs && 0 == sequences)
			return true;
		for (int i = 0;i < list.size();i++) {
			final MutablePair<TileState, Integer> pair = list.get(i);
			if (1 <= pair.getSecond() && 0 < sequences) {
				if (i + 2 < list.size()) {
					if (list.get(i + 1).getSecond() > 0 && list.get(i + 2).getSecond() > 0) {
						pair.setSecond(pair.getSecond() - 1);
						list.get(i + 1).setSecond(list.get(i + 1).getSecond() - 1);
						list.get(i + 2).setSecond(list.get(i + 2).getSecond() - 1);
						if (HuAlgorithm.dfsCalcHuable(list, pairs, sequences - 1))
							return true;
						pair.setSecond(pair.getSecond() + 1);
						list.get(i + 1).setSecond(list.get(i + 1).getSecond() + 1);
						list.get(i + 2).setSecond(list.get(i + 2).getSecond() + 1);
					}
				}
			}
			if (2 <= pair.getSecond() && 0 < pairs) {
				pair.setSecond(pair.getSecond() - 2);
				if (HuAlgorithm.dfsCalcHuable(list, pairs - 1, sequences))
					return true;
				pair.setSecond(pair.getSecond() + 2);
			}
			if (3 <= pair.getSecond() && 0 < sequences) {
				pair.setSecond(pair.getSecond() - 3);
				if (HuAlgorithm.dfsCalcHuable(list, pairs, sequences - 1))
					return true;
				pair.setSecond(pair.getSecond() + 3);
			}
		}
		return false;
	}

	public static void main(String[] args) {
		// test huable

	}

	public static class MutablePair<T, U> {
		public T first;
		public U second;

		public MutablePair(final T first, final U second) {
			this.first = first;
			this.second = second;
		}

		public static <T, U> MutablePair<T, U> of(final T first, final U second) {
			return new MutablePair<>(first, second);
		}

		public static <T, U> MutablePair<T, U> of(final Pair<T, U> pair) {
			return new MutablePair<>(pair.getFirst(), pair.getSecond());
		}

		public T getFirst() {
			return this.first;
		}

		public void setFirst(final T first) {
			this.first = first;
		}

		public U getSecond() {
			return this.second;
		}

		public void setSecond(final U second) {
			this.second = second;
		}
	}
}
