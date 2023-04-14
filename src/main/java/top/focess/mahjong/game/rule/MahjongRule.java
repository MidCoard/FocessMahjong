package top.focess.mahjong.game.rule;

import top.focess.mahjong.game.LocalGame;
import top.focess.mahjong.game.rule.manager.GameManager;
import top.focess.mahjong.game.rule.manager.SiChuanGameManager;

public enum MahjongRule {

	SICHUAN("SiChuanMahjong") {
		@Override
		public int getReadyTime(final int size) {
			return switch (size) {
//				case 2 -> 20;
				case 2 -> 4;
				case 3 -> 15;
				case 4 -> 10;
				default -> -1;
			};
		}

		@Override
		public GameManager getGameManager(final LocalGame game, final int playerSize) {
			return new SiChuanGameManager(game, playerSize, score -> score + 1);
		}
	},

	SICHUAN2("SiChuanMahjong") {
		@Override
		public int getReadyTime(final int size) {
			return switch (size) {
				case 2 -> 20;
				case 3 -> 15;
				case 4 -> 10;
				default -> -1;
			};
		}

		@Override
		public GameManager getGameManager(final LocalGame game, final int playerSize) {
			return new SiChuanGameManager(game, playerSize, score -> score == 16 ? 16 : score * 2);
		}
	};

	private final String name;

	MahjongRule(final String name) {
		this.name = name;
	}

	public boolean checkPlayerSize(final int size) {
		return size <= 4;
	}

	public abstract GameManager getGameManager(LocalGame game, int playerSize);

	public String getName() {
		return this.name;
	}

	public abstract int getReadyTime(int size);
}
