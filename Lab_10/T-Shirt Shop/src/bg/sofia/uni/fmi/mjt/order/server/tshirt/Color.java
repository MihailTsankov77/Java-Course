package bg.sofia.uni.fmi.mjt.order.server.tshirt;

public enum Color {
    BLACK("BLACK"),
    WHITE("WHITE"),
    RED("RED"),
    UNKNOWN("UNKNOWN");

    private final String name;

    Color(String name) {
        this.name = name;
    }

    public static Color from(String value) {
        for (Color enumValue : Color.values()) {
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