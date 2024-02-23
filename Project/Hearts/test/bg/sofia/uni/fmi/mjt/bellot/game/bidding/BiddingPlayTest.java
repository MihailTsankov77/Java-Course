package bg.sofia.uni.fmi.mjt.bellot.game.bidding;

import bg.sofia.uni.fmi.mjt.bellot.game.PlayerCommunicator;
import bg.sofia.uni.fmi.mjt.bellot.game.exceptions.IncorrectPlayException;
import bg.sofia.uni.fmi.mjt.bellot.game.player.cyclemanager.PlayerCycleManager;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BiddingPlayTest {


    @Test
    public void testPLayerCycle() throws IncorrectPlayException {
        PlayerCycleManager mockCycleManager = mock();
        when(mockCycleManager.getCurrentPlayerId())
                .thenReturn(0)
                .thenReturn(1)
                .thenReturn(2)
                .thenReturn(3)
                .thenReturn(0);


        when(mockCycleManager.getPlayerTeam(anyInt())).thenAnswer(invocation -> {
            int argument = invocation.getArgument(0);
            return argument == 0 ? 0 : 1;
        });

        BiddingManager mockBiddingManager = mock();
        when(mockBiddingManager.getPlayerOptions(anyInt())).thenReturn(mock());

        AtomicInteger counter = new AtomicInteger(0);
        when(mockBiddingManager.playerBid(any(), anyInt())).thenAnswer(invocation -> {
            int argument = invocation.getArgument(1);
            if (argument != 0) {
                return false;
            }

            int count = counter.incrementAndGet();
            return count != 1;
        });
        when(mockBiddingManager.buildDealingState()).thenReturn(mock());

        PlayerCommunicator mockPlayerCommunicator = mock();
        when(mockPlayerCommunicator.getBidding(anyInt(), any())).thenReturn(mock());
        doNothing().when(mockPlayerCommunicator).updateOthersForBid(anyInt(), any());

        BiddingPlay.start(mockCycleManager, mockPlayerCommunicator, mockBiddingManager);

        // Verify that it cycle through players
        verify(mockCycleManager, times(5)).getCurrentPlayerId();

        verify(mockPlayerCommunicator, times(5)).getBidding(anyInt(), any());
        verify(mockPlayerCommunicator, times(5)).updateOthersForBid(anyInt(), any());
    }

    @Test
    public void testHandleException() throws IncorrectPlayException {
        PlayerCycleManager mockCycleManager = mock();
        when(mockCycleManager.getCurrentPlayerId()).thenReturn(0);

        when(mockCycleManager.getPlayerTeam(anyInt())).thenReturn(0);

        BiddingManager mockBiddingManager = mock();
        when(mockBiddingManager.getPlayerOptions(anyInt())).thenReturn(mock());

        when(mockBiddingManager.playerBid(any(), anyInt())).thenThrow(IncorrectPlayException.class);

        assertThrows(RuntimeException.class, () -> BiddingPlay.start(mockCycleManager, mock(), mockBiddingManager),
                "Handle irregular play");
    }
}
