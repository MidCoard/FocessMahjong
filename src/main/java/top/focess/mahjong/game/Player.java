package top.focess.mahjong.game;

import top.focess.mahjong.game.data.PlayerData;
import top.focess.mahjong.game.remote.RemotePlayer;
import top.focess.mahjong.terminal.TerminalLauncher;

import java.util.UUID;

public class Player {

    private final UUID id;
    private final String name;

    private Game game;

    private PlayerState playerState = PlayerState.WAITING;

    public Player() {
        this(UUID.randomUUID());
    }

    public Player(UUID id) {
        this(id, "Player" + id.toString().substring(0, 4));
    }

    public Player(UUID id, String name) {
        this.id = id;
        this.name = name;
    }

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

    public PlayerData getPlayerData() {
        return new PlayerData(this.id, this.name, playerState, this.game == null ? null : this.game.getId());
    }

    public PlayerState getPlayerState() {
        return playerState;
    }

    public void setPlayerState(PlayerState playerState) {
        if (this.playerState != playerState) {
            TerminalLauncher.change("playerState", this, this.playerState, playerState);
            this.playerState = playerState;
        }
    }

    public static Player getPlayer(int clientId, PlayerData playerData) {
        if (LocalPlayer.localPlayer.getId().equals(playerData.id()))
            return LocalPlayer.localPlayer;
        return RemotePlayer.getOrCreatePlayer(clientId, playerData);
    }

    public String getName() {
        return this.name;
    }

    public enum PlayerState {

        WAITING,
        READY,
        PLAYING;
    }
}
