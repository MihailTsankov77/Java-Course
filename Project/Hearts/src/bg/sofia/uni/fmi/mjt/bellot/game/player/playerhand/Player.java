package bg.sofia.uni.fmi.mjt.bellot.game.player.playerhand;

import bg.sofia.uni.fmi.mjt.bellot.game.cards.Card;
import bg.sofia.uni.fmi.mjt.bellot.game.exceptions.IncorrectPlayException;
import bg.sofia.uni.fmi.mjt.bellot.game.player.hand.HandRestrictions;

public interface Player {

    PlayableOptions getPlayableOptions(HandRestrictions restrictions);

    void playCard(Card card) throws IncorrectPlayException;
}
