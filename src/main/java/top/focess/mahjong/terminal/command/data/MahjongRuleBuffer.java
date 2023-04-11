package top.focess.mahjong.terminal.command.data;

import org.checkerframework.checker.nullness.qual.NonNull;
import top.focess.command.data.DataBuffer;
import top.focess.command.data.StringBuffer;
import top.focess.mahjong.game.rule.MahjongRule;

public class MahjongRuleBuffer extends DataBuffer<MahjongRule> {

	private final StringBuffer stringBuffer;

	private MahjongRuleBuffer(final int size) {
		this.stringBuffer = StringBuffer.allocate(size);
	}

	public static MahjongRuleBuffer allocate(final int size) {
		return new MahjongRuleBuffer(size);
	}

	@Override
	public void flip() {
		this.stringBuffer.flip();
	}

	@Override
	public void put(final MahjongRule rule) {
		this.stringBuffer.put(rule.name());
	}

	@Override
	public @NonNull MahjongRule get() {
		return MahjongRule.valueOf(this.stringBuffer.get());
	}

	@Override
	public @NonNull MahjongRule get(final int index) {
		return MahjongRule.valueOf(this.stringBuffer.get(index));
	}
}
