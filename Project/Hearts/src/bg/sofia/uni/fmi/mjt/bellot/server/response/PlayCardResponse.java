package bg.sofia.uni.fmi.mjt.bellot.server.response;

import bg.sofia.uni.fmi.mjt.bellot.game.player.playerhand.PlayableOptions;
import bg.sofia.uni.fmi.mjt.bellot.server.response.types.ClientAction;
import bg.sofia.uni.fmi.mjt.bellot.server.response.types.ResponseType;

public class PlayCardResponse extends ServerResponseBase {

    private final PlayableOptions options;

    public PlayCardResponse(PlayableOptions options) {
        super(ClientAction.RESPOND, ResponseType.PLAY_CARD);
        this.options = options;
    }

    public PlayableOptions getOptions() {
        return options;
    }

}
