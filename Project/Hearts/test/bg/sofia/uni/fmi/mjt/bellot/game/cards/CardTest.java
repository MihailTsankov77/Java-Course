package bg.sofia.uni.fmi.mjt.bellot.game.cards;

import bg.sofia.uni.fmi.mjt.bellot.game.bidding.types.Bidding;
import bg.sofia.uni.fmi.mjt.bellot.game.cards.types.CardColor;
import bg.sofia.uni.fmi.mjt.bellot.game.cards.types.CardValue;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CardTest {

    @Test
    public void testHigherValueCard() {
        Card smallerCard = new Card(CardColor.HEART, CardValue.JACK);
        Card biggerCard = new Card(CardColor.HEART, CardValue.ACE);

        assertFalse(Card.isHigherValueCard(smallerCard, biggerCard, false),
                "test with Jack and ace -> not trumps");

        assertTrue(Card.isHigherValueCard(smallerCard, biggerCard, true),
                "test with Jack and ace -> trumps");
    }

    @Test
    public void testCardPoints() {
        Card card = new Card(CardColor.HEART, CardValue.JACK);

        assertEquals(2, card.getCardPoints(Bidding.NO_TRUMPS),
                "no trumps");

        assertEquals(2, card.getCardPoints(Bidding.DIAMONDS),
                "not trump color");

        assertEquals(20, card.getCardPoints(Bidding.HEARTS),
                "trump color");

        assertEquals(20, card.getCardPoints(Bidding.ALL_TRUMPS),
                "all trumps");
    }
}
