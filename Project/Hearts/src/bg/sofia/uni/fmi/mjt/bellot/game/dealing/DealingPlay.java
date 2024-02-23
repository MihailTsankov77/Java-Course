package bg.sofia.uni.fmi.mjt.bellot.game.dealing;

import bg.sofia.uni.fmi.mjt.bellot.game.PlayerCommunicator;
import bg.sofia.uni.fmi.mjt.bellot.game.cards.declarations.Declarations;
import bg.sofia.uni.fmi.mjt.bellot.game.exceptions.IncorrectPlayException;
import bg.sofia.uni.fmi.mjt.bellot.game.player.cyclemanager.PlayerCycleManager;
import bg.sofia.uni.fmi.mjt.bellot.game.player.hand.Hand;
import bg.sofia.uni.fmi.mjt.bellot.game.player.hand.HandRestrictions;
import bg.sofia.uni.fmi.mjt.bellot.game.player.playerhand.PlayableOptions;
import bg.sofia.uni.fmi.mjt.bellot.game.player.playerhand.PlayedOption;
import bg.sofia.uni.fmi.mjt.bellot.game.player.playerhand.Player;

import java.util.HashMap;
import java.util.Map;

public class DealingPlay {

    private static final int NORMAL_NUMBER_OF_HANDS = 8;
    private final int numberOfHands;
    private final Dealing dealingState;
    private final Map<Integer, Player> handsByPlayer;
    private final PlayerCommunicator playerCommunicator;
    private final PlayerCycleManager playerCycleManagerBuilder;
    private int currentPlayerId;
    private int playedHands = 0;

    private DealingPlay(Dealing dealingState,
                        Map<Integer, Player> handsByPlayer,
                        PlayerCommunicator playerCommunicator,
                        PlayerCycleManager playerCycleManagerBuilder,
                        int numberOfHands) {
        this.dealingState = dealingState;
        this.handsByPlayer = handsByPlayer;
        this.playerCommunicator = playerCommunicator;
        this.playerCycleManagerBuilder = playerCycleManagerBuilder;
        currentPlayerId = playerCycleManagerBuilder.getCurrentPlayerId();
        this.numberOfHands = numberOfHands;
    }

    public static DealingResult start(Dealing dealingState,
                                      Map<Integer, Player> handsByPlayer,
                                      PlayerCommunicator playerCommunicator,
                                      PlayerCycleManager playerCycleManagerBuilder) {
        return start(
                dealingState,
                handsByPlayer,
                playerCommunicator,
                playerCycleManagerBuilder,
                NORMAL_NUMBER_OF_HANDS);
    }

    public static DealingResult start(Dealing dealingState,
                                      Map<Integer, Player> handsByPlayer,
                                      PlayerCommunicator playerCommunicator,
                                      PlayerCycleManager playerCycleManagerBuilder,
                                      int numberOfHands) {
        DealingPlay dealingPlay = new DealingPlay(
                dealingState,
                handsByPlayer,
                playerCommunicator,
                playerCycleManagerBuilder,
                numberOfHands);

        return dealingPlay.play();
    }

    private DealingResult play() {
        while (playedHands < numberOfHands) {
            try {
                playHand();
            } catch (IncorrectPlayException e) {
                throw new RuntimeException(e);
            }
        }

        return dealingState.getFinalPointsForTeams();
    }

    private void splitPoints(Hand hand, Map<Integer, Integer> handPointsByTeam) {
        int winnerId = hand.getWinnerId();
        int winnerTeamId = playerCycleManagerBuilder.getPlayerTeam(winnerId);
        handPointsByTeam.put(winnerTeamId, handPointsByTeam.get(winnerTeamId) + hand.getPoints());

        playerCommunicator.updateHandTaken(winnerId);

        for (var teamPoints : handPointsByTeam.entrySet()) {
            dealingState.addPoints(teamPoints.getKey(), teamPoints.getValue());
        }

        currentPlayerId = winnerId;
        playedHands++;
    }

    private void playHand() throws IncorrectPlayException {
        HandRestrictions currentRestrictions = new HandRestrictions(null, null);
        Hand hand = new Hand(dealingState.getBidding());

        PlayerCycleManager cycleManager = playerCycleManagerBuilder.startNewCycle(currentPlayerId);
        int playerToPlay = cycleManager.getCurrentPlayerId();

        Map<Integer, Integer> handPointsByTeam = new HashMap<>();
        for (Integer teamId : cycleManager.getTeamsIds()) {
            handPointsByTeam.put(teamId, 0);
        }

        do {
            PlayableOptions playableOptions = handsByPlayer.get(playerToPlay).getPlayableOptions(currentRestrictions);
            PlayedOption playedOption = playerCommunicator.getPlayedOption(playerToPlay, playableOptions);

            playerCommunicator.updateOthersForPlay(playerToPlay, playedOption.card());

            currentRestrictions = hand.playCard(playedOption.card(), playerToPlay);
            handsByPlayer.get(playerToPlay).playCard(playedOption.card());

            int teamId = cycleManager.getPlayerTeam(playerToPlay);
            handPointsByTeam.put(teamId, handPointsByTeam.get(teamId) + getPlayedOptionPoints(playedOption));

            playerToPlay = cycleManager.getCurrentPlayerId();
        } while (playerToPlay != currentPlayerId);

        splitPoints(hand, handPointsByTeam);
    }

    private int getPlayedOptionPoints(PlayedOption option) {
        return option.declarations().stream()
                .map(Declarations::getPoints)
                .mapToInt(Integer::intValue)
                .sum();
    }
}
