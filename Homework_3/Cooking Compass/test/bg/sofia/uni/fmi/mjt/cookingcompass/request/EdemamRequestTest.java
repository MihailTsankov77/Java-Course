package bg.sofia.uni.fmi.mjt.cookingcompass.request;

import bg.sofia.uni.fmi.mjt.cookingcompass.types.query.Health;
import bg.sofia.uni.fmi.mjt.cookingcompass.types.query.MealType;
import bg.sofia.uni.fmi.mjt.cookingcompass.types.query.RecipeType;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EdemamRequestTest {

    @Test
    public void testBaseRequestIsBuildProperly() {
        Request request = EdemamRequest.builder().build();
        String expected = "https://api.edamam.com/api/recipes/v2?type=any&app_id=d7291f1e&app_key=65314bd650c86a1f6ad1a450013b7324";
        assertEquals(expected, request.buildURI(), "Test without unnecessary params");
    }

    @Test
    public void testRequestWithOneParamIsBuildProperly() {
        Request request1 = EdemamRequest.builder().setWordParams("chicken").build();
        String expected1 = "https://api.edamam.com/api/recipes/v2?type=any&app_id=d7291f1e&app_key=65314bd650c86a1f6ad1a450013b7324&q=chicken";
        assertEquals(expected1, request1.buildURI(), "Test with only words");

        Request request2 = EdemamRequest.builder().setMealTypeParams(MealType.LUNCH_DINNER).build();
        String expected2 = "https://api.edamam.com/api/recipes/v2?type=any&app_id=d7291f1e&app_key=65314bd650c86a1f6ad1a450013b7324&mealType=lunch-dinner";
        assertEquals(expected2, request2.buildURI(), "Test with only meal type");

        Request request3 = EdemamRequest.builder().setHealthParams(Health.FISH_FREE).build();
        String expected3 = "https://api.edamam.com/api/recipes/v2?type=any&app_id=d7291f1e&app_key=65314bd650c86a1f6ad1a450013b7324&health=fish-free";
        assertEquals(expected3, request3.buildURI(), "Test with only health type");
    }

    private static Set<String> getQueryParams(String s) {
        String query = s.split("\\?")[1]; // get query
        List<String> queries = List.of(query.split("&"));

        return queries
                .stream()
                .filter(q -> !q.contains("type") && !q.contains("app_id") && !q.contains("app_key"))
                .map(q->q.split("=")[1])
                .collect(Collectors.toSet());
    }


    @Test
    public void testRequestWithMoreParamIsBuildProperly() {
        Request request1 = EdemamRequest.builder().setWordParams(List.of("chicken", "bob")).build();
        assertEquals(
                Set.of("chicken", "bob"),
                getQueryParams(request1.buildURI()),
                "Test with more words"
        );

        Request request2 = EdemamRequest.builder().setMealTypeParams(List.of(MealType.LUNCH_DINNER, MealType.TEATIME)).build();
        assertEquals(
                Set.of(MealType.LUNCH_DINNER.value(), MealType.TEATIME.value()),
                getQueryParams(request2.buildURI()),
                "Test with more meal type"
        );

        Request request3 = EdemamRequest.builder().setHealthParams(List.of(Health.FISH_FREE, Health.ALCOHOL_FREE)).build();
        assertEquals(
                Set.of(Health.FISH_FREE.value(), Health.ALCOHOL_FREE.value()),
                getQueryParams(request3.buildURI()),
                "Test with more health type"
        );
    }

    @Test
    public void testRequestChangeTypeIsBuildProperly() {
        Request request = EdemamRequest.builder(RecipeType.PUBLIC).build();
        String expected = "https://api.edamam.com/api/recipes/v2?type=public&app_id=d7291f1e&app_key=65314bd650c86a1f6ad1a450013b7324";
        assertEquals(expected, request.buildURI(), "Test change type");
    }

    @Test
    public void testRequestMultipleQueryIsBuildProperly() {
        Request request = EdemamRequest
                .builder()
                .setWordParams("chicken")
                .setMealTypeParams(MealType.LUNCH_DINNER)
                .setHealthParams(Health.FISH_FREE)
                .build();
        ;
        String expected = "https://api.edamam.com/api/recipes/v2?type=any&app_id=d7291f1e&app_key=65314bd650c86a1f6ad1a450013b7324&q=chicken&health=fish-free&mealType=lunch-dinner";

        assertEquals(expected, request.buildURI(), "Test with a lot of params");
    }


}
