package bg.sofia.uni.fmi.mjt.bellot.game.bidding;

import bg.sofia.uni.fmi.mjt.bellot.game.bidding.types.Bidding;
import bg.sofia.uni.fmi.mjt.bellot.game.bidding.types.Doubling;
import bg.sofia.uni.fmi.mjt.bellot.game.bidding.types.PlayerContractOption;
import bg.sofia.uni.fmi.mjt.bellot.game.dealing.DealingState;
import bg.sofia.uni.fmi.mjt.bellot.game.exceptions.IncorrectPlayException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BiddingPhase implements BiddingManager {

    private static final int ACCEPT_BID_PASSES = 3;
    private static final int GIVE_UP_PASSES = 4;
    private final Set<Integer> teamsIds;
    private Bidding currentBinding = null;
    private Doubling currentDoubling = Doubling.NONE;
    private int teamId;
    private int passCount = 0;

    private List<Bidding> leftBidding = Bidding.getBiddingSequence();
    private List<Doubling> leftDoubling = Doubling.getDoublingSequence();

    public BiddingPhase(Set<Integer> teamsIds) {
        this.teamsIds = teamsIds;
    }

    private boolean handleBidding(Bidding bidding, int teamId) throws IncorrectPlayException {
        int biddingIndex = leftBidding.indexOf(bidding);

        if (biddingIndex == -1) {
            throw new IncorrectPlayException("The player could not say " +
                    bidding + " he could only play " + leftBidding);
        }

        leftBidding = leftBidding.subList(biddingIndex + 1, leftBidding.size());

        leftDoubling = Doubling.getDoublingSequence();
        currentDoubling = Doubling.NONE;
        passCount = 0;

        currentBinding = bidding;
        this.teamId = teamId;

        return false;
    }

    private boolean handleDoubling(Doubling doubling, int teamId) throws IncorrectPlayException {
        if (this.teamId == teamId) {
            throw new IncorrectPlayException("You can not double if you are the same team");
        }

        if (currentBinding == null) {
            throw new IncorrectPlayException("You should bind first");
        }

        int doublingIndex = leftDoubling.indexOf(doubling);

        if (doublingIndex == -1) {
            throw new IncorrectPlayException("The player could not say " +
                    doubling + " he could only play " + leftDoubling);
        }

        leftDoubling = leftDoubling.subList(doublingIndex + 1, leftDoubling.size());

        this.passCount = 0;
        this.teamId = teamId;
        this.currentDoubling = doubling;

        return false;
    }

    private boolean handlePass() {
        passCount++;

        if (currentBinding != null && passCount == ACCEPT_BID_PASSES) {
            return true;
        }

        return passCount == GIVE_UP_PASSES;
    }

    @Override
    public boolean playerBid(PlayerContractOption option, int teamId) throws IncorrectPlayException {
        if (!teamsIds.contains(teamId)) {
            throw new IllegalArgumentException("This team is not part of the bg.sofia.uni.fmi.mjt.bellot.client.game");
        }

        return switch (option) {
            case SPADES, CLUBS, HEARTS, DIAMONDS, NO_TRUMPS, ALL_TRUMPS -> handleBidding(Bidding.of(option), teamId);
            case CONTRA, RE_CONTRA -> handleDoubling(Doubling.of(option), teamId);
            case PASS -> handlePass();
        };
    }

    public Set<PlayerContractOption> getPlayerOptions(int teamId) {
        Set<PlayerContractOption> options = new HashSet<>();

        for (Bidding bidding : leftBidding) {
            options.add(PlayerContractOption.of(bidding));
        }

        if (teamId != this.teamId) {
            for (Doubling doubling : leftDoubling) {
                options.add(PlayerContractOption.of(doubling));
            }
        }

        options.add(PlayerContractOption.PASS);

        return options;
    }

    public DealingState buildDealingState() {
        if (passCount == GIVE_UP_PASSES) {
            return null;
        }

        return new DealingState(teamsIds, currentBinding, currentDoubling, teamId);
    }
}
