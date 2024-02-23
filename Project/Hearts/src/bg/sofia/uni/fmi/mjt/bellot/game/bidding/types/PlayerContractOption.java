package bg.sofia.uni.fmi.mjt.bellot.game.bidding.types;

import bg.sofia.uni.fmi.mjt.bellot.game.exceptions.InvalidConversionException;

public enum PlayerContractOption {
    SPADES,
    HEARTS,
    DIAMONDS,
    CLUBS,
    NO_TRUMPS,
    ALL_TRUMPS,
    CONTRA,
    RE_CONTRA,
    PASS;

    public static PlayerContractOption of(Bidding bidding) {
        return switch (bidding) {
            case SPADES -> SPADES;
            case CLUBS -> CLUBS;
            case HEARTS -> HEARTS;
            case DIAMONDS -> DIAMONDS;
            case NO_TRUMPS -> NO_TRUMPS;
            case ALL_TRUMPS -> ALL_TRUMPS;
        };
    }

    public static PlayerContractOption of(Doubling doubling) throws InvalidConversionException {
        return switch (doubling) {
            case CONTRA -> CONTRA;
            case RE_CONTRA -> RE_CONTRA;
            default -> throw new InvalidConversionException(doubling + "is not convertable to PlayerContractOption");
        };
    }
}
