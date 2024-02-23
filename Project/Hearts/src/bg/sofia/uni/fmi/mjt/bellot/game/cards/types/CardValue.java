package bg.sofia.uni.fmi.mjt.bellot.game.cards.types;

import java.util.List;

public enum CardValue {

    ACE("A", 11, 11),
    SEVEN("7", 0, 0),
    EIGHT("8", 0, 0),
    NINE("9", 0, 14),
    TEN("10", 10, 10),
    JACK("J", 2, 20),
    QUEEN("Q", 3, 3),
    KING("K", 4, 4);

    public static final List<CardValue> NO_TRUMPS_CARD_SEQUENCE =
            List.of(SEVEN, EIGHT, NINE, TEN, JACK, QUEEN, KING, ACE);

    public static final List<CardValue> TRUMPS_CARD_SEQUENCE =
            List.of(SEVEN, EIGHT, TEN, QUEEN, KING, ACE, NINE, JACK);
    final String value;
    final int points;
    final int trumpPoints;

    CardValue(String value, int points, int trumpPoints) {
        this.value = value;
        this.points = points;
        this.trumpPoints = trumpPoints;
    }

    public String getValue() {
        return value;
    }

    public int getPoints(boolean isTrumpColor) {
        return isTrumpColor ? trumpPoints : points;
    }
}
