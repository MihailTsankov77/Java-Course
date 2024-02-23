package bg.sofia.uni.fmi.mjt.bellot.game.player.playerhand;

import bg.sofia.uni.fmi.mjt.bellot.game.cards.Card;
import bg.sofia.uni.fmi.mjt.bellot.game.cards.declarations.Declarations;

import java.util.Set;

public record PlayedOption(Card card, Set<Declarations> declarations) {
}
