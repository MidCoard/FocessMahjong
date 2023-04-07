package top.focess.mahjong.terminal.command.data;

import org.checkerframework.checker.nullness.qual.NonNull;
import top.focess.command.data.DataBuffer;
import top.focess.command.data.StringBuffer;
import top.focess.mahjong.game.rule.MahjongRule;

public class MahjongRuleBuffer extends DataBuffer<MahjongRule> {

    private final StringBuffer stringBuffer;

    private MahjongRuleBuffer(int size) {
        this.stringBuffer = StringBuffer.allocate(size);
    }

    @Override
    public void flip() {
        stringBuffer.flip();
    }

    @Override
    public void put(MahjongRule rule) {
        stringBuffer.put(rule.name());
    }

    @Override
    public @NonNull MahjongRule get() {
        return MahjongRule.valueOf(stringBuffer.get());
    }

    @Override
    public @NonNull MahjongRule get(int index) {
        return MahjongRule.valueOf(stringBuffer.get(index));
    }

    public static MahjongRuleBuffer allocate(int size) {
        return new MahjongRuleBuffer(size);
    }
}
