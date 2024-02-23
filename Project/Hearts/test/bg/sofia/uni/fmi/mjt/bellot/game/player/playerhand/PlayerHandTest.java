package bg.sofia.uni.fmi.mjt.bellot.game.player.playerhand;

import bg.sofia.uni.fmi.mjt.bellot.game.bidding.types.Bidding;
import bg.sofia.uni.fmi.mjt.bellot.game.cards.Card;
import bg.sofia.uni.fmi.mjt.bellot.game.cards.declarations.Declarations;
import bg.sofia.uni.fmi.mjt.bellot.game.cards.types.CardColor;
import bg.sofia.uni.fmi.mjt.bellot.game.cards.types.CardValue;
import bg.sofia.uni.fmi.mjt.bellot.game.exceptions.IncorrectPlayException;
import bg.sofia.uni.fmi.mjt.bellot.game.player.hand.HandRestrictions;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static bg.sofia.uni.fmi.mjt.bellot.game.bidding.types.Bidding.ALL_TRUMPS;
import static bg.sofia.uni.fmi.mjt.bellot.game.bidding.types.Bidding.HEARTS;
import static bg.sofia.uni.fmi.mjt.bellot.game.bidding.types.Bidding.NO_TRUMPS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PlayerHandTest {

    static HandRestrictions noRestriction = new HandRestrictions(null, null);
    static Card jackD = new Card(CardColor.DIAMONDS, CardValue.JACK);
    static Card queenD = new Card(CardColor.DIAMONDS, CardValue.QUEEN);
    static Card kingD = new Card(CardColor.DIAMONDS, CardValue.KING);
    static Card jackH = new Card(CardColor.HEART, CardValue.JACK);
    static Set<Card> cards = Set.of(jackD, queenD, kingD, jackH);

    static Player getPlayer(Bidding bidding) {
        PlayerHandBuilder builder = PlayerHand.builder().addBidding(bidding);
        for (Card card : cards) {
            builder.addCard(card);
        }
        return builder.build();
    }


    @Test
    void testNoSuchCard() {
        PlayerHand hand = PlayerHand.builder().addCard(jackD).addBidding(ALL_TRUMPS).build();
        assertThrows(IncorrectPlayException.class, () -> hand.playCard(jackH));
    }

    @Test
    void testRemoveCard() throws IncorrectPlayException {
        PlayerHand hand = PlayerHand.builder()
                .addCard(jackD)
                .addCard(jackH)
                .addBidding(ALL_TRUMPS)
                .build();
        hand.playCard(jackH);
        assertThrows(IncorrectPlayException.class, () -> hand.playCard(jackH));
    }

    @Test
    void testCarre() {
        PlayerHand hand = PlayerHand.builder()
                .addCard(jackD)
                .addCard(jackH)
                .addCard(new Card(CardColor.CLUB, CardValue.JACK))
                .addCard(new Card(CardColor.SPADE, CardValue.JACK))
                .addBidding(ALL_TRUMPS)
                .build();

        PlayableOptions options = hand.getPlayableOptions(noRestriction);
        assertEquals(
                Set.of(Declarations.CARRE_TWO_HUNDRED),
                options.declarations()
        );
    }

    @Test
    void testGetDeclarations() throws IncorrectPlayException {
        Player hand = getPlayer(ALL_TRUMPS);

        PlayableOptions playableOptions = hand.getPlayableOptions(noRestriction);

        assertEquals(
                Set.of(Declarations.SEQUENCE_TIERCE, Declarations.BELLOT_DIAMONDS),
                playableOptions.declarations()
        );

        hand.playCard(jackH);

        PlayableOptions secondHandPlayableOptions = hand.getPlayableOptions(noRestriction);
        assertEquals(
                Set.of(Declarations.BELLOT_DIAMONDS),
                secondHandPlayableOptions.declarations()
        );
    }

    @Test
    void testBellotOnDifferentColor() throws IncorrectPlayException {
        Player hand = getPlayer(HEARTS);

        hand.playCard(jackH);

        PlayableOptions secondHandPlayableOptions = hand.getPlayableOptions(noRestriction);
        assertEquals(Set.of(), secondHandPlayableOptions.declarations());
    }

    @Test
    void testRestrictions() {
        Player hand = getPlayer(HEARTS);

        PlayableOptions options = hand.getPlayableOptions(
                new HandRestrictions(null, new Card(CardColor.HEART, CardValue.NINE))
        );
        assertEquals(Set.of(jackH), options.cards());
    }

    @Test
    void testNoTrumpRestrictionNoColor() {
        Player hand = getPlayer(HEARTS);

        HandRestrictions restrictions = new HandRestrictions(new Card(CardColor.SPADE, CardValue.NINE), null);
        PlayableOptions options = hand.getPlayableOptions(restrictions);
        assertEquals(Set.of(jackH), options.cards());
    }

    @Test
    void testNoTrumpRestrictionHasColor() {
        Player hand = getPlayer(HEARTS);

        HandRestrictions restrictions = new HandRestrictions(jackD, null);
        PlayableOptions options = hand.getPlayableOptions(restrictions);
        assertEquals(Set.of(kingD, queenD), options.cards());
    }

    @Test
    void testRestrictionNoColor() {
        Player hand = getPlayer(NO_TRUMPS);

        HandRestrictions restrictions = new HandRestrictions(new Card(CardColor.SPADE, CardValue.NINE), null);
        PlayableOptions options = hand.getPlayableOptions(restrictions);
        assertEquals(cards, options.cards());
    }

    @Test
    void testRestriction() {
        Player hand = getPlayer(NO_TRUMPS);

        HandRestrictions restrictions = new HandRestrictions(new Card(CardColor.DIAMONDS, CardValue.ACE), null);
        PlayableOptions options = hand.getPlayableOptions(restrictions);
        assertEquals(Set.of(kingD, queenD, jackD), options.cards());
    }

}
