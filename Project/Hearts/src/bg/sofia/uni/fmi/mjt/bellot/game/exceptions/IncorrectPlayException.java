package bg.sofia.uni.fmi.mjt.bellot.game.exceptions;

public class IncorrectPlayException extends Exception {
    public IncorrectPlayException(String message) {
        super(message);
    }

    public IncorrectPlayException(String message, Exception e) {
        super(message, e);
    }
}
