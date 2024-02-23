package bg.sofia.uni.fmi.mjt.bellot.client.player;

import bg.sofia.uni.fmi.mjt.bellot.client.response.ClientBidResponse;
import bg.sofia.uni.fmi.mjt.bellot.client.response.ClientPlayCardResponse;
import bg.sofia.uni.fmi.mjt.bellot.client.response.ClientResponseParser;
import bg.sofia.uni.fmi.mjt.bellot.game.bidding.types.PlayerContractOption;
import bg.sofia.uni.fmi.mjt.bellot.game.cards.Card;
import bg.sofia.uni.fmi.mjt.bellot.game.cards.declarations.Declarations;
import bg.sofia.uni.fmi.mjt.bellot.game.cards.types.CardValue;
import bg.sofia.uni.fmi.mjt.bellot.game.player.playerhand.PlayedOption;
import bg.sofia.uni.fmi.mjt.bellot.server.response.BidResponse;
import bg.sofia.uni.fmi.mjt.bellot.server.response.IndividualAnnouncementResponse;
import bg.sofia.uni.fmi.mjt.bellot.server.response.OtherBidResponse;
import bg.sofia.uni.fmi.mjt.bellot.server.response.OtherPlayCardResponse;
import bg.sofia.uni.fmi.mjt.bellot.server.response.PlayCardResponse;
import bg.sofia.uni.fmi.mjt.bellot.server.response.ServerResponseBase;
import bg.sofia.uni.fmi.mjt.bellot.server.response.ServerResponseParser;
import bg.sofia.uni.fmi.mjt.bellot.server.response.SetCardsResponse;
import bg.sofia.uni.fmi.mjt.bellot.server.response.TeamAnnouncementResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class ClientPlayer {
    private static final Scanner SCANNER = new Scanner(System.in);
    private final BufferedReader reader;
    private final PrintWriter writer;

    private ClientPlayer(BufferedReader reader, PrintWriter writer) {
        this.reader = reader;
        this.writer = writer;
    }

    public static void start(BufferedReader reader, PrintWriter writer) {
        new ClientPlayer(reader, writer).start();
    }

    private void start() {
        connect();
        play();
    }

    private void connect() {
        System.out.println("Set Name: ");
        String name = SCANNER.nextLine();
        writer.println(name);
    }

    private void play() {
        try {
            while (true) {
                String response = reader.readLine();

                if (response == null) {
                    continue;
                }

                if (!response.isBlank()) {
                    handleResponse(response);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleResponse(String rawResponse) {
        ServerResponseBase response = ServerResponseParser.fromJson(rawResponse);
        switch (response.getType()) {
            case SET_CARDS -> showCards((SetCardsResponse) response);
            case OTHER_BID -> showBid((OtherBidResponse) response);
            case OTHER_PLAY_CARD -> showCardPlayed((OtherPlayCardResponse) response);
            case PLAY_CARD -> handlePlayCard((PlayCardResponse) response);
            case BID -> handleBid((BidResponse) response);
            case SET_TEAMS -> showTeam((IndividualAnnouncementResponse) response);
            case HAND_TAKEN -> showHandTaken((IndividualAnnouncementResponse) response);
            case GAME_RESULTS -> showGameResult((TeamAnnouncementResponse) response);
            case DEALING_TAKEN -> showDealingTaken((TeamAnnouncementResponse) response);
        }
    }

    private void showCards(SetCardsResponse response) {
        System.out.println("Your cards are:");
        System.out.println(response.getCards());
    }

    private void showBid(OtherBidResponse response) {
        System.out.println("Player " + response.getPlayerName() + " says " + response.getOption());
    }

    private void showCardPlayed(OtherPlayCardResponse response) {
        System.out.println("Player " + response.getPlayerName() + " played " + response.getCard());
    }

    private void handleBid(BidResponse response) {
        System.out.println("Your turn to bid, your options are:");

        List<PlayerContractOption> options = response.getOptions().stream().toList();
        for (int i = 0; i < options.size(); i++) {
            System.out.println((i + 1) + ". " + options.get(i));
        }

        int optionIndex = SCANNER.nextInt();

        writer.println(ClientResponseParser.toJson(new ClientBidResponse(options.get(optionIndex - 1))));
    }

    private Card getCard(List<Card> cards) {
        for (int i = 0; i < cards.size(); i++) {
            System.out.println((i + 1) + ". " + cards.get(i));
        }

        int cardIndex = SCANNER.nextInt();

        return cards.get(cardIndex - 1);
    }

    private Set<Declarations> getDeclarations(List<Declarations> declarations) {
        Set<Declarations> chosenDeclarations = new HashSet<>();
        if (!declarations.isEmpty()) {
            for (int i = 0; i < declarations.size(); i++) {
                System.out.println((i + 1) + ". " + declarations.get(i));
            }

            int declarationIndex = SCANNER.nextInt();

            chosenDeclarations.add(declarations.get(declarationIndex - 1));
        }

        return chosenDeclarations;
    }

    private void handlePlayCard(PlayCardResponse response) {
        System.out.println("Your turn to play, your options are:");

        Card card = getCard(response.getOptions().cards().stream().toList());

        boolean isCardForBellot = card.value().equals(CardValue.QUEEN) || card.value().equals(CardValue.KING);
        Declarations bellotDeclaration = Declarations.of(card.color());

        List<Declarations> declarations = response.getOptions().declarations().stream()
                .filter(declaration -> {
                    if (!Declarations.BELLOT.contains(declaration)) {
                        return true;
                    }

                    return isCardForBellot && bellotDeclaration.equals(declaration);
                }).toList();

        Set<Declarations> chosenDeclarations = getDeclarations(declarations);
        PlayedOption playedOption = new PlayedOption(card, chosenDeclarations);

        writer.println(ClientResponseParser.toJson(new ClientPlayCardResponse(playedOption)));
    }

    private void showTeam(IndividualAnnouncementResponse response) {
        System.out.println("Your team member is " + response.getPlayerName());
    }

    private void showHandTaken(IndividualAnnouncementResponse response) {
        System.out.println("The hand was taken by " + response.getPlayerName());
    }

    private void showDealingTaken(TeamAnnouncementResponse response) {
        String names = String.join(", ", response.getPlayerNames());
        System.out.println("The dealing was taken by " + names);
    }

    private void showGameResult(TeamAnnouncementResponse response) {
        String names = String.join(", ", response.getPlayerNames());
        System.out.println("The bg.sofia.uni.fmi.mjt.bellot.client.game was won by " + names);
    }

}
