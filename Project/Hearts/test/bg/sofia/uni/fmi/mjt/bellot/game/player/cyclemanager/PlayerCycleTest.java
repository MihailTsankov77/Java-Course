package bg.sofia.uni.fmi.mjt.bellot.game.player.cyclemanager;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PlayerCycleTest {

    static PlayerCycleManager manager;

    @BeforeAll
    static void create() {
        manager = new PlayerCycle(
                List.of(1, 2, 3, 4),
                Map.of(1, 1, 2, 1, 3, 2, 4, 2),
                0);
    }

    @Test
    void testCycle() {
        assertEquals(1, manager.getCurrentPlayerId());
        assertEquals(2, manager.getCurrentPlayerId());
        assertEquals(3, manager.getCurrentPlayerId());
        assertEquals(4, manager.getCurrentPlayerId());
        assertEquals(1, manager.getCurrentPlayerId());
        assertEquals(2, manager.getCurrentPlayerId());
        assertEquals(3, manager.getCurrentPlayerId());
        assertEquals(4, manager.getCurrentPlayerId());
    }

    @Test
    void testCreateNewCycle() {
        PlayerCycleManager newManager = manager.startNewCycle(1);

        assertEquals(1, newManager.getCurrentPlayerId());
        assertEquals(2, newManager.getCurrentPlayerId());
        assertEquals(3, newManager.getCurrentPlayerId());
        assertEquals(4, newManager.getCurrentPlayerId());
        assertEquals(1, newManager.getCurrentPlayerId());
        assertEquals(2, newManager.getCurrentPlayerId());
        assertEquals(3, newManager.getCurrentPlayerId());
        assertEquals(4, newManager.getCurrentPlayerId());
    }

    @Test
    void testGetPLayers() {
        assertEquals(Set.of(1, 2, 3, 4), manager.getPlayersIds());
    }

    @Test
    void testGetTeams() {
        assertEquals(Set.of(1, 2), manager.getTeamsIds());
        assertEquals(1, manager.getPlayerTeam(1));
    }

}
