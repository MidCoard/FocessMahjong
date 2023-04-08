package top.focess.mahjong.terminal.listener;

import top.focess.mahjong.game.Game;

public interface TerminalGameListener<T> {

    void onChanged(Game game, T oldValue, T newValue);

}
