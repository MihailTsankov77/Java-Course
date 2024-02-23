package bg.sofia.uni.fmi.mjt.cookingcompass.exceptions;

public class NoMoreDataException extends Exception {

    public NoMoreDataException(String message) {
        super(message);
    }

    public NoMoreDataException(String message, Exception e) {
        super(message, e);
    }
}
