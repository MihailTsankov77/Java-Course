package bg.sofia.uni.fmi.mjt.bellot.game.player.cyclemanager;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PlayerCycle implements PlayerCycleManager {
    private final List<Integer> playersIds;

    private final Map<Integer, Integer> playersTeams;
    private int currentPlayerIndex;

    public PlayerCycle(List<Integer> playersIds, Map<Integer, Integer> playersTeams, int startIndex) {
        this.playersIds = playersIds;
        this.playersTeams = playersTeams;
        currentPlayerIndex = startIndex;
    }

    @Override
    public int getCurrentPlayerId() {
        int playerId = playersIds.get(currentPlayerIndex);
        currentPlayerIndex = (currentPlayerIndex + 1) % playersIds.size();

        return playerId;
    }

    @Override
    public int getPlayerTeam(int playerId) {
        return playersTeams.get(playerId);
    }

    @Override
    public PlayerCycleManager startNewCycle(int startPlayerId) {
        int startIndex = playersIds.indexOf(startPlayerId);
        return new PlayerCycle(playersIds, playersTeams, startIndex);
    }

    @Override
    public Set<Integer> getPlayersIds() {
        return new HashSet<>(playersIds);
    }

    @Override
    public Set<Integer> getTeamsIds() {
        return new HashSet<>(playersTeams.values());
    }
}
