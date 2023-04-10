package top.focess.mahjong.game.rule.manager;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.checkerframework.checker.nullness.qual.NonNull;
import top.focess.mahjong.game.GameTileState;
import top.focess.mahjong.game.LocalGame;
import top.focess.mahjong.game.data.TilesData;
import top.focess.mahjong.game.packet.Change3TilesDirectionPacket;
import top.focess.mahjong.game.packet.DiscardTilePacket;
import top.focess.mahjong.game.packet.KongPacket;
import top.focess.mahjong.game.tile.PlayerTiles;
import top.focess.mahjong.game.tile.Tile;
import top.focess.mahjong.game.tile.TileState;
import top.focess.mahjong.game.tile.Tiles;
import top.focess.mahjong.terminal.TerminalLauncher;

import java.util.*;

public class SiChuanGameManager extends GameManager {

    private final int playerSize;
    private final LocalGame game;
    private GameTileState previousGameTileState = null;
    private GameTileState gameTileState = GameTileState.SHUFFLING;
    private int countdown = gameTileState.getTime();
    private final Tiles tiles = new Tiles(108);
    private final List<PlayerTiles> playerTiles = Lists.newArrayList();

    private final List<Map<LocalGame.TileAction, List<Object>>> actions = Lists.newArrayList();

    private int current = -1;

    private Tile currentTile;

    private int dealer = -1;

    private final Random random = new Random();

    public SiChuanGameManager(LocalGame game, int playerSize) {
        this.game = game;
        this.playerSize = playerSize;
        // shuffling tiles
        List<TileState> tileStates = Lists.newArrayList();
        for (int i = 0; i < 27; i++)
            for (int j = 0; j < 4; j++)
                tileStates.add(TileState.values()[i]);
        Collections.shuffle(tileStates);
        for (int i = 0; i < 108; i++)
            this.tiles.getTile(i).setTileState(tileStates.get(i));

        for (int i = 0; i < playerSize; i++)
            this.playerTiles.add(new PlayerTiles());

        for (int i = 0; i < playerSize; i++)
            this.actions.add(Maps.newHashMap());


        // we ignore the random tiles because shuffling tiles is enough
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < playerSize; j++)
                this.playerTiles.get(j).addTile(this.tiles.fetch(4));

