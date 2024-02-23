package bg.sofia.uni.fmi.mjt.bellot.server.room;

import bg.sofia.uni.fmi.mjt.bellot.game.bidding.types.PlayerContractOption;
import bg.sofia.uni.fmi.mjt.bellot.game.cards.Card;
import bg.sofia.uni.fmi.mjt.bellot.game.cards.types.CardColor;
import bg.sofia.uni.fmi.mjt.bellot.game.cards.types.CardValue;
import bg.sofia.uni.fmi.mjt.bellot.server.player.Player;
import bg.sofia.uni.fmi.mjt.bellot.server.response.OtherBidResponse;
import bg.sofia.uni.fmi.mjt.bellot.server.response.OtherPlayCardResponse;
import bg.sofia.uni.fmi.mjt.bellot.server.response.ServerResponseParser;
import bg.sofia.uni.fmi.mjt.bellot.server.response.SetCardsResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CommunicatorTest {


    static Selector mockedSelector;
    static SelectionKey key1;
    static SelectionKey key2;

    static Map<SocketChannel, Player> players;
    static List<Integer> playersIds = List.of(1, 2);

    static Communicator communicator;
    static SocketChannel socketChannel1;
    static SocketChannel socketChannel2;

    @BeforeEach
    void createRecourses() throws IOException {
        mockedSelector = mock();
        when(mockedSelector.select()).thenReturn(4);

        key1 = mock();
        when(key1.isReadable()).thenReturn(true);
        when(key1.isWritable()).thenReturn(true);
        socketChannel1 = mock();
        when(key1.channel()).thenReturn(socketChannel1);

        key2 = mock();
        when(key2.isReadable()).thenReturn(true);
        when(key2.isWritable()).thenReturn(true);
        socketChannel2 = mock();
        when(key2.channel()).thenReturn(socketChannel2);

        when(mockedSelector.selectedKeys()).thenReturn(new HashSet<>(List.of(key1, key2)));

        Player player1 = mock();
        doNothing().when(player1).write(anyString());
        when(player1.getId()).thenReturn(1);
        Player player2 = mock();
        players = Map.of(socketChannel1, player1, socketChannel2, player2);
        doNothing().when(player2).write(anyString());
        when(player2.getId()).thenReturn(2);

        communicator = new Communicator(players, mockedSelector, playersIds);
        communicator.setNames(Map.of(1, "1", 2, "2"));
    }

    @Test
    void testSetCards() {
        Set<Card> cards = Set.of(new Card(CardColor.SPADE, CardValue.JACK));
        communicator.setCards(1, cards);
        String expected = ServerResponseParser.toJson(new SetCardsResponse(cards));

        verify(players.get(socketChannel1)).write(expected);
    }

    @Test
    void testUpdateOthersForBid() {
        communicator.updateOthersForBid(1, PlayerContractOption.ALL_TRUMPS);
        String expected = ServerResponseParser.toJson(new OtherBidResponse("1", PlayerContractOption.ALL_TRUMPS));

        verify(players.get(socketChannel2)).write(expected);
    }

    @Test
    void testUpdateOthersForPlay() {
        Card card = new Card(CardColor.SPADE, CardValue.JACK);
        communicator.updateOthersForPlay(1, card);
        String expected = ServerResponseParser.toJson(new OtherPlayCardResponse("1", card));

        verify(players.get(socketChannel2)).write(expected);
    }

}
