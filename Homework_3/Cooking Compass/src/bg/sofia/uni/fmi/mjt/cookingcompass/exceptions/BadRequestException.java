package bg.sofia.uni.fmi.mjt.cookingcompass.exceptions;

public class BadRequestException extends Exception {

    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(String message, Exception e) {
        super(message, e);
    }
}
