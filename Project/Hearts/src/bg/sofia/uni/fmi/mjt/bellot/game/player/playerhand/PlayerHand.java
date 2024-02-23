package bg.sofia.uni.fmi.mjt.bellot.game.player.playerhand;

import bg.sofia.uni.fmi.mjt.bellot.game.bidding.types.Bidding;
import bg.sofia.uni.fmi.mjt.bellot.game.cards.Card;
import bg.sofia.uni.fmi.mjt.bellot.game.cards.declarations.Bellot;
import bg.sofia.uni.fmi.mjt.bellot.game.cards.declarations.Carre;
import bg.sofia.uni.fmi.mjt.bellot.game.cards.declarations.Declarations;
import bg.sofia.uni.fmi.mjt.bellot.game.cards.declarations.Sequence;
import bg.sofia.uni.fmi.mjt.bellot.game.cards.types.CardColor;
import bg.sofia.uni.fmi.mjt.bellot.game.cards.types.CardValue;
import bg.sofia.uni.fmi.mjt.bellot.game.exceptions.IncorrectPlayException;
import bg.sofia.uni.fmi.mjt.bellot.game.player.hand.HandRestrictions;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class PlayerHand implements Player {
    private static final int CARRE_COUNT = 4;
    private final Set<Card> cards;
    private final Bidding bidding;
    private final CardColor trumpColor;
    private boolean isFirstHand = true;

    PlayerHand(PlayerHandBuilder builder) {
        cards = builder.hand;
        bidding = builder.bidding;
        trumpColor = CardColor.of(builder.bidding);
    }

    public static PlayerHandBuilder builder() {
        return new PlayerHandBuilder();
    }

    @Override
    public PlayableOptions getPlayableOptions(HandRestrictions restrictions) {
        Set<Declarations> declarations = new HashSet<>();
        if (isFirstHand) {
            declarations.addAll(getStartDeclarations());
        }

        declarations.addAll(getBellot(restrictions));

        return new PlayableOptions(getPlayableCards(restrictions), declarations);
    }

    private Set<Declarations> getStartDeclarations() {
        Set<Declarations> declarations = new HashSet<>();

        declarations.addAll(getSequences().stream().map(Declarations::of).collect(Collectors.toSet()));
        declarations.addAll(getCarre().stream().map(Declarations::of).collect(Collectors.toSet()));

        return declarations;
    }

    private int countSequence(Set<CardValue> cards) {
        int maxSequence = 0;
        int sequence = 0;

        for (CardValue value : CardValue.NO_TRUMPS_CARD_SEQUENCE) {
            if (!cards.contains(value)) {
                sequence = 0;
                continue;
            }

            maxSequence = Math.max(maxSequence, ++sequence);
        }

        return maxSequence;
    }

    private Set<Sequence> getSequences() {
        Map<CardColor, Set<CardValue>> cardsByColor = cards.stream()
                .collect(Collectors.groupingBy(Card::color,
                        Collectors.mapping(Card::value, Collectors.toSet())));

        int maxSequence = cardsByColor.values().stream().map(this::countSequence).max(Integer::compareTo).orElse(0);

        Set<Sequence> sequences = new HashSet<>();

        for (Sequence sequence : Sequence.values()) {
            if (maxSequence >= sequence.getSequence()) {
                sequences.add(sequence);
            }
        }

        return sequences;
    }

    private Set<Carre> getCarre() {
        Map<CardValue, Long> valuesCount = cards.stream()
                .collect(Collectors.groupingBy(Card::value, Collectors.counting()));

        Set<Carre> carres = new HashSet<>();
        for (Carre carre : Carre.values()) {
            for (CardValue value : carre.getSets()) {
                if (valuesCount.containsKey(value) && valuesCount.get(value) == CARRE_COUNT) {
                    carres.add(carre);
                    break;
                }
            }
        }

        return carres;
    }

    private Set<Declarations> getBellot(HandRestrictions restrictions) {
        Set<Card> bellotCards = cards.stream()
                .filter(card ->
                        (bidding == Bidding.ALL_TRUMPS
                                && (restrictions.trumpCard() == null
                                || restrictions.trumpCard().color().equals(card.color())))
                                || card.color().equals(trumpColor))
                .filter(card -> card.value().equals(CardValue.QUEEN))
                .collect(Collectors.toSet());

        Set<Declarations> declarations = new HashSet<>();

        for (Card card : bellotCards) {
            Card correspondingCard = Bellot.getCorrespondingCard(card);
            if (cards.contains(correspondingCard)) {
                declarations.add(Declarations.of(card.color()));
            }
        }

        return declarations;
    }

    private Set<Card> getCardsOptions(Card baseCard, boolean isTrump) {
        Set<Card> sameColor = cards.stream()
                .filter(card -> card.color().equals(baseCard.color()))
                .collect(Collectors.toSet());

        if (!sameColor.isEmpty()) {
            Set<Card> higherCards = sameColor.stream()
                    .filter(card -> Card.isHigherValueCard(card, baseCard, isTrump))
                    .collect(Collectors.toSet());

            if (!higherCards.isEmpty()) {
                return higherCards;
            }

            return sameColor.stream()
                    .filter(card -> !Card.isHigherValueCard(card, baseCard, isTrump))
                    .collect(Collectors.toSet());
        }

        return null;
    }

    private Set<Card> getPlayableCards(HandRestrictions restrictions) {
        if (restrictions.card() != null) {
            Set<Card> options = getCardsOptions(restrictions.card(), false);
            if (options != null) {
                return options;
            }

            if (restrictions.trumpCard() != null) {
                options = getCardsOptions(restrictions.trumpCard(), true);
                if (options != null) {
                    return options;
                }
            }

            options = cards.stream()
                    .filter(card -> card.color().equals(trumpColor))
                    .collect(Collectors.toSet());
            return options.isEmpty() ? cards : options;
        }

        if (restrictions.trumpCard() != null) {
            Set<Card> options = getCardsOptions(restrictions.trumpCard(), true);
            if (options != null) {
                return options;
            }
        }

        return cards;
    }

    @Override
    public void playCard(Card card) throws IncorrectPlayException {
        if (cards.contains(card)) {
            isFirstHand = false;
            cards.remove(card);

            return;
        }

        throw new IncorrectPlayException("Tried to play card that he does not have");
    }
}
