package top.focess.mahjong.terminal.command.converter;

import top.focess.command.converter.ExceptionDataConverter;
import top.focess.mahjong.game.tile.TileState;

public class TileStateConverter extends ExceptionDataConverter<TileState> {

    public static final TileStateConverter TILE_STATE_CONVERTER = new TileStateConverter();

    @Override
    public TileState convert(String arg) {
        return TileState.valueOf(arg.toUpperCase());
    }

    @Override
    protected Class<TileState> getTargetClass() {
        return TileState.class;
    }
}
