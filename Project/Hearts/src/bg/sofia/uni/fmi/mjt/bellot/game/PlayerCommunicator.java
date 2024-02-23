package bg.sofia.uni.fmi.mjt.bellot.game;

import bg.sofia.uni.fmi.mjt.bellot.game.bidding.types.PlayerContractOption;
import bg.sofia.uni.fmi.mjt.bellot.game.cards.Card;
import bg.sofia.uni.fmi.mjt.bellot.game.player.playerhand.PlayableOptions;
import bg.sofia.uni.fmi.mjt.bellot.game.player.playerhand.PlayedOption;

import java.util.Map;
import java.util.Set;

public interface PlayerCommunicator {

    /**
     * Set the cards to the player
     *
     * @param playerId - for whom are the cards
     * @param cards
     */
    void setCards(int playerId, Set<Card> cards);

    /**
     * Update the other players what the current one is chosen
     * sends a message to all players except the current
     *
     * @param playerId - the player that have made the play
     * @param option   - the play
     */
    void updateOthersForBid(int playerId, PlayerContractOption option);

    /**
     * Update the other players what the current one is has played
     * sends a message to all players except the current
     *
     * @param playerId - the player that have made the play
     * @param card     - the play
     */
    void updateOthersForPlay(int playerId, Card card);

    /**
     * Update all players of their teammate
     *
     * @param playerTeams - the config for teams
     */
    void setTeams(Map<Integer, Integer> playerTeams);

    /**
     * Update all the players who have taken the hand
     *
     * @param playerId - the player that have made the play
     */
    void updateHandTaken(int playerId);

    /**
     * Update all the players who have taken the dealing
     *
     * @param players - the players that have made won
     */
    void updateDealingTaken(Set<Integer> players);

    /**
     * Update all the players who have taken the game
     *
     * @param players - the players that have made won
     */
    void updateGameWon(Set<Integer> players);

    /**
     * Get the play from player
     *
     * @param playerId - the player that is on turn
     * @param options  - what he can play, consist from cards and declarations
     * @return the chosen option from the player
     */
    PlayedOption getPlayedOption(int playerId, PlayableOptions options);

    /**
     * Get the bid from player
     *
     * @param playerId - the player that is on turn
     * @param options  - what he can bid
     * @return the chosen option from the player
     */
    PlayerContractOption getBidding(int playerId, Set<PlayerContractOption> options);
}
