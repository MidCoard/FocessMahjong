package top.focess.mahjong.game.data;

import top.focess.mahjong.game.Player;

import java.util.UUID;

public record PlayerData(UUID id, String name, Player.PlayerState playerState, UUID gameId) {}
