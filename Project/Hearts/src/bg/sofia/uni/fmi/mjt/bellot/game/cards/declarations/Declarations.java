package bg.sofia.uni.fmi.mjt.bellot.game.cards.declarations;

import bg.sofia.uni.fmi.mjt.bellot.game.cards.types.CardColor;

import java.util.List;

public enum Declarations {
    BELLOT_SPADES(20),
    BELLOT_HEARTS(20),
    BELLOT_DIAMONDS(20),
    BELLOT_CLUB(20),
    CARRE_HUNDRED(100),
    CARRE_HUNDRED_AN_FIFTY(150),
    CARRE_TWO_HUNDRED(200),
    SEQUENCE_TIERCE(20),
    SEQUENCE_QUARTE(50),
    SEQUENCE_QUINTE(100);

    public static final List<Declarations> BELLOT =
            List.of(BELLOT_CLUB, BELLOT_DIAMONDS, BELLOT_DIAMONDS, BELLOT_SPADES);
    private final int points;

    Declarations(int points) {
        this.points = points;
    }

    public static Declarations of(Carre carre) {
        return switch (carre) {
            case HUNDRED -> CARRE_HUNDRED;
            case HUNDRED_AN_FIFTY -> CARRE_HUNDRED_AN_FIFTY;
            case TWO_HUNDRED -> CARRE_TWO_HUNDRED;
        };
    }

    public static Declarations of(Sequence sequence) {
        return switch (sequence) {
            case TIERCE -> SEQUENCE_TIERCE;
            case QUARTE -> SEQUENCE_QUARTE;
            case QUINTE -> SEQUENCE_QUINTE;
        };
    }

    public static Declarations of(CardColor color) {
        return switch (color) {
            case CLUB -> BELLOT_CLUB;
            case HEART -> BELLOT_HEARTS;
            case SPADE -> BELLOT_SPADES;
            case DIAMONDS -> BELLOT_DIAMONDS;
        };
    }

    public int getPoints() {
        return points;
    }

}
