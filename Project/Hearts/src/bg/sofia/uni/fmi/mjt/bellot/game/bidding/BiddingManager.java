package bg.sofia.uni.fmi.mjt.bellot.game.bidding;

import bg.sofia.uni.fmi.mjt.bellot.game.bidding.types.PlayerContractOption;
import bg.sofia.uni.fmi.mjt.bellot.game.dealing.DealingState;
import bg.sofia.uni.fmi.mjt.bellot.game.exceptions.IncorrectPlayException;

import java.util.Set;

public interface BiddingManager {

    /**
     * Controls what the player can say
     *
     * @param playerTeam - used for contra and re-contra
     * @return all the possible options for the user
     */
    Set<PlayerContractOption> getPlayerOptions(int playerTeam);

    /**
     * Save the player option and update the state of the bidding
     *
     * @param biddingOption - what the player have chosen
     * @param playerTeam    - the player teams
     * @return - if true the bidding is finished
     * @throws IncorrectPlayException - if you make a play that is not in the playable options
     */
    boolean playerBid(PlayerContractOption biddingOption, int playerTeam) throws IncorrectPlayException;

    /**
     * Used to build the DealingState after the bidding is finished
     */
    DealingState buildDealingState();
}
