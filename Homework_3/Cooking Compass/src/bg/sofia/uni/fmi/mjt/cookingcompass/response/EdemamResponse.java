package bg.sofia.uni.fmi.mjt.cookingcompass.response;

import bg.sofia.uni.fmi.mjt.cookingcompass.recipe.Recipe;
import bg.sofia.uni.fmi.mjt.cookingcompass.request.NextPageRequest;
import bg.sofia.uni.fmi.mjt.cookingcompass.request.Request;
import bg.sofia.uni.fmi.mjt.cookingcompass.response.wrappers.RecipeWrapper;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class EdemamResponse implements PaginationResponse<Recipe> {

    private final Request nextPageRequest;
    private final List<Recipe> data;

    private static final Gson GSON = new Gson();

    private EdemamResponse(Request nextPageRequest, List<Recipe> data) {
        this.nextPageRequest = nextPageRequest;
        this.data = data;
    }

    private static Request parseNextPage(JsonElement jsonParser) {
        JsonElement nextPageURI = jsonParser
                .getAsJsonObject()
                .get("_links")
                .getAsJsonObject()
                .get("next")
                .getAsJsonObject()
                .get("href");

        if (nextPageURI.isJsonNull()) {
            return null;
        }

        return new NextPageRequest(nextPageURI.getAsString());
    }

    public static EdemamResponse of(String rawJson) {
        JsonElement jsonParser = JsonParser.parseString(rawJson);

        JsonElement hits = jsonParser.getAsJsonObject().get("hits").getAsJsonArray();

        Type type = new TypeToken<List<RecipeWrapper>>() {
        }.getType();

        List<RecipeWrapper> wrappedRecipes = GSON.fromJson(hits, type);

        List<Recipe> recipes = wrappedRecipes.stream().map(RecipeWrapper::unwrap).toList();

        return new EdemamResponse(parseNextPage(jsonParser), recipes);
    }

    public boolean hasNext() {
        return nextPageRequest != null;
    }

    public Request getNextPageRequest() {
        return nextPageRequest;
    }

    public List<Recipe> getData() {
        return data;
    }
}
