package bg.sofia.uni.fmi.mjt.bellot.game.player.playerhand;

import bg.sofia.uni.fmi.mjt.bellot.game.bidding.types.Bidding;
import bg.sofia.uni.fmi.mjt.bellot.game.cards.Card;

import java.util.HashSet;
import java.util.Set;

public class PlayerHandBuilder {
    final Set<Card> hand = new HashSet<>();
    Bidding bidding = null;

    public Set<Card> getCards() {
        return hand;
    }

    public PlayerHandBuilder addCard(Card card) {
        hand.add(card);
        return this;
    }

    public PlayerHandBuilder addBidding(Bidding bidding) {
        this.bidding = bidding;
        return this;
    }

    public PlayerHand build() {
        return new PlayerHand(this);
    }

}
