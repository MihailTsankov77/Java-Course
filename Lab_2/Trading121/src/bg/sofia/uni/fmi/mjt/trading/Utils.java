package bg.sofia.uni.fmi.mjt.trading;

import static java.lang.Math.round;

public class Utils {
    private static final double PRECISION = 100;

    public static double format(double input) {
        return ((double) round(input * PRECISION)) / PRECISION;
    }
}
