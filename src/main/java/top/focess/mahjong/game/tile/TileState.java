package top.focess.mahjong.game.tile;

public enum TileState {

    ONE_DOTS(TileStateCategory.DOTS),
    TWO_DOTS(TileStateCategory.DOTS),
    THREE_DOTS(TileStateCategory.DOTS),
    FOUR_DOTS(TileStateCategory.DOTS),
    FIVE_DOTS(TileStateCategory.DOTS),
    SIX_DOTS(TileStateCategory.DOTS),
    SEVEN_DOTS(TileStateCategory.DOTS),
    EIGHT_DOTS(TileStateCategory.DOTS),
    NINE_DOTS(TileStateCategory.DOTS),


    ONE_BAMBOO(TileStateCategory.BAMBOO),
    TWO_BAMBOO(TileStateCategory.BAMBOO),
    THREE_BAMBOO(TileStateCategory.BAMBOO),
    FOUR_BAMBOO(TileStateCategory.BAMBOO),
    FIVE_BAMBOO(TileStateCategory.BAMBOO),
    SIX_BAMBOO(TileStateCategory.BAMBOO),
    SEVEN_BAMBOO(TileStateCategory.BAMBOO),
    EIGHT_BAMBOO(TileStateCategory.BAMBOO),
    NINE_BAMBOO(TileStateCategory.BAMBOO),

    ONE_CHARACTERS(TileStateCategory.CHARACTERS),
    TWO_CHARACTERS(TileStateCategory.CHARACTERS),
    THREE_CHARACTERS(TileStateCategory.CHARACTERS),
    FOUR_CHARACTERS(TileStateCategory.CHARACTERS),
    FIVE_CHARACTERS(TileStateCategory.CHARACTERS),
    SIX_CHARACTERS(TileStateCategory.CHARACTERS),
    SEVEN_CHARACTERS(TileStateCategory.CHARACTERS),
    EIGHT_CHARACTERS(TileStateCategory.CHARACTERS),
    NINE_CHARACTERS(TileStateCategory.CHARACTERS),

    EAST_WIND,
    SOUTH_WIND,
    WEST_WIND,
    NORTH_WIND,

    RED_DRAGON,
    GREEN_DRAGON,
    WHITE_DRAGON,


    ;

    private final TileStateCategory category;

    TileState() {
        this(null);
    }

    TileState(final TileStateCategory category) {
        this.category = category;
    }

    public TileStateCategory getCategory() {
        return this.category;
    }

    public enum TileStateCategory {
        DOTS,
        BAMBOO,
        CHARACTERS,
    }
}
