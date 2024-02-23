package bg.sofia.uni.fmi.mjt.bellot.server.response;

import bg.sofia.uni.fmi.mjt.bellot.game.cards.Card;
import bg.sofia.uni.fmi.mjt.bellot.server.response.types.ClientAction;
import bg.sofia.uni.fmi.mjt.bellot.server.response.types.ResponseType;

public class OtherPlayCardResponse extends ServerResponseBase {

    private final String playerName;
    private final Card card;

    public OtherPlayCardResponse(String playerName, Card card) {
        super(ClientAction.LISTEN, ResponseType.OTHER_PLAY_CARD);
        this.playerName = playerName;
        this.card = card;
    }

    public Card getCard() {
        return card;
    }

    public String getPlayerName() {
        return playerName;
    }
}
