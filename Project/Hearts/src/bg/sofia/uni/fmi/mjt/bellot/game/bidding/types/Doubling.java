package bg.sofia.uni.fmi.mjt.bellot.game.bidding.types;

import bg.sofia.uni.fmi.mjt.bellot.game.exceptions.InvalidConversionException;

import java.util.List;

public enum Doubling {
    NONE(1),
    CONTRA(2),
    RE_CONTRA(4);

    private final int multiplier;

    Doubling(int multiplier) {
        this.multiplier = multiplier;
    }

    public static List<Doubling> getDoublingSequence() {
        return List.of(CONTRA, RE_CONTRA);
    }

    public static Doubling of(PlayerContractOption option) {
        return switch (option) {
            case CONTRA -> CONTRA;
            case RE_CONTRA -> RE_CONTRA;
            default -> throw new InvalidConversionException(option + "is not convertable to Doubling");
        };
    }

    public int getMultiplier() {
        return multiplier;
    }
}
