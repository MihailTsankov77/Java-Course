package bg.sofia.uni.fmi.mjt.cookingcompass.response.wrappers;

import bg.sofia.uni.fmi.mjt.cookingcompass.recipe.BaseRecipe;
import bg.sofia.uni.fmi.mjt.cookingcompass.recipe.Recipe;

public class RecipeWrapper implements Wrapper<Recipe> {
    private BaseRecipe recipe;

    @Override
    public Recipe unwrap() {
        return Recipe.of(recipe);
    }
}
