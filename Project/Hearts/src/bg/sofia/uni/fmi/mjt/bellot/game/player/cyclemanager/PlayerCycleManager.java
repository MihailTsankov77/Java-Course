package bg.sofia.uni.fmi.mjt.bellot.game.player.cyclemanager;

import java.util.Set;

public interface PlayerCycleManager {
    int getCurrentPlayerId();

    int getPlayerTeam(int playerId);

    PlayerCycleManager startNewCycle(int startPlayerId);

    Set<Integer> getPlayersIds();

    Set<Integer> getTeamsIds();
}
