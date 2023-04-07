package top.focess.mahjong.terminal.command.converter;

import top.focess.command.converter.ExceptionDataConverter;
import top.focess.mahjong.game.rule.MahjongRule;

public class MahjongRuleConverter extends ExceptionDataConverter<MahjongRule> {

    public static final MahjongRuleConverter MAHJONG_RULE_CONVERTER = new MahjongRuleConverter();

    @Override
    public MahjongRule convert(String arg) {
        return MahjongRule.valueOf(arg.toUpperCase());
    }

    @Override
    protected Class<MahjongRule> getTargetClass() {
        return MahjongRule.class;
    }
}
