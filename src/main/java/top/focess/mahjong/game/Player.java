package top.focess.mahjong.game;

import java.util.UUID;

public class Player {

    private final UUID id = UUID.randomUUID();

    private Game game;

    public Game getGame() {
        return this.game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public boolean leave() {
        return this.getGame() != null && this.getGame().leave(this);
    }

    public boolean join(Game game) {
        return game.join(this);
    }

    public UUID getId() {
        return this.id;
    }
}
