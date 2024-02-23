package bg.sofia.uni.fmi.mjt.cookingcompass.types.labels;

import bg.sofia.uni.fmi.mjt.cookingcompass.types.TypeAPI;

public enum Diet implements TypeAPI {
    BALANCED("Balanced"),
    HIGH_FIBER("High-Fiber"),
    HIGH_PROTEIN("High-Protein"),
    LOW_CARB("Low-Carb"),
    LOW_FAT("Low-Fat"),
    LOW_SODIUM("Low-Sodium");

    private final String value;

    Diet(String value) {
        this.value = value;
    }

    public static Diet of(String s) {
        for (Diet value : Diet.values()) {
            if (value.value().equals(s)) {
                return value;
            }
        }
        return null;
    }

    @Override
    public String value() {
        return value;
    }
}
