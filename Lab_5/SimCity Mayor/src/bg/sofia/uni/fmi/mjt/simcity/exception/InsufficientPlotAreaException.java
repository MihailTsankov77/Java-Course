package bg.sofia.uni.fmi.mjt.simcity.exception;

public class InsufficientPlotAreaException extends RuntimeException {
    public InsufficientPlotAreaException(int plotArea) {
        super("InsufficientPlotAreaException: not enough space left -> " + plotArea);
    }
}
