package bg.sofia.uni.fmi.mjt.bellot.game;

import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class GamePlayTest {

    static Map<Integer, Integer> playersTeams = Map.of(
            1, 1,
            2, 1,
            3, 2,
            4, 2);

    @Test
    void testExtractPlayersFromTeam() {
        assertEquals(Set.of(1, 2), GamePlay.getPlayersFromTeam(1, playersTeams));
    }

    @Test
    void testGetGameResult() {
        Map<Integer, Integer> pointsByTeam = Map.of(
                1, 151,
                2, 120);

        GameResult expected = new GameResult(Set.of(1, 2), 151, 120);
        assertEquals(expected, GamePlay.getGameResult(pointsByTeam, playersTeams));
    }

    @Test
    void testGetGameResultNotEnough() {
        Map<Integer, Integer> pointsByTeam = Map.of(
                1, 130,
                2, 120);

        assertNull(GamePlay.getGameResult(pointsByTeam, playersTeams));
    }

}
