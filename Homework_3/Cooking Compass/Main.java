import bg.sofia.uni.fmi.mjt.cookingcompass.client.Client;
import bg.sofia.uni.fmi.mjt.cookingcompass.client.EdemamClient;
import bg.sofia.uni.fmi.mjt.cookingcompass.exceptions.BadRequestException;
import bg.sofia.uni.fmi.mjt.cookingcompass.exceptions.ConnectionException;
import bg.sofia.uni.fmi.mjt.cookingcompass.exceptions.NoMoreDataException;
import bg.sofia.uni.fmi.mjt.cookingcompass.recipe.Recipe;
import bg.sofia.uni.fmi.mjt.cookingcompass.request.EdemamRequest;
import bg.sofia.uni.fmi.mjt.cookingcompass.request.Request;
import bg.sofia.uni.fmi.mjt.cookingcompass.types.query.Health;
import bg.sofia.uni.fmi.mjt.cookingcompass.types.query.MealType;

import java.util.List;

public class Main {
    public static void main(String[] args) throws BadRequestException, ConnectionException, NoMoreDataException {

        Request request = EdemamRequest
                .builder()
                .setWordParams("chicken")
                .setMealTypeParams(List.of(MealType.LUNCH_DINNER, MealType.TEATIME)).setHealthParams(Health.FISH_FREE)
                .build();

        System.out.println(request.buildURI());
//
        Client<Recipe> client = EdemamClient.builder(request).build();

        client.loadMore();

        List<Recipe> recipes = client.getData();

        client.loadMore(1);

        System.out.println(recipes.size());
    }
}

