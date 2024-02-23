package bg.sofia.uni.fmi.mjt.bellot.client.response;

import bg.sofia.uni.fmi.mjt.bellot.client.response.types.ClientResponseType;
import bg.sofia.uni.fmi.mjt.bellot.game.player.playerhand.PlayedOption;

public class ClientPlayCardResponse extends ClientResponseBase {

    private final PlayedOption option;

    public ClientPlayCardResponse(PlayedOption option) {
        super(ClientResponseType.PLAY_CARD);
        this.option = option;
    }

    public PlayedOption getOption() {
        return option;
    }
}
