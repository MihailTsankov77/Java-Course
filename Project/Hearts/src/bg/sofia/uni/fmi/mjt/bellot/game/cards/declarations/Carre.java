package bg.sofia.uni.fmi.mjt.bellot.game.cards.declarations;

import bg.sofia.uni.fmi.mjt.bellot.game.cards.types.CardValue;

import java.util.Set;

public enum Carre {
    HUNDRED(100, Set.of(CardValue.ACE, CardValue.TEN, CardValue.QUEEN, CardValue.KING)),
    HUNDRED_AN_FIFTY(150, Set.of(CardValue.NINE)),

    TWO_HUNDRED(200, Set.of(CardValue.JACK));

    final int points;
    final Set<CardValue> sets;

    Carre(int points, Set<CardValue> sets) {
        this.points = points;
        this.sets = sets;
    }

    public Set<CardValue> getSets() {
        return sets;
    }

    public int getPoints() {
        return points;
    }

}
