package bg.sofia.uni.fmi.mjt.cookingcompass.client;

import bg.sofia.uni.fmi.mjt.cookingcompass.recipe.Recipe;
import bg.sofia.uni.fmi.mjt.cookingcompass.request.Request;
import bg.sofia.uni.fmi.mjt.cookingcompass.response.EdemamResponse;

public class EdemamClient extends BaseClient<Recipe> {

    private EdemamClient(EdemamClientBuilder builder) {
        super(builder);
    }

    public static EdemamClientBuilder builder(Request baseRequest) {
        return new EdemamClientBuilder(baseRequest);
    }

    public static class EdemamClientBuilder extends BaseClient.ClientBaseBuilder<Recipe> {

        public EdemamClientBuilder(Request baseRequest) {
            super(baseRequest, EdemamResponse::of);
        }
    }
}
