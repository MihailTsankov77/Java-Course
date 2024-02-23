package bg.sofia.uni.fmi.mjt.bellot.client.response;

import bg.sofia.uni.fmi.mjt.bellot.client.response.types.ClientResponseType;

public abstract class ClientResponseBase {

    private final ClientResponseType type;

    public ClientResponseBase(ClientResponseType type) {
        this.type = type;
    }

    public ClientResponseType getType() {
        return type;
    }
}
