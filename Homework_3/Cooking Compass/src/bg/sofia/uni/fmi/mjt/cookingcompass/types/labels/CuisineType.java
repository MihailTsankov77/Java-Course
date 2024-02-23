package bg.sofia.uni.fmi.mjt.cookingcompass.types.labels;

import bg.sofia.uni.fmi.mjt.cookingcompass.types.TypeAPI;

public enum CuisineType implements TypeAPI {
    AMERICAN("american"),
    ASIAN("asian"),
    BRITISH("british"),
    CARIBBEAN("caribbean"),
    CENTRAL_EUROPE("central europe"),
    CHINESE("chinese"),
    EASTERN_EUROPE("eastern europe"),
    FRENCH("french"),
    GREEK("greek"),
    INDIAN("indian"),
    ITALIAN("italian"),
    JAPANESE("japanese"),
    KOREAN("korean"),
    KOSHER("kosher"),
    MEDITERRANEAN("mediterranean"),
    MEXICAN("mexican"),
    MIDDLE_EASTERN("middle eastern"),
    NORDIC("nordic"),
    SOUTH_AMERICAN("south american"),
    SOUTH_EAST_ASIAN("south east asian"),
    WORLD("world");

    private final String value;

    CuisineType(String value) {
        this.value = value;
    }

    public static CuisineType of(String s) {
        for (CuisineType value : CuisineType.values()) {
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