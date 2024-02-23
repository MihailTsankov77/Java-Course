package bg.sofia.uni.fmi.mjt.bellot.game;

import bg.sofia.uni.fmi.mjt.bellot.game.bidding.BiddingPlay;
import bg.sofia.uni.fmi.mjt.bellot.game.dealing.Dealing;
import bg.sofia.uni.fmi.mjt.bellot.game.dealing.DealingPlay;
import bg.sofia.uni.fmi.mjt.bellot.game.dealing.DealingResult;
import bg.sofia.uni.fmi.mjt.bellot.game.player.Deck;
import bg.sofia.uni.fmi.mjt.bellot.game.player.cyclemanager.PlayerCycle;
import bg.sofia.uni.fmi.mjt.bellot.game.player.cyclemanager.PlayerCycleManager;
import bg.sofia.uni.fmi.mjt.bellot.game.player.playerhand.Player;
import bg.sofia.uni.fmi.mjt.bellot.game.player.playerhand.PlayerHandBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class GamePlay {
    private static final int POINTS_NEEDED = 151;
    private final PlayerCycleManager playerCycleManager;
    private final PlayerCommunicator playerCommunicator;
    private final List<Integer> playersIds;
    private final Map<Integer, Integer> playersTeams = new HashMap<>();
    private final Map<Integer, Integer> pointsByTeam = new HashMap<>();
    private int hangingPoints = 0;

    private GamePlay(List<Integer> playersIds, PlayerCommunicator playerCommunicator) {

        this.playerCommunicator = playerCommunicator;

        this.playersIds = playersIds;
        pointsByTeam.put(0, 0);
        pointsByTeam.put(1, 0);

        for (int i = 0; i < playersIds.size(); i++) {
            playersTeams.put(playersIds.get(i), i % 2);
        }

        this.playerCycleManager = new PlayerCycle(playersIds, playersTeams, 0);

        playerCommunicator.setTeams(playersTeams);
    }

    public static GameResult start(List<Integer> playersIds, PlayerCommunicator playerCommunicator) {
        GamePlay gamePlay = new GamePlay(playersIds, playerCommunicator);
        return gamePlay.play();
    }

    public static Set<Integer> getPlayersFromTeam(int teamId, Map<Integer, Integer> playersTeams) {
        return playersTeams.entrySet().stream()
                .filter(team -> team.getValue().equals(teamId))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    public static GameResult getGameResult(Map<Integer, Integer> pointsByTeam, Map<Integer, Integer> playersTeams) {
        Map.Entry<Integer, Integer> winningTeam = pointsByTeam.entrySet().stream()
                .max(Map.Entry.comparingByValue()).orElse(null);

        if (winningTeam == null || winningTeam.getValue() < POINTS_NEEDED) {
            return null;
        }

        Set<Integer> winners = getPlayersFromTeam(winningTeam.getKey(), playersTeams);
        Integer looserPoints = pointsByTeam.entrySet().stream()
                .min(Map.Entry.comparingByValue())
                .map(Map.Entry::getValue)
                .orElse(0);

        return new GameResult(winners, winningTeam.getValue(), looserPoints);
    }

    private GameResult play() {
        GameResult gameResult = null;

        while (gameResult == null) {
            playDealing();
            gameResult = getGameResult(pointsByTeam, playersTeams);
        }

        playerCommunicator.updateGameWon(gameResult.playersIds());

        return gameResult;
    }

    private Map<Integer, PlayerHandBuilder> splitCards() {
        List<PlayerHandBuilder> handBuilders = Deck.generateShuffle();
        Map<Integer, PlayerHandBuilder> handsBuilderByPlayer = new HashMap<>();
        int currentHand = 0;

        for (int playerId : playersIds) {
            playerCommunicator.setCards(playerId, handBuilders.get(currentHand).getCards());
            handsBuilderByPlayer.put(playerId, handBuilders.get(currentHand));
            currentHand++;
        }

        return handsBuilderByPlayer;
    }

    private void splitPoints(DealingResult result) {
        playerCommunicator.updateDealingTaken(getPlayersFromTeam(result.winningTeamId(), playersTeams));

        for (var teamPoints : result.pointByTeam().entrySet()) {
            int teamId = teamPoints.getKey();
            pointsByTeam.put(teamId, pointsByTeam.get(teamId) + teamPoints.getValue());
        }

        int winningTeamId = result.winningTeamId();
        if (winningTeamId != -1) {
            pointsByTeam.put(winningTeamId, pointsByTeam.get(winningTeamId) + hangingPoints);
            hangingPoints = 0;
        }

        hangingPoints += result.hangingPoints();
    }

    private void playDealing() {
        Map<Integer, PlayerHandBuilder> handsBuilderByPlayer = splitCards();

        int currentPlayer = playerCycleManager.getCurrentPlayerId();

        Dealing dealingState = BiddingPlay.start(
                playerCycleManager.startNewCycle(currentPlayer),
                playerCommunicator
        );

        if (dealingState == null) {
            return;
        }

        Map<Integer, Player> handsByPlayer = Deck.createHands(dealingState.getBidding(), handsBuilderByPlayer);

        DealingResult result = DealingPlay.start(
                dealingState,
                handsByPlayer,
                playerCommunicator,
                playerCycleManager.startNewCycle(currentPlayer)
        );

        splitPoints(result);
    }
}
