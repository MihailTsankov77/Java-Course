package bg.sofia.uni.fmi.mjt.bellot.game.cards.declarations;

import bg.sofia.uni.fmi.mjt.bellot.game.cards.Card;
import bg.sofia.uni.fmi.mjt.bellot.game.cards.types.CardValue;

import java.util.HashMap;
import java.util.Map;

public class Bellot {
    private static final Map<CardValue, CardValue> CORRESPONDING_VALUE = new HashMap<>();

    static {
        CORRESPONDING_VALUE.put(CardValue.KING, CardValue.QUEEN);
        CORRESPONDING_VALUE.put(CardValue.QUEEN, CardValue.KING);
    }

    public static Card getCorrespondingCard(Card card) {
        if (!CORRESPONDING_VALUE.containsKey(card.value())) {
            return null;
        }

        return new Card(card.color(), CORRESPONDING_VALUE.get(card.value()));
    }
}
