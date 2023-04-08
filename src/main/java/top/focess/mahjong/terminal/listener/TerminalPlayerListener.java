package top.focess.mahjong.terminal.listener;

import top.focess.mahjong.game.Player;

public interface TerminalPlayerListener<T> {

    void onChanged(Player player, T oldValue, T newValue);
}
