package bg.sofia.uni.fmi.mjt.bellot.game.dealing;

import bg.sofia.uni.fmi.mjt.bellot.game.bidding.types.Bidding;
import bg.sofia.uni.fmi.mjt.bellot.game.bidding.types.Doubling;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DealingState implements Dealing {

    private static final double POINTS_DELIMITER = 10;
    private static final int LAST_DIGIT_DELIMITER = 10;
    private static final int ALL_TRUMPS_THRESHOLD = 100;
    private static final int ALL_TRUMPS_LAST_DIGIT_MIN_VALUE = 4;
    private final Map<Integer, Integer> pointsByTeam = new HashMap<>();
    private final Bidding bidding;
    private final Doubling doubling;
    private int announcedByTeamId;
    private int otherTeamId;

    public DealingState(Set<Integer> teamIds, Bidding binding, Doubling doubling, int announcedByTeamId) {
        this.bidding = binding;
        this.doubling = doubling;
        this.announcedByTeamId = announcedByTeamId;

        for (int teamId : teamIds) {
            if (teamId != announcedByTeamId) {
                otherTeamId = teamId;
                break;
            }
        }

        for (Integer teamId : teamIds) {
            pointsByTeam.put(teamId, 0);
        }
    }

    private static int getPointsForBidding(int points, Bidding bidding, boolean isLosing) {
        return switch (bidding) {
            case HEARTS, CLUBS, DIAMONDS, SPADES -> (int) Math.round(points / POINTS_DELIMITER);
            case NO_TRUMPS -> (int) Math.round((points * 2) / POINTS_DELIMITER);
            case ALL_TRUMPS -> {
                if (isLosing && points > ALL_TRUMPS_THRESHOLD
                        && points % LAST_DIGIT_DELIMITER == ALL_TRUMPS_LAST_DIGIT_MIN_VALUE) {
                    points++;
                }

                yield (int) Math.round(points / POINTS_DELIMITER);
            }
        };
    }

    @Override
    public void addPoints(int teamId, int points) {
        if (!pointsByTeam.containsKey(teamId)) {
            throw new IllegalArgumentException(
                    "This player is not part of the bg.sofia.uni.fmi.mjt.bellot.client.game"
            );
        }

        int currentPoints = pointsByTeam.get(teamId);
        pointsByTeam.put(teamId, currentPoints + points);
    }

    private int getWinningTeamId() {
        if (new HashSet<>(pointsByTeam.values()).size() == 1) {
            return -1;
        }

        return pointsByTeam.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey).orElse(-1);
    }

    @Override
    public DealingResult getFinalPointsForTeams() {
        int winningTeamId = getWinningTeamId();

        if (winningTeamId == -1) {
            return handleHanging();
        }

        if (winningTeamId != announcedByTeamId) {
            return handleLosing();
        }

        if (doubling != Doubling.NONE) {
            announcedByTeamId = otherTeamId;
            otherTeamId = winningTeamId;
            return handleLosing();
        }

        return handleWinning();
    }

    private int getAllDealingPoints() {
        int points = pointsByTeam.values().stream().mapToInt(Integer::intValue).sum();
        return (int) Math.round(points / POINTS_DELIMITER) * doubling.getMultiplier();
    }

    private DealingResult handleHanging() {
        Map<Integer, Integer> resultsByTeam = new HashMap<>();
        int halfPoints = getAllDealingPoints() / 2;

        resultsByTeam.put(announcedByTeamId, 0);
        resultsByTeam.put(otherTeamId, halfPoints);

        return new DealingResult(resultsByTeam, -1, halfPoints);
    }

    private DealingResult handleLosing() {
        Map<Integer, Integer> resultsByTeam = new HashMap<>();

        resultsByTeam.put(announcedByTeamId, 0);
        resultsByTeam.put(otherTeamId, getAllDealingPoints());

        return new DealingResult(resultsByTeam, otherTeamId, 0);
    }

    private DealingResult handleWinning() {
        Map<Integer, Integer> resultsByTeam = new HashMap<>();

        int winnerPoints = getPointsForBidding(pointsByTeam.get(announcedByTeamId), bidding, false);
        int looserPoints = getPointsForBidding(pointsByTeam.get(otherTeamId), bidding, true);

        resultsByTeam.put(announcedByTeamId, winnerPoints);
        resultsByTeam.put(otherTeamId, looserPoints);

        return new DealingResult(resultsByTeam, announcedByTeamId, 0);
    }

    @Override
    public Bidding getBidding() {
        return bidding;
    }
}
