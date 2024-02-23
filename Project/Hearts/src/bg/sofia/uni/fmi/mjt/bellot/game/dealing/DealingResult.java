package bg.sofia.uni.fmi.mjt.bellot.game.dealing;

import java.util.Map;

public record DealingResult(Map<Integer, Integer> pointByTeam, int winningTeamId, int hangingPoints) {
}
