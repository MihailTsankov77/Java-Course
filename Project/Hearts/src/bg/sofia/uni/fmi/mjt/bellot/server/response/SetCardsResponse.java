package bg.sofia.uni.fmi.mjt.bellot.server.response;

import bg.sofia.uni.fmi.mjt.bellot.game.cards.Card;
import bg.sofia.uni.fmi.mjt.bellot.server.response.types.ClientAction;
import bg.sofia.uni.fmi.mjt.bellot.server.response.types.ResponseType;

import java.util.Set;

public class SetCardsResponse extends ServerResponseBase {

    private final Set<Card> cards;

    public SetCardsResponse(Set<Card> cards) {
        super(ClientAction.LISTEN, ResponseType.SET_CARDS);
        this.cards = cards;
    }

    public Set<Card> getCards() {
        return cards;
    }

}
