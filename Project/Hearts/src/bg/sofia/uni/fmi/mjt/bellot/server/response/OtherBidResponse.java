package bg.sofia.uni.fmi.mjt.bellot.server.response;

import bg.sofia.uni.fmi.mjt.bellot.game.bidding.types.PlayerContractOption;
import bg.sofia.uni.fmi.mjt.bellot.server.response.types.ClientAction;
import bg.sofia.uni.fmi.mjt.bellot.server.response.types.ResponseType;

public class OtherBidResponse extends ServerResponseBase {

    private final String playerName;
    private final PlayerContractOption option;

    public OtherBidResponse(String playerName, PlayerContractOption option) {
        super(ClientAction.LISTEN, ResponseType.OTHER_BID);
        this.playerName = playerName;
        this.option = option;
    }

    public PlayerContractOption getOption() {
        return option;
    }

    public String getPlayerName() {
        return playerName;
    }
}
