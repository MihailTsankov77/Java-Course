package bg.sofia.uni.fmi.mjt.bellot.game.player;

import bg.sofia.uni.fmi.mjt.bellot.game.bidding.types.Bidding;
import bg.sofia.uni.fmi.mjt.bellot.game.cards.Card;
import bg.sofia.uni.fmi.mjt.bellot.game.cards.types.CardColor;
import bg.sofia.uni.fmi.mjt.bellot.game.cards.types.CardValue;
import bg.sofia.uni.fmi.mjt.bellot.game.player.playerhand.Player;
import bg.sofia.uni.fmi.mjt.bellot.game.player.playerhand.PlayerHandBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Deck {

    private static final int NUMBER_OF_PLAYERS = 4;

    public static List<Card> getNewDeck() {
        List<Card> deck = new ArrayList<>();

        for (CardColor color : CardColor.values()) {
            for (CardValue value : CardValue.values()) {
                deck.add(new Card(color, value));
            }
        }

        return deck;
    }

    public static List<PlayerHandBuilder> generateShuffle() {
        List<Card> deck = getNewDeck();
        Collections.shuffle(deck);

        List<PlayerHandBuilder> playerHandBuilders = new ArrayList<>();

        for (int i = 0; i < NUMBER_OF_PLAYERS; i++) {
            playerHandBuilders.add(new PlayerHandBuilder());
        }

        int numberOfCardsPerPlayer = deck.size() / NUMBER_OF_PLAYERS;

        for (int i = 0; i < deck.size(); i++) {
            playerHandBuilders.get(i / numberOfCardsPerPlayer).addCard(deck.get(i));
        }

        return playerHandBuilders;
    }

    public static Map<Integer, Player> createHands(Bidding bidding,
                                                   Map<Integer, PlayerHandBuilder> handsBuilderByPlayer) {
        Map<Integer, Player> handsByPlayer = new HashMap<>();

        for (var player : handsBuilderByPlayer.entrySet()) {
            handsByPlayer.put(player.getKey(), player.getValue().addBidding(bidding).build());
        }

        return handsByPlayer;
    }
}
