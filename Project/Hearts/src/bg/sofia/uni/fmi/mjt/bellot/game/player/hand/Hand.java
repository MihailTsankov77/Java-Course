package bg.sofia.uni.fmi.mjt.bellot.game.player.hand;

import bg.sofia.uni.fmi.mjt.bellot.game.bidding.types.Bidding;
import bg.sofia.uni.fmi.mjt.bellot.game.cards.Card;
import bg.sofia.uni.fmi.mjt.bellot.game.cards.types.CardColor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static bg.sofia.uni.fmi.mjt.bellot.game.cards.Card.isHigherValueCard;

public class Hand {

    private final Bidding bidding;
    private final CardColor trumpColor;
    private final Map<Integer, Set<Card>> cardsPlayedByPlayer = new HashMap<>();
    private Card highestCard = null;
    private Card highestTrumpCard = null;

    public Hand(Bidding bidding) {
        this.bidding = bidding;
        trumpColor = CardColor.of(bidding);
    }

    private void setCard(Card card, int playerId) {
        if (card.color().equals(trumpColor) || bidding == Bidding.ALL_TRUMPS) {
            if (highestTrumpCard == null) {
                highestTrumpCard = card;
            } else if (highestTrumpCard.color().equals(card.color())) {
                if (isHigherValueCard(card, highestTrumpCard, true)) {
                    highestTrumpCard = card;
                }
            }
        } else {
            if (highestCard == null) {
                highestCard = card;
            } else if (highestCard.color().equals(card.color())) {
                if (isHigherValueCard(card, highestCard, false)) {
                    highestCard = card;
                }
            }
        }

        cardsPlayedByPlayer.putIfAbsent(playerId, new HashSet<>());
        cardsPlayedByPlayer.get(playerId).add(card);
    }

    public HandRestrictions playCard(Card card, int playerId) {
        setCard(card, playerId);
        return new HandRestrictions(highestCard, highestTrumpCard);
    }

    public int getWinnerId() {
        return cardsPlayedByPlayer.entrySet().stream()
                .filter(entry -> entry.getValue()
                        .contains(highestTrumpCard != null ?
                                highestTrumpCard :
                                highestCard))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(-1);
    }

    public int getPoints() {
        return cardsPlayedByPlayer.values().stream()
                .flatMap(Set::stream)
                .map(card -> card.getCardPoints(bidding))
                .mapToInt(Integer::intValue)
                .sum();
    }
}
