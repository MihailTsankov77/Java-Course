package bg.sofia.uni.fmi.mjt.bellot.server.response;

import bg.sofia.uni.fmi.mjt.bellot.server.response.types.ClientAction;
import bg.sofia.uni.fmi.mjt.bellot.server.response.types.ResponseType;

public class IndividualAnnouncementResponse extends ServerResponseBase {

    private final String playerName;

    private IndividualAnnouncementResponse(ResponseType type, String playerName) {
        super(ClientAction.LISTEN, type);

        this.playerName = playerName;
    }

    public static IndividualAnnouncementResponse createTeamResponse(String playerName) {
        return new IndividualAnnouncementResponse(ResponseType.SET_TEAMS, playerName);
    }

    public static IndividualAnnouncementResponse createHandTakenResponse(String playerName) {
        return new IndividualAnnouncementResponse(ResponseType.HAND_TAKEN, playerName);
    }

    public String getPlayerName() {
        return playerName;
    }

}
