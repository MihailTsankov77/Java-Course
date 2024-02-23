package bg.sofia.uni.fmi.mjt.cookingcompass.types.query;

import bg.sofia.uni.fmi.mjt.cookingcompass.types.TypeAPI;

public enum MealType implements TypeAPI {
    BREAKFAST("breakfast"),
    BRUNCH("brunch"),
    LUNCH_DINNER("lunch-dinner"),
    SNACK("snack"),
    TEATIME("teatime");

    private final String value;

    MealType(String value) {
        this.value = value;
    }

    public static MealType of(String s) {
        for (MealType value : MealType.values()) {
            if (value.value.equals(s)) {
                return value;
            }
        }
        return null;
    }

    @Override
    public String value() {
        return value.toLowerCase();
    }
}
