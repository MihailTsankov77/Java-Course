package bg.sofia.uni.fmi.mjt.bellot.server.response;

import bg.sofia.uni.fmi.mjt.bellot.server.response.types.ClientAction;
import bg.sofia.uni.fmi.mjt.bellot.server.response.types.ResponseType;

import java.util.Set;

public class TeamAnnouncementResponse extends ServerResponseBase {

    private final Set<String> playerNames;

    private TeamAnnouncementResponse(ResponseType type, Set<String> playerNames) {
        super(ClientAction.LISTEN, type);

        this.playerNames = playerNames;
    }

    public static TeamAnnouncementResponse createDealingTakenResponse(Set<String> playerNames) {
        return new TeamAnnouncementResponse(ResponseType.DEALING_TAKEN, playerNames);
    }

    public static TeamAnnouncementResponse createGameWonResponse(Set<String> playerNames) {
        return new TeamAnnouncementResponse(ResponseType.GAME_RESULTS, playerNames);
    }

    public Set<String> getPlayerNames() {
        return playerNames;
    }

}
