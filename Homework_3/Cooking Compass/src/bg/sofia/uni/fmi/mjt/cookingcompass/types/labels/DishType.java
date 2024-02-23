package bg.sofia.uni.fmi.mjt.cookingcompass.types.labels;

import bg.sofia.uni.fmi.mjt.cookingcompass.types.TypeAPI;

public enum DishType implements TypeAPI {

    ALCOHOL_COCKTAIL("alcohol cocktail"),
    BISCUITS_AND_COOKIES("biscuits and cookies"),
    BREAD("bread"),
    CEREALS("cereals"),
    CONDIMENTS_AND_SAUCES("condiments and sauces"),
    DESSERTS("desserts"),
    DRINKS("drinks"),
    EGG("egg"),
    ICE_CREAM_AND_CUSTARD("ice cream and custard"),
    MAIN_COURSE("main course"),
    PANCAKE("pancake"),
    PASTA("pasta"),
    PASTRY("pastry"),
    PIES_AND_TARTS("pies and tarts"),
    PIZZA("pizza"),
    PREPS("preps"),
    PRESERVE("preserve"),
    SALAD("salad"),
    SANDWICHES("sandwiches"),
    SEAFOOD("seafood"),
    SIDE_DISH("side dish"),
    SOUP("soup"),
    SPECIAL_OCCASIONS("special occasions"),
    STARTER("starter"),
    SWEETS("sweets");

    private final String value;

    DishType(String value) {
        this.value = value;
    }

    public static DishType of(String s) {
        for (DishType value : DishType.values()) {
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
