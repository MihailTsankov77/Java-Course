package bg.sofia.uni.fmi.mjt.cookingcompass.types.query;

import bg.sofia.uni.fmi.mjt.cookingcompass.types.TypeAPI;

public enum RecipeType implements TypeAPI {
    PUBLIC("public"),
    USER("user"),
    ANY("any");

    private final String value;

    RecipeType(String value) {
        this.value = value;
    }

    @Override
    public String value() {
        return value.toLowerCase();
    }
}
