package bg.sofia.uni.fmi.mjt.itinerary;

public record Location(int x, int y) {
    private static final double METERS_TO_KILOMETERS_RATIO = 1000;

    static double getDistanceBetween(Location location1, Location location2) {
        double distX = location1.x - location2.x;
        double distY = location1.y - location2.y;

        return (Math.abs(distX) + Math.abs(distY)) / METERS_TO_KILOMETERS_RATIO;
    }
}