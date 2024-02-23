package bg.sofia.uni.fmi.mjt.cookingcompass.response;

import bg.sofia.uni.fmi.mjt.cookingcompass.recipe.Recipe;
import bg.sofia.uni.fmi.mjt.cookingcompass.request.NextPageRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EdemamResponseTest {

    private static final String HITS = """
                 "hits":[
                 {
                     "recipe":{
                        "label":"Bob's Chicken Melt Sandwich",
                        "dietLabels":[
                           "High-Fiber"
                        ],
                        "healthLabels":[
                           "Egg-Free",
                           "Peanut-Free",
                           "Tree-Nut-Free",
                           "Soy-Free",
                           "Fish-Free",
                           "Shellfish-Free",
                           "Crustacean-Free",
                           "Celery-Free",
                           "Mustard-Free",
                           "Sesame-Free",
                           "Lupine-Free",
                           "Mollusk-Free",
                           "Alcohol-Free",
                           "Sulfite-Free"
                        ],
                        "ingredientLines":[
                           "1 boneless chicken breast",
                           "2 slices sourdough bread",
                           "1 slice provolone cheese",
                           "2 slices bacon",
                           "2 tablespoons. spinach artichoke dip",
                           "2 tablespoons butter (enough to spread on each slice of bread)"
                        ],
                        "totalWeight":692.5249999995244,
                        "cuisineType":[
                           "american"
                        ],
                        "mealType":[
                           "lunch/dinner"
                        ],
                        "dishType":[
                           "sandwiches"
                        ]
                      }}]
            """;

    @Test
    public void testWithWrongData() {
        assertThrows(IllegalStateException.class, () -> EdemamResponse.of(""));
    }

    @Test
    public void testNoNextPage() {
        String data ="{" +"""
                 "_links":{
                     "next":{
                            "href":null
                         }
                     },
                 """
                + HITS
                +"}";

        assertFalse(EdemamResponse.of(data).hasNext());
    }

    @Test
    public void testWithNormalData() {
        String data ="{" +"""
                 "_links":{
                     "next":{
                            "href":"https://api.edamam.com/api/recipes/v2?q=chicken%2Cbob&app_key=65314bd650c86a1f6ad1a450013b7324&_cont=CHcVQBtNNQphDmgVQ3tAEX4BYV1tDQEAQm1JBGoba1R7BwUVX3dHCmATNlN3VgNTQGRGB2cUYlIiA1AHFTQSA2BAYlB1BRFqX3cWQT1OcV9xBE4%3D&type=any&app_id=d7291f1e"
                         }
                     },
                 """
                + HITS
                +"}";

        PaginationResponse<Recipe> response = EdemamResponse.of(data);

        assertEquals(
                "https://api.edamam.com/api/recipes/v2?q=chicken%2Cbob&app_key=65314bd650c86a1f6ad1a450013b7324&_cont=CHcVQBtNNQphDmgVQ3tAEX4BYV1tDQEAQm1JBGoba1R7BwUVX3dHCmATNlN3VgNTQGRGB2cUYlIiA1AHFTQSA2BAYlB1BRFqX3cWQT1OcV9xBE4%3D&type=any&app_id=d7291f1e",
                response.getNextPageRequest().buildURI(),
                "Test next page response"
        );

        assertEquals(1, response.getData().size(), "Test parse");
    }
}
