package bg.sofia.uni.fmi.mjt.bellot.game;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public record GameResult(Set<Integer> playersIds, int winningScore, int losingScore) {
    public String toString(Map<Integer, String> playersNames) {
        String names = playersIds.stream()
                .map(playersNames::get)
                .collect(Collectors.joining(" and "));

        return names + " won the game with result " + winningScore + " to " + losingScore;
    }
}

