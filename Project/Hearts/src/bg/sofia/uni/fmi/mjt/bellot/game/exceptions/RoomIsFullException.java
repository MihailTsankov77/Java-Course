package bg.sofia.uni.fmi.mjt.bellot.game.exceptions;

public class RoomIsFullException extends RuntimeException {
    public RoomIsFullException(String message) {
        super(message);
    }

    public RoomIsFullException(String message, Exception e) {
        super(message, e);
    }
}
