package bg.sofia.uni.fmi.mjt.cookingcompass.exceptions;

public class ConnectionException extends Exception {

    public ConnectionException(String message) {
        super(message);
    }

    public ConnectionException(String message, Exception e) {
        super(message, e);
    }
}
