package bg.sofia.uni.fmi.mjt.bellot.game.dealing;

import bg.sofia.uni.fmi.mjt.bellot.game.bidding.types.Bidding;
import bg.sofia.uni.fmi.mjt.bellot.game.bidding.types.Doubling;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DealingStateTest {

    static Dealing getDealing(Bidding bidding, Doubling doubling) {
        return new DealingState(Set.of(1, 2), bidding, doubling, 1);
    }

    @Test
    void testWinning() {
        Dealing dealing = getDealing(Bidding.DIAMONDS, Doubling.NONE);

        dealing.addPoints(1, 110);
        dealing.addPoints(2, 52);

        DealingResult result = dealing.getFinalPointsForTeams();

        assertEquals(Map.of(1, 11, 2, 5), result.pointByTeam());
        assertEquals(1, result.winningTeamId());
        assertEquals(0, result.hangingPoints());
    }

    @Test
    void testWinningRoundingAllTrumps() {
        Dealing dealing = getDealing(Bidding.ALL_TRUMPS, Doubling.NONE);

        dealing.addPoints(1, 144);
        dealing.addPoints(2, 114);

        DealingResult result = dealing.getFinalPointsForTeams();

        assertEquals(Map.of(1, 14, 2, 12), result.pointByTeam());
        assertEquals(1, result.winningTeamId());
        assertEquals(0, result.hangingPoints());
    }

    @Test
    void testLosing() {
        Dealing dealing = getDealing(Bidding.DIAMONDS, Doubling.NONE);

        dealing.addPoints(1, 52);
        dealing.addPoints(2, 110);

        DealingResult result = dealing.getFinalPointsForTeams();

        assertEquals(Map.of(1, 0, 2, 16), result.pointByTeam());
        assertEquals(2, result.winningTeamId());
        assertEquals(0, result.hangingPoints());
    }

    @Test
    void testContra() {
        Dealing dealing = getDealing(Bidding.DIAMONDS, Doubling.CONTRA);

        dealing.addPoints(1, 110);
        dealing.addPoints(2, 52);

        DealingResult result = dealing.getFinalPointsForTeams();

        assertEquals(Map.of(1, 32, 2, 0), result.pointByTeam());
        assertEquals(1, result.winningTeamId());
        assertEquals(0, result.hangingPoints());
    }

    @Test
    void testReContra() {
        Dealing dealing = getDealing(Bidding.DIAMONDS, Doubling.RE_CONTRA);

        dealing.addPoints(1, 52);
        dealing.addPoints(2, 110);

        DealingResult result = dealing.getFinalPointsForTeams();

        assertEquals(Map.of(1, 0, 2, 64), result.pointByTeam());
        assertEquals(2, result.winningTeamId());
        assertEquals(0, result.hangingPoints());
    }

    @Test
    void testDraw() {
        Dealing dealing = getDealing(Bidding.ALL_TRUMPS, Doubling.NONE);

        dealing.addPoints(1, 129);
        dealing.addPoints(2, 129);

        DealingResult result = dealing.getFinalPointsForTeams();

        assertEquals(Map.of(1, 0, 2, 13), result.pointByTeam());
        assertEquals(-1, result.winningTeamId());
        assertEquals(13, result.hangingPoints());
    }
}