        this.playerTiles.get(0).addTile(this.tiles.fetch(1));
        for (int i = 1; i < playerSize; i++)
            this.playerTiles.get(i).addTile(this.tiles.fetch(1));
        this.playerTiles.get(0).addTile(this.tiles.fetch(1));
    }

    public synchronized void tick() {
        if (this.countdown > 0)
            this.countdown--;
        if (this.countdown == 0) {
            this.previousGameTileState = this.gameTileState;
            this.gameTileState = calculateNextState();
            this.countdown = gameTileState.getTime();
        }
    }

    private @NonNull GameTileState calculateNextState() {
        if (this.gameTileState == GameTileState.SHUFFLING)
            return GameTileState.CHANGE_3_TILES;
        else if (this.gameTileState == GameTileState.CHANGE_3_TILES) {
            List<Set<Tile>> tiles = Lists.newArrayList();
            for (int i = 0; i < this.playerTiles.size(); i++) {
                if (actions.get(i).getOrDefault(LocalGame.TileAction.CHANGE_3_TILES, List.of()).size() == 0) {
                    PlayerTiles playerTiles = this.playerTiles.get(i);
                    TileState.TileStateCategory category = playerTiles.getLeastCategory(3);
                    List<Integer> indexes = playerTiles.getRandomIndexes(3, category); // can be fixed. if we have multiple tiles with the same category, we should choose the one with the most effective tiles
                    actions.get(i).put(LocalGame.TileAction.CHANGE_3_TILES, List.of(indexes.get(0), indexes.get(1), indexes.get(2)));
                }
                List<Integer> indexes = Lists.newArrayList();
                for (Object o : actions.get(i).get(LocalGame.TileAction.CHANGE_3_TILES))
                    indexes.add((Integer) o);
                tiles.add(this.playerTiles.get(i).getAndRemoveTiles(indexes));
            }
            int dir = random.nextInt(this.playerSize - 1);
            if (dir == 0)
                for (int i = 0; i < this.playerTiles.size(); i++)
                    this.playerTiles.get(i).addTile(tiles.get((i + 1) % this.playerSize));
            else if (dir == 1)
                for (int i = 0; i < this.playerTiles.size(); i++)
                    this.playerTiles.get(i).addTile(tiles.get((i + this.playerSize - 1) % this.playerSize));
            else if (dir == 2)
                for (int i = 0; i < this.playerTiles.size(); i++)
                    this.playerTiles.get(i).addTile(tiles.get((i + this.playerSize - 2) % this.playerSize));
            TerminalLauncher.change("changeDirection", this.game,-1, dir);
            this.game.sendPacket(new Change3TilesDirectionPacket(this.game.getId(), dir));
            return GameTileState.WAITING;
        } else if (this.gameTileState == GameTileState.DISCARDING) {
            this.game.sendPacket(new DiscardTilePacket(this.game.getPlayerId(this.current), this.game.getId(),  this.currentTile.getTileState()));
            return GameTileState.WAITING;
        } else if (this.gameTileState == GameTileState.WAITING) {
            if (this.previousGameTileState == GameTileState.CHANGE_3_TILES) {
                this.current = (this.current + 1) % this.playerTiles.size();
                this.currentTile = this.tiles.fetch();
                return GameTileState.DISCARDING;
            } else if (this.previousGameTileState == GameTileState.DISCARDING)
                return GameTileState.CONDITION;
            else if (this.previousGameTileState == GameTileState.CONDITION)
                return GameTileState.DISCARDING;
        }
        throw new IllegalStateException("The gameTileState is illegal");
    }

    public int getCountdown() {
        return this.countdown;
    }

    @Override
    public TilesData getTilesData(int player) {
        if (player >= this.playerTiles.size() || player < 0)
            return null;
        return new TilesData(this.tiles.getRemainSize(), this.playerTiles.get(player).getRawTileStates(), this.gameTileState, this.playerTiles.stream().map(PlayerTiles::getDiscardTileStates).toList(), this.playerTiles.stream().map(PlayerTiles::getScore).toList());
    }

    @Override
    public GameTileState getGameTileState() {
        return this.gameTileState;
    }

    @Override
    public synchronized void doTileAction(LocalGame.TileAction tileAction, int player, Object... objects) {
        if (player >= this.playerTiles.size() || player < 0)
            throw new IndexOutOfBoundsException("The player is out of the playerTiles");
        if (tileAction == LocalGame.TileAction.CHANGE_3_TILES) {
            if (this.gameTileState != GameTileState.CHANGE_3_TILES)
                return;
            if (objects.length != 3)
                return;
            for (Object object : objects)
                if (!(object instanceof Integer))
                    return;
            for (Object object : objects)
                if ((Integer) object < 0 || (Integer) object >= this.playerTiles.get(player).getTilesSize())
                    return;
            TileState.TileStateCategory category = this.playerTiles.get(player).getTileState((Integer) objects[0]).getCategory();
            for (int i = 1; i < 3; i++)
                if (this.playerTiles.get(player).getTileState((Integer) objects[i]).getCategory() != category)
                    return;
            actions.get(player).put(tileAction, Lists.newArrayList(objects));
        } else if (tileAction == LocalGame.TileAction.KONG) {
            // discarding and 4 tiles with the same state
            // discarding and 3 tiles with the same state and 1 tile with the same state
            // condition and 4 tiles with the same state
            if (this.gameTileState != GameTileState.DISCARDING && this.gameTileState != GameTileState.CONDITION)
                return;
            if (this.gameTileState == GameTileState.DISCARDING && this.current != player)
                return;
            if (this.gameTileState == GameTileState.CONDITION && this.current == player)
                return;
            if (objects.length != 1)
                return;
            if (!(objects[0] instanceof TileState))
                return;
            int count = this.playerTiles.get(player).getTileStateCount((TileState) objects[0]);
            if (count < 3)
                return;
            if (count < 4 && this.currentTile.getTileState() != objects[0])
                return;
            // force stop discarding or condition
            if (this.gameTileState == GameTileState.DISCARDING) {
                if (this.playerTiles.get(player).getHandTileStateCount((TileState) objects[0]) >= 3) {
                    this.playerTiles.get(player).addScore((int) (this.playerTiles.stream().filter(PlayerTiles::isHu).count() * 2));
                    this.playerTiles.stream().filter(PlayerTiles::isHu).forEach(playerTiles -> playerTiles.addScore(-2));
                } else {
                    this.playerTiles.get(player).addScore((int) (this.playerTiles.stream().filter(PlayerTiles::isHu).count()));
                    this.playerTiles.stream().filter(PlayerTiles::isHu).forEach(playerTiles -> playerTiles.addScore(-1));
                }
            } else {
                this.playerTiles.get(player).addScore(2);
                this.playerTiles.get(this.current).addScore(-2);
            }
            this.playerTiles.get(player).addTile(this.currentTile);
            this.playerTiles.get(player).kong((TileState) objects[0]);
            this.game.sendPacket(new KongPacket(this.game.getId(), this.game.getPlayerId(player), (TileState) objects[0]));
            this.previousGameTileState = this.gameTileState;
            this.gameTileState = GameTileState.WAITING;
            this.countdown = gameTileState.getTime();
        } else if (tileAction == LocalGame.TileAction.DISCARD_TILE) {
            if (this.gameTileState != GameTileState.DISCARDING)
                return;
            if (this.current != player)
                return;
            if (objects.length != 1)
                return;
            if (!(objects[0] instanceof TileState))
                return;
            if (this.playerTiles.get(player).getHandTileStateCount((TileState) objects[0]) == 0 && !this.currentTile.getTileState().equals(objects[0]))
                return;
            // force stop discarding
            this.playerTiles.get(player).addTile(this.currentTile);
            this.playerTiles.get(player).discard((TileState) objects[0]);
            this.previousGameTileState = this.gameTileState;
            this.gameTileState = GameTileState.WAITING;
            this.countdown = gameTileState.getTime();
        } else if (tileAction == LocalGame.TileAction.HU) {
            if (this.gameTileState != GameTileState.DISCARDING && this.gameTileState != GameTileState.CONDITION)
                return;
            if (this.gameTileState == GameTileState.DISCARDING && this.current != player)
                return;
            if (this.gameTileState == GameTileState.CONDITION && this.current == player)
                return;
            if (objects.length != 0)
                return;
            if (!this.playerTiles.get(player).huable(this.currentTile.getTileState()))
                return;
            // force stop discarding or condition
            this.playerTiles.get(player).hu(this.currentTile);
            this.previousGameTileState = this.gameTileState;
            this.gameTileState = GameTileState.WAITING;
            this.countdown = gameTileState.getTime();
        }
    }

    public int getCurrent() {
        return current;
    }

    @Override
    public TileState getCurrentTileState() {
        return this.currentTile.getTileState();
    }

    public int getDealer() {
        return dealer;
    }
}
