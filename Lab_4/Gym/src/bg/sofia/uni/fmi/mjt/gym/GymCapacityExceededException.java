package bg.sofia.uni.fmi.mjt.gym;

public class GymCapacityExceededException extends Exception {
    public GymCapacityExceededException(int capacity) {
        super("GymCapacityExceededException: capacity of " + capacity + "exceeded");
    }
}