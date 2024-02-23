package bg.sofia.uni.fmi.mjt.bellot.game.dealing;

import bg.sofia.uni.fmi.mjt.bellot.game.PlayerCommunicator;
import bg.sofia.uni.fmi.mjt.bellot.game.bidding.types.Bidding;
import bg.sofia.uni.fmi.mjt.bellot.game.cards.Card;
import bg.sofia.uni.fmi.mjt.bellot.game.cards.declarations.Declarations;
import bg.sofia.uni.fmi.mjt.bellot.game.cards.types.CardColor;
import bg.sofia.uni.fmi.mjt.bellot.game.cards.types.CardValue;
import bg.sofia.uni.fmi.mjt.bellot.game.exceptions.IncorrectPlayException;
import bg.sofia.uni.fmi.mjt.bellot.game.player.cyclemanager.PlayerCycle;
import bg.sofia.uni.fmi.mjt.bellot.game.player.cyclemanager.PlayerCycleManager;
import bg.sofia.uni.fmi.mjt.bellot.game.player.playerhand.PlayedOption;
import bg.sofia.uni.fmi.mjt.bellot.game.player.playerhand.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DealingPlayTest {

    PlayerCycleManager cycleManager;
    List<Integer> playerIds = List.of(0, 1, 2, 3);
    Map<Integer, Player> handsByPlayer;
    PlayerCommunicator communicator;
    Dealing dealing;

    @BeforeEach
    void createCustomResources() {
        communicator = mock();
        doNothing().when(communicator).updateHandTaken(anyInt());
        doNothing().when(communicator).updateOthersForPlay(anyInt(), any());

        handsByPlayer = new HashMap<>();
        for (int id : playerIds) {
            handsByPlayer.put(id, mock());
        }

        dealing = mock();
        doNothing().when(dealing).addPoints(anyInt(), anyInt());
        when(dealing.getFinalPointsForTeams()).thenReturn(mock());
        when(dealing.getBidding()).thenReturn(Bidding.ALL_TRUMPS);

        Map<Integer, Integer> playerTeams = Map.of(0, 1, 1, 2, 2, 1, 3, 2);
        cycleManager = mock();
        when(cycleManager.getCurrentPlayerId()).thenReturn(0);
        when(cycleManager.getPlayerTeam(0)).thenReturn(1);
        when(cycleManager.getPlayerTeam(1)).thenReturn(2);
        when(cycleManager.getPlayerTeam(2)).thenReturn(1);
        when(cycleManager.getPlayerTeam(3)).thenReturn(2);
        when(cycleManager.startNewCycle(anyInt()))
                .thenReturn(new PlayerCycle(playerIds, playerTeams, 0))
                .thenReturn(new PlayerCycle(playerIds, playerTeams, 1));
    }

    @Test
    void testHandleIncorrectPlay() throws IncorrectPlayException {
        when(handsByPlayer.get(0).getPlayableOptions(any())).thenReturn(mock());
        doThrow(IncorrectPlayException.class).when(handsByPlayer.get(0)).playCard(any());

        assertThrows(RuntimeException.class, () -> DealingPlay.start(
                        dealing,
                        handsByPlayer,
                        communicator,
                        cycleManager),
                "Handle irregular play");
    }

    @Test
    void testDealing() throws IncorrectPlayException {
        when(handsByPlayer.get(0).getPlayableOptions(any())).thenReturn(mock());
        doNothing().when(handsByPlayer.get(0)).playCard(any());

        when(communicator.getPlayedOption(eq(0), any()))
                .thenReturn(new PlayedOption(new Card(CardColor.HEART, CardValue.JACK), Set.of()))
                .thenReturn(new PlayedOption(new Card(CardColor.DIAMONDS, CardValue.JACK), Set.of()));

        when(communicator.getPlayedOption(eq(1), any()))
                .thenReturn(new PlayedOption(new Card(CardColor.SPADE, CardValue.JACK), Set.of(Declarations.SEQUENCE_QUINTE)))
                .thenReturn(new PlayedOption(new Card(CardColor.DIAMONDS, CardValue.EIGHT), Set.of()));

        when(communicator.getPlayedOption(eq(2), any()))
                .thenReturn(new PlayedOption(new Card(CardColor.HEART, CardValue.SEVEN), Set.of(Declarations.CARRE_HUNDRED_AN_FIFTY)))
                .thenReturn(new PlayedOption(new Card(CardColor.DIAMONDS, CardValue.SEVEN), Set.of()));

        when(communicator.getPlayedOption(eq(3), any()))
                .thenReturn(new PlayedOption(new Card(CardColor.HEART, CardValue.EIGHT), Set.of()))
                .thenReturn(new PlayedOption(new Card(CardColor.DIAMONDS, CardValue.JACK), Set.of()));


        DealingPlay.start(
                dealing,
                handsByPlayer,
                communicator,
                cycleManager,
                2);

        verify(communicator, times(7)).getPlayedOption(anyInt(), any());
        verify(communicator, times(7)).updateOthersForPlay(anyInt(), any());

        verify(dealing).addPoints(1, 190);
        verify(dealing).addPoints(2, 100);

        verify(dealing).addPoints(1, 0);
        verify(dealing).addPoints(2, 20);
    }


}

