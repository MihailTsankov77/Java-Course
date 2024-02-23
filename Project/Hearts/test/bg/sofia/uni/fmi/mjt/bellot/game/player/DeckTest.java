package bg.sofia.uni.fmi.mjt.bellot.game.player;

import bg.sofia.uni.fmi.mjt.bellot.game.player.playerhand.PlayerHandBuilder;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DeckTest {

    @Test
    void testShuffle() {
        List<PlayerHandBuilder> playerHandBuilders = Deck.generateShuffle();

        assertEquals(4, playerHandBuilders.size());
        for (PlayerHandBuilder builder : playerHandBuilders) {
            assertEquals(8, builder.getCards().size());
        }
    }

}
