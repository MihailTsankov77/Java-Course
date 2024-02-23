package bg.sofia.uni.fmi.mjt.bellot.game.dealing;

import bg.sofia.uni.fmi.mjt.bellot.game.bidding.types.Bidding;

public interface Dealing {
    /**
     * Add points to the dealing state
     *
     * @param teamId - the id of the team that have taken the hand
     * @param points - the number of points the hand holds
     */
    void addPoints(int teamId, int points);

    /**
     * Used to get the result of the dealing after the dealing have finished
     */
    DealingResult getFinalPointsForTeams();

    Bidding getBidding();
}
