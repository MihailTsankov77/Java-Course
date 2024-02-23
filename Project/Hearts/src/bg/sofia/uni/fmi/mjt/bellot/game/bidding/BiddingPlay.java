package bg.sofia.uni.fmi.mjt.bellot.game.bidding;

import bg.sofia.uni.fmi.mjt.bellot.game.PlayerCommunicator;
import bg.sofia.uni.fmi.mjt.bellot.game.bidding.types.PlayerContractOption;
import bg.sofia.uni.fmi.mjt.bellot.game.dealing.Dealing;
import bg.sofia.uni.fmi.mjt.bellot.game.exceptions.IncorrectPlayException;
import bg.sofia.uni.fmi.mjt.bellot.game.player.cyclemanager.PlayerCycleManager;

import java.util.Set;

public class BiddingPlay {

    private final PlayerCycleManager playerCycleManager;
    private final PlayerCommunicator playerCommunicator;
    private final BiddingManager biddingPhase;

    private BiddingPlay(PlayerCycleManager playerCycleManager, PlayerCommunicator playerCommunicator) {
        this.playerCycleManager = playerCycleManager;
        this.playerCommunicator = playerCommunicator;
        biddingPhase = new BiddingPhase(playerCycleManager.getTeamsIds());
    }

    private BiddingPlay(PlayerCycleManager playerCycleManager,
                        PlayerCommunicator playerCommunicator,
                        BiddingManager biddingPhase) {
        this.playerCycleManager = playerCycleManager;
        this.playerCommunicator = playerCommunicator;
        this.biddingPhase = biddingPhase;
    }

    public static Dealing start(PlayerCycleManager playerCycleManager, PlayerCommunicator playerCommunicator) {
        BiddingPlay biddingPlay = new BiddingPlay(playerCycleManager, playerCommunicator);
        return biddingPlay.play();
    }

    public static Dealing start(PlayerCycleManager playerCycleManager,
                                PlayerCommunicator playerCommunicator,
                                BiddingManager biddingPhase) {
        BiddingPlay biddingPlay = new BiddingPlay(playerCycleManager, playerCommunicator, biddingPhase);
        return biddingPlay.play();
    }

    private boolean playerBid() {
        int playerId = playerCycleManager.getCurrentPlayerId();
        int playerTeam = playerCycleManager.getPlayerTeam(playerId);

        Set<PlayerContractOption> biddingOptions = biddingPhase.getPlayerOptions(playerTeam);
        PlayerContractOption biddingOption = playerCommunicator.getBidding(playerId, biddingOptions);

        try {
            boolean isFinalBid = biddingPhase.playerBid(biddingOption, playerTeam);
            playerCommunicator.updateOthersForBid(playerId, biddingOption);

            return isFinalBid;
        } catch (IncorrectPlayException e) {
            throw new RuntimeException(e);
        }
    }

    private Dealing play() {
        while (true) {
            if (playerBid()) break;
        }

        return biddingPhase.buildDealingState();
    }
}
