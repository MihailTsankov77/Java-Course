package bg.sofia.uni.fmi.mjt.gym.member;

public record Address(double longitude, double latitude) {
    public double getDistanceTo(Address other) {
        double x = Math.pow(longitude - other.longitude, 2);
        double y = Math.pow(latitude - other.latitude, 2);

        return Math.sqrt(x + y);
    }
}