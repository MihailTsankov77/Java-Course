package bg.sofia.uni.fmi.mjt.bellot.client.response;

import bg.sofia.uni.fmi.mjt.bellot.client.response.types.ClientResponseType;
import bg.sofia.uni.fmi.mjt.bellot.game.bidding.types.PlayerContractOption;

public class ClientBidResponse extends ClientResponseBase {

    private final PlayerContractOption option;

    public ClientBidResponse(PlayerContractOption option) {
        super(ClientResponseType.BID);
        this.option = option;
    }

    public PlayerContractOption getOption() {
        return option;
    }
}
