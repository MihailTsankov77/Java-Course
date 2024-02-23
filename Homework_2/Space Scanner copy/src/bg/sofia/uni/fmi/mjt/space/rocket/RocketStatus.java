package bg.sofia.uni.fmi.mjt.space.rocket;

public enum RocketStatus {
    STATUS_RETIRED("StatusRetired"),
    STATUS_ACTIVE("StatusActive");

    private final String value;

    RocketStatus(String value) {
        this.value = value;
    }

    public static RocketStatus fromValue(String value) {
        for (RocketStatus enumValue : RocketStatus.values()) {
            if (enumValue.value.equals(value)) {
                return enumValue;
            }
        }

        throw new IllegalArgumentException("No enum constant with value: " + value);
    }

    public String toString() {
        return value;
    }
}