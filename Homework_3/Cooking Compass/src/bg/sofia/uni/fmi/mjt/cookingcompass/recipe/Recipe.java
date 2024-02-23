package bg.sofia.uni.fmi.mjt.cookingcompass.recipe;

import bg.sofia.uni.fmi.mjt.cookingcompass.types.labels.CuisineType;
import bg.sofia.uni.fmi.mjt.cookingcompass.types.labels.Diet;
import bg.sofia.uni.fmi.mjt.cookingcompass.types.labels.DishType;
import bg.sofia.uni.fmi.mjt.cookingcompass.types.labels.MealTypeLabel;
import bg.sofia.uni.fmi.mjt.cookingcompass.types.query.Health;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public record Recipe(
        String label, List<Diet> dietLabels,
        List<Health> healthLabels, double totalWeight,
        List<CuisineType> cuisineType, List<MealTypeLabel> mealType,
        List<DishType> dishType, List<String> ingredientLines
) {

    private static <T> List<T> parseNullableList(List<String> data, Function<String, T> parser) {
        return Optional.ofNullable(data)
                .map(list -> list.stream().map(parser).toList()).orElse(null);
    }

    public static Recipe of(BaseRecipe baseRecipe) {
        List<Diet> dietLabels = parseNullableList(baseRecipe.dietLabels(), Diet::of);
        List<Health> healthLabels = parseNullableList(baseRecipe.healthLabels(), Health::of);
        List<CuisineType> cuisineType = parseNullableList(baseRecipe.cuisineType(), CuisineType::of);
        List<MealTypeLabel> mealType = parseNullableList(baseRecipe.mealType(), MealTypeLabel::of);
        List<DishType> dishType = parseNullableList(baseRecipe.dishType(), DishType::of);

        return new Recipe(baseRecipe.label(), dietLabels, healthLabels, baseRecipe.totalWeight(),
                cuisineType, mealType, dishType, baseRecipe.ingredientLines());
    }
}
