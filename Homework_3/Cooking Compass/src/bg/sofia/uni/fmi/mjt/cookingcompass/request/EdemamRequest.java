package bg.sofia.uni.fmi.mjt.cookingcompass.request;

import bg.sofia.uni.fmi.mjt.cookingcompass.types.query.Health;
import bg.sofia.uni.fmi.mjt.cookingcompass.types.query.MealType;
import bg.sofia.uni.fmi.mjt.cookingcompass.types.query.RecipeType;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class EdemamRequest implements Request {

    private static final String API_BASE_URL = "https://api.edamam.com/api/recipes/v2";

    private static final String APP_ID_PARAM = "d7291f1e";
    private static final String APP_KEY_PARAM = "65314bd650c86a1f6ad1a450013b7324";
    private final RecipeType type;
    private final Set<String> q;
    private final Set<MealType> mealTypes;
    private final Set<Health> healthLabels;

    private EdemamRequest(RequestBuilder builder) {
        type = builder.type;
        q = builder.q;
        mealTypes = builder.mealTypes;
        healthLabels = builder.healthLabels;
    }

    private static void addParam(StringBuilder query, String key, String value) {
        addParam(query, key, value, false);
    }

    private static void addParam(StringBuilder query, String key, String value, boolean isFirst) {
        if (!isFirst) {
            query.append("&");
        }

        query.append(key).append("=").append(value);
    }

    public static RequestBuilder builder() {
        return new RequestBuilder();
    }

    public static RequestBuilder builder(RecipeType type) {
        return new RequestBuilder(type);
    }

    private String buildQueryParams() {
        StringBuilder query = new StringBuilder();
        addParam(query, "type", type.value(), true);
        addParam(query, "app_id", APP_ID_PARAM);
        addParam(query, "app_key", APP_KEY_PARAM);

        for (String word: q){
            addParam(query, "q", word);
        }

        for (Health health : healthLabels) {
            addParam(query, "health", health.value());
        }

        for (MealType mealType : mealTypes) {
            addParam(query, "mealType", mealType.value());
        }

        return query.toString();
    }

    @Override
    public String buildURI() {
        String queryParams = buildQueryParams();
        return STR. "\{ API_BASE_URL }?\{ queryParams }" ;
    }

    public static class RequestBuilder {
        private final Set<String> q = new HashSet<>();
        private final Set<MealType> mealTypes = new HashSet<>();
        private final Set<Health> healthLabels = new HashSet<>();
        private RecipeType type = RecipeType.ANY;

        private RequestBuilder() {
        }

        private RequestBuilder(RecipeType type) {
            this.type = type;
        }

        public RequestBuilder setWordParams(String word) {
            q.add(word);
            return this;
        }

        public RequestBuilder setWordParams(Collection<String> words) {
            q.addAll(words);
            return this;
        }

        public RequestBuilder setMealTypeParams(MealType mealType) {
            mealTypes.add(mealType);
            return this;
        }

        public RequestBuilder setMealTypeParams(Collection<MealType> mealType) {
            mealTypes.addAll(mealType);
            return this;
        }

        public RequestBuilder setHealthParams(Health health) {
            healthLabels.add(health);
            return this;
        }

        public RequestBuilder setHealthParams(Collection<Health> health) {
            healthLabels.addAll(health);
            return this;
        }

        public EdemamRequest build() {
            return new EdemamRequest(this);
        }
    }

}
