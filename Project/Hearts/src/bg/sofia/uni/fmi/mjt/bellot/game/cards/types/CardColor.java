package bg.sofia.uni.fmi.mjt.bellot.game.cards.types;

import bg.sofia.uni.fmi.mjt.bellot.game.bidding.types.Bidding;

public enum CardColor {

    SPADE(" ♠️"),
    HEART(" ❤️"),
    DIAMONDS(" ♦️"),

    CLUB(" ♣️");

    final String value;

    CardColor(String value) {
        this.value = value;
    }

    public static CardColor of(Bidding bidding) {
        return switch (bidding) {
            case SPADES -> SPADE;
            case CLUBS -> CLUB;
            case HEARTS -> HEART;
            case DIAMONDS -> DIAMONDS;
            case NO_TRUMPS, ALL_TRUMPS -> null;
        };
    }

    public String getValue() {
        return value;
    }
}
