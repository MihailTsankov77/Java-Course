package bg.sofia.uni.fmi.mjt.bellot.client.response;

import bg.sofia.uni.fmi.mjt.bellot.client.response.types.ClientResponseType;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class ClientResponseParser {

    private static final Gson GSON = new Gson();

    private ClientResponseParser() {
    }

    public static String toJson(ClientResponseBase response) {
        return GSON.toJson(response);
    }

    public static ClientResponseBase fromJson(String rawJson) {
        JsonElement jsonParser = JsonParser.parseString(rawJson);

        ClientResponseType type = ClientResponseType.valueOf(jsonParser.getAsJsonObject().get("type").getAsString());

        return switch (type) {
            case BID -> GSON.fromJson(rawJson, ClientBidResponse.class);
            case PLAY_CARD -> GSON.fromJson(rawJson, ClientPlayCardResponse.class);
        };
    }
}
