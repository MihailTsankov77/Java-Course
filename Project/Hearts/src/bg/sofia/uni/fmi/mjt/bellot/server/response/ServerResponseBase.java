package bg.sofia.uni.fmi.mjt.bellot.server.response;

import bg.sofia.uni.fmi.mjt.bellot.server.response.types.ClientAction;
import bg.sofia.uni.fmi.mjt.bellot.server.response.types.ResponseType;

public abstract class ServerResponseBase {

    private final ClientAction action;
    private final ResponseType type;

    public ServerResponseBase(ClientAction action, ResponseType type) {
        this.action = action;
        this.type = type;
    }

    public ClientAction getAction() {
        return action;
    }

    public ResponseType getType() {
        return type;
    }
}