package bg.sofia.uni.fmi.mjt.bellot.game.bidding.types;

import bg.sofia.uni.fmi.mjt.bellot.game.exceptions.InvalidConversionException;

import java.util.List;

public enum Bidding {
    SPADES(16, 162),
    HEARTS(16, 162),
    DIAMONDS(16, 162),
    CLUBS(16, 162),
    NO_TRUMPS(26, 260),
    ALL_TRUMPS(26, 258);

    private final int points;
    private final int cardValues;

    Bidding(int points, int cardsValues) {
        this.points = points;
        this.cardValues = cardsValues;
    }

    public static List<Bidding> getBiddingSequence() {
        return List.of(SPADES, DIAMONDS, HEARTS, CLUBS, NO_TRUMPS, ALL_TRUMPS);
    }

    public static Bidding of(PlayerContractOption option) {
        return switch (option) {
            case SPADES -> SPADES;
            case CLUBS -> CLUBS;
            case HEARTS -> HEARTS;
            case DIAMONDS -> DIAMONDS;
            case NO_TRUMPS -> NO_TRUMPS;
            case ALL_TRUMPS -> ALL_TRUMPS;
            default -> throw new InvalidConversionException(option + "is not convertable to Bidding");
        };
    }

    public int getPoints() {
        return points;
    }
}
