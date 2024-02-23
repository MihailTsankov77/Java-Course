package bg.sofia.uni.fmi.mjt.bellot.game.exceptions;

public class InvalidConversionException extends RuntimeException {
    public InvalidConversionException(String message) {
        super(message);
    }

    public InvalidConversionException(String message, Exception e) {
        super(message, e);
    }
}
