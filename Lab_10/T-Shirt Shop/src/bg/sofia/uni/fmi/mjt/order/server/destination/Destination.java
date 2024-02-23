package bg.sofia.uni.fmi.mjt.order.server.destination;

public enum Destination {
    EUROPE("EUROPE"),
    NORTH_AMERICA("NORTH_AMERICA"),
    AUSTRALIA("AUSTRALIA"),
    UNKNOWN("UNKNOWN");

    private final String name;

    Destination(String name) {
        this.name = name;
    }

    public static Destination from(String value) {
        for (Destination enumValue : Destination.values()) {
            if (enumValue.name.equals(value)) {
                return enumValue;
            }
        }

        return UNKNOWN;
    }

    public String getName() {
        return name;
    }
}