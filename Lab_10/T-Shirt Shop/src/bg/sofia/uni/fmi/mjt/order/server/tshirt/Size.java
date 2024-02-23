package bg.sofia.uni.fmi.mjt.order.server.tshirt;

public enum Size {
    S("S"),
    M("M"),
    L("L"),
    XL("XL"),
    UNKNOWN("UNKNOWN");

    private final String name;

    Size(String name) {
        this.name = name;
    }

    public static Size from(String value) {
        for (Size enumValue : Size.values()) {
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