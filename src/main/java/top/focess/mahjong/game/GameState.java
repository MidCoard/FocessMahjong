package top.focess.mahjong.game;

public enum GameState {

    NEW, // The game is just created, no setup is done.
    WAITING, // setup is done, waiting for players to join and ready.
    PLAYING; // game is playing. including the shuffling tiles, dealing tiles, playing tiles, and game over.

}
