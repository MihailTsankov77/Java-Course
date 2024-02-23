package bg.sofia.uni.fmi.mjt.bellot.server.response;

import bg.sofia.uni.fmi.mjt.bellot.game.bidding.types.PlayerContractOption;
import bg.sofia.uni.fmi.mjt.bellot.server.response.types.ClientAction;
import bg.sofia.uni.fmi.mjt.bellot.server.response.types.ResponseType;

import java.util.Set;

public class BidResponse extends ServerResponseBase {

    private final Set<PlayerContractOption> options;

    public BidResponse(Set<PlayerContractOption> options) {
        super(ClientAction.RESPOND, ResponseType.BID);
        this.options = options;
    }

    public Set<PlayerContractOption> getOptions() {
        return options;
    }
}
