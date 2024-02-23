package bg.sofia.uni.fmi.mjt.bellot.game.player.hand;

import bg.sofia.uni.fmi.mjt.bellot.game.bidding.types.Bidding;
import bg.sofia.uni.fmi.mjt.bellot.game.cards.Card;
import bg.sofia.uni.fmi.mjt.bellot.game.cards.types.CardColor;
import bg.sofia.uni.fmi.mjt.bellot.game.cards.types.CardValue;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HandTest {

    @Test
    void testHandPoints() {
        Hand hand = new Hand(Bidding.ALL_TRUMPS);

        hand.playCard(new Card(CardColor.HEART, CardValue.JACK), 1);
        hand.playCard(new Card(CardColor.SPADE, CardValue.JACK), 2);
        hand.playCard(new Card(CardColor.HEART, CardValue.SEVEN), 3);
        hand.playCard(new Card(CardColor.HEART, CardValue.EIGHT), 4);

        assertEquals(40, hand.getPoints());
    }

    @Test
    void testHandPRestrictions() {
        Hand hand = new Hand(Bidding.SPADES);

        Card nTJack = new Card(CardColor.HEART, CardValue.JACK);

        assertEquals(
                new HandRestrictions(nTJack, null),
                hand.playCard(nTJack, 1)
        );

        Card TNine = new Card(CardColor.SPADE, CardValue.NINE);
        assertEquals(
                new HandRestrictions(nTJack, TNine),
                hand.playCard(TNine, 2)
        );

        assertEquals(
                new HandRestrictions(nTJack, TNine),
                hand.playCard(new Card(CardColor.HEART, CardValue.SEVEN), 3)
        );

        Card TJack = new Card(CardColor.SPADE, CardValue.JACK);
        assertEquals(
                new HandRestrictions(nTJack, TJack),
                hand.playCard(TJack, 4)
        );

        assertEquals(36, hand.getPoints());
    }
}
