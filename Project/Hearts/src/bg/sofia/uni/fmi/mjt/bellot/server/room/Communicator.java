package bg.sofia.uni.fmi.mjt.bellot.server.room;

import bg.sofia.uni.fmi.mjt.bellot.client.response.ClientBidResponse;
import bg.sofia.uni.fmi.mjt.bellot.client.response.ClientPlayCardResponse;
import bg.sofia.uni.fmi.mjt.bellot.client.response.ClientResponseBase;
import bg.sofia.uni.fmi.mjt.bellot.client.response.ClientResponseParser;
import bg.sofia.uni.fmi.mjt.bellot.client.response.types.ClientResponseType;
import bg.sofia.uni.fmi.mjt.bellot.game.PlayerCommunicator;
import bg.sofia.uni.fmi.mjt.bellot.game.bidding.types.PlayerContractOption;
import bg.sofia.uni.fmi.mjt.bellot.game.cards.Card;
import bg.sofia.uni.fmi.mjt.bellot.game.player.playerhand.PlayableOptions;
import bg.sofia.uni.fmi.mjt.bellot.game.player.playerhand.PlayedOption;
import bg.sofia.uni.fmi.mjt.bellot.server.player.Player;
import bg.sofia.uni.fmi.mjt.bellot.server.response.BidResponse;
import bg.sofia.uni.fmi.mjt.bellot.server.response.IndividualAnnouncementResponse;
import bg.sofia.uni.fmi.mjt.bellot.server.response.OtherBidResponse;
import bg.sofia.uni.fmi.mjt.bellot.server.response.OtherPlayCardResponse;
import bg.sofia.uni.fmi.mjt.bellot.server.response.PlayCardResponse;
import bg.sofia.uni.fmi.mjt.bellot.server.response.ServerResponseBase;
import bg.sofia.uni.fmi.mjt.bellot.server.response.ServerResponseParser;
import bg.sofia.uni.fmi.mjt.bellot.server.response.SetCardsResponse;
import bg.sofia.uni.fmi.mjt.bellot.server.response.TeamAnnouncementResponse;

import java.io.Closeable;
import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Communicator implements PlayerCommunicator, Closeable {
    private final Map<SocketChannel, Player> players;
    private final Set<Integer> playersIds;
    private final Selector selector;
    private Map<Integer, String> playersNames = new HashMap<>();

    public Communicator(Map<SocketChannel, Player> players, Selector selector, List<Integer> playersIds) {
        this.players = players;
        this.selector = selector;
        this.playersIds = new HashSet<>(playersIds);
    }

    @Override
    public void setCards(int playerId, Set<Card> cards) {
        sendMessages(new HashSet<>(List.of(playerId)), new SetCardsResponse(cards));
    }

    @Override
    public void updateOthersForBid(int playerId, PlayerContractOption option) {
        Set<Integer> sendToIds = new HashSet<>(playersIds);
        sendToIds.remove(playerId);

        sendMessages(sendToIds, new OtherBidResponse(playersNames.get(playerId), option));
    }

    @Override
    public void updateOthersForPlay(int playerId, Card card) {
        Set<Integer> sendToIds = new HashSet<>(playersIds);
        sendToIds.remove(playerId);

        sendMessages(sendToIds, new OtherPlayCardResponse(playersNames.get(playerId), card));
    }

    @Override
    public PlayedOption getPlayedOption(int playerId, PlayableOptions options) {
        sendMessages(new HashSet<>(List.of(playerId)), new PlayCardResponse(options));
        String response = getResponse(playerId);

        ClientResponseBase responseBase = ClientResponseParser.fromJson(response);
        if (!responseBase.getType().equals(ClientResponseType.PLAY_CARD)) {
            return getPlayedOption(playerId, options);
        }

        return ((ClientPlayCardResponse) responseBase).getOption();
    }

    @Override
    public PlayerContractOption getBidding(int playerId, Set<PlayerContractOption> options) {
        sendMessages(new HashSet<>(List.of(playerId)), new BidResponse(options));
        String response = getResponse(playerId);
        ClientResponseBase responseBase = ClientResponseParser.fromJson(response);

        if (!responseBase.getType().equals(ClientResponseType.BID)) {
            return getBidding(playerId, options);
        }

        return ((ClientBidResponse) responseBase).getOption();
    }

    @Override
    public void setTeams(Map<Integer, Integer> playerTeams) {
        for (var playerTeam : playerTeams.entrySet()) {
            for (var other : playerTeams.entrySet()) {
                if (!playerTeam.getKey().equals(other.getKey()) && playerTeam.getValue().equals(other.getValue())) {
                    sendMessages(
                            new HashSet<>(List.of(playerTeam.getKey())),
                            IndividualAnnouncementResponse.createTeamResponse(playersNames.get(other.getKey()))
                    );
                }
            }
        }
    }

    @Override
    public void updateHandTaken(int playerId) {
        sendMessages(
                new HashSet<>(playersIds),
                IndividualAnnouncementResponse.createHandTakenResponse(playersNames.get(playerId))
        );
    }

    @Override
    public void updateDealingTaken(Set<Integer> players) {
        Set<String> playerNames = players.stream().map(playersNames::get).collect(Collectors.toSet());

        sendMessages(
                new HashSet<>(playersIds),
                TeamAnnouncementResponse.createDealingTakenResponse(playerNames)
        );
    }

    @Override
    public void updateGameWon(Set<Integer> players) {
        Set<String> playerNames = players.stream().map(playersNames::get).collect(Collectors.toSet());

        sendMessages(
                new HashSet<>(playersIds),
                TeamAnnouncementResponse.createGameWonResponse(playerNames)
        );
    }

    private void sendMessages(Set<Integer> sendToIds, ServerResponseBase response) {
        String jsonResponse = ServerResponseParser.toJson(response);
        while (!sendToIds.isEmpty()) {
            try {
                int readyChannels = selector.select();
                if (readyChannels == 0) {
                    continue;
                }

                Set<SelectionKey> selectedKeys = selector.selectedKeys();//
                Iterator<SelectionKey> keyIterator = selectedKeys.iterator();
                while (keyIterator.hasNext()) {
                    SelectionKey key = keyIterator.next();
                    keyIterator.remove();
                    if (!key.isWritable()) {
                        continue;
                    }

                    SocketChannel socketChannel = (SocketChannel) key.channel();
                    Player player = players.get(socketChannel);
                    if (sendToIds.contains(player.getId())) {
                        // try catch
                        player.write(jsonResponse);
                        sendToIds.remove(player.getId());
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private String getResponse(int playerId) {
        while (true) {
            try {
                int readyChannels = selector.select();
                if (readyChannels == 0) {
                    continue;
                }

                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

                while (keyIterator.hasNext()) {
                    SelectionKey key = keyIterator.next();
                    keyIterator.remove();
                    if (!key.isReadable()) {
                        continue;
                    }

                    SocketChannel socketChannel = (SocketChannel) key.channel();
                    Player player = players.get(socketChannel);

                    if (player.getId() == playerId) {
                        return player.read();
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void getNames() {
        for (int playerId : playersIds) {
            playersNames.put(playerId, getResponse(playerId));
        }
    }

    public void setNames(Map<Integer, String> playersNames) {
        this.playersNames = playersNames;
    }

    public Map<Integer, String> getPlayersNames() {
        return playersNames;
    }

    @Override
    public void close() throws IOException {
        for (Player player : players.values()) {
            player.close();
        }
    }
}
