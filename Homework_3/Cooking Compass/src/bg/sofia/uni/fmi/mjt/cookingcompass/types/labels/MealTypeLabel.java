package bg.sofia.uni.fmi.mjt.cookingcompass.types.labels;

import bg.sofia.uni.fmi.mjt.cookingcompass.types.TypeAPI;

public enum MealTypeLabel implements TypeAPI {
    BREAKFAST("breakfast"),
    BRUNCH("brunch"),
    LUNCH_DINNER("lunch/dinner"),
    SNACK("snack"),
    TEATIME("teatime");

    private final String value;

    MealTypeLabel(String value) {
        this.value = value;
    }

    public static MealTypeLabel of(String s) {
        for (MealTypeLabel value : MealTypeLabel.values()) {
            if (value.value().equals(s)) {
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
