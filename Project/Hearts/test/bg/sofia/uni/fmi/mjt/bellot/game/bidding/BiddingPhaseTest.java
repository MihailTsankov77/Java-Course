package bg.sofia.uni.fmi.mjt.bellot.game.bidding;

import bg.sofia.uni.fmi.mjt.bellot.game.bidding.types.PlayerContractOption;
import bg.sofia.uni.fmi.mjt.bellot.game.exceptions.IncorrectPlayException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BiddingPhaseTest {

    BiddingManager manager;

    @BeforeEach
    public void createBiddingPhase() {
        manager = new BiddingPhase(Set.of(0, 1));
    }

    @Test
    public void testIncorrectTeam() {
        assertThrows(IllegalArgumentException.class, () -> manager.playerBid(PlayerContractOption.PASS, 4));
    }

    @Test
    public void testFourPasses() throws IncorrectPlayException {
        assertFalse(manager.playerBid(PlayerContractOption.PASS, 0));
        assertFalse(manager.playerBid(PlayerContractOption.PASS, 1));
        assertFalse(manager.playerBid(PlayerContractOption.PASS, 0));
        assertTrue(manager.playerBid(PlayerContractOption.PASS, 1),
                "When four passes are present the bidding ends");

        assertNull(manager.buildDealingState(), "when there is no bidding");
    }

    @Test
    public void testAcceptPasses() throws IncorrectPlayException {
        assertFalse(manager.playerBid(PlayerContractOption.ALL_TRUMPS, 0));
        assertFalse(manager.playerBid(PlayerContractOption.PASS, 1));
        assertFalse(manager.playerBid(PlayerContractOption.PASS, 0));
        assertTrue(manager.playerBid(PlayerContractOption.PASS, 1),
                "When three passes are present the bidding is accepted");

        assertNotNull(manager.buildDealingState(), "when there is bidding there is dealing state");
    }


    @Test
    public void testContra() throws IncorrectPlayException {
        manager.playerBid(PlayerContractOption.ALL_TRUMPS, 0);

        assertEquals(Set.of(PlayerContractOption.PASS), manager.getPlayerOptions(0),
                "The same team can not say contra");

        assertEquals(Set.of(PlayerContractOption.PASS,
                        PlayerContractOption.CONTRA,
                        PlayerContractOption.RE_CONTRA
                ), manager.getPlayerOptions(1),
                "The other team can say contra");

        manager.playerBid(PlayerContractOption.CONTRA, 1);

        assertEquals(Set.of(PlayerContractOption.PASS), manager.getPlayerOptions(1),
                "The same team can not say re-contra");

        assertEquals(Set.of(PlayerContractOption.PASS,
                        PlayerContractOption.RE_CONTRA
                ), manager.getPlayerOptions(0),
                "The other team can say re-contra");
    }


}
