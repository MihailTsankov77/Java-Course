package bg.sofia.uni.fmi.mjt.bellot.server.response;

import bg.sofia.uni.fmi.mjt.bellot.server.response.types.ResponseType;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class ServerResponseParser {

    private static final Gson GSON = new Gson();

    private ServerResponseParser() {
    }

    public static String toJson(ServerResponseBase response) {
        return GSON.toJson(response);
    }

    public static ServerResponseBase fromJson(String rawJson) {
        JsonElement jsonParser = JsonParser.parseString(rawJson);

        ResponseType type = ResponseType.valueOf(jsonParser.getAsJsonObject().get("type").getAsString());

        return switch (type) {
            case BID -> GSON.fromJson(rawJson, BidResponse.class);
            case OTHER_BID -> GSON.fromJson(rawJson, OtherBidResponse.class);
            case PLAY_CARD -> GSON.fromJson(rawJson, PlayCardResponse.class);
            case SET_CARDS -> GSON.fromJson(rawJson, SetCardsResponse.class);
            case OTHER_PLAY_CARD -> GSON.fromJson(rawJson, OtherPlayCardResponse.class);
            case SET_TEAMS, HAND_TAKEN -> GSON.fromJson(rawJson, IndividualAnnouncementResponse.class);
            case GAME_RESULTS, DEALING_TAKEN -> GSON.fromJson(rawJson, TeamAnnouncementResponse.class);
        };
    }

}
