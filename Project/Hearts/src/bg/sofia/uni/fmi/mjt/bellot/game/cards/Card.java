package bg.sofia.uni.fmi.mjt.bellot.game.cards;

import bg.sofia.uni.fmi.mjt.bellot.game.bidding.types.Bidding;
import bg.sofia.uni.fmi.mjt.bellot.game.cards.types.CardColor;
import bg.sofia.uni.fmi.mjt.bellot.game.cards.types.CardValue;

import java.util.List;
import java.util.Set;

import static bg.sofia.uni.fmi.mjt.bellot.game.cards.types.CardValue.NO_TRUMPS_CARD_SEQUENCE;
import static bg.sofia.uni.fmi.mjt.bellot.game.cards.types.CardValue.TRUMPS_CARD_SEQUENCE;

public record Card(CardColor color, CardValue value) {

    public static boolean isHigherValueCard(Card card, Card currentCard, boolean isTrump) {
        List<CardValue> cardList = isTrump ? TRUMPS_CARD_SEQUENCE : NO_TRUMPS_CARD_SEQUENCE;

        return cardList.indexOf(card.value()) > cardList.indexOf(currentCard.value());
    }

    private int getCardPointsForTrumpColors(Set<CardColor> highColors) {
        boolean isCardHighColor = highColors.contains(color());

        return value().getPoints(isCardHighColor);
    }

    public int getCardPoints(Bidding bidding) {
        return switch (bidding) {
            case SPADES, CLUBS, HEARTS, DIAMONDS -> getCardPointsForTrumpColors(Set.of(CardColor.of(bidding)));
            case NO_TRUMPS -> getCardPointsForTrumpColors(Set.of());
            case ALL_TRUMPS -> getCardPointsForTrumpColors(Set.of(CardColor.values()));
        };
    }

    @Override
    public String toString() {
        return value.getValue() + color.getValue();
    }
}
