package bg.sofia.uni.fmi.mjt.space.mission;

import bg.sofia.uni.fmi.mjt.space.exception.ParseException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record Detail(String rocketName, String payload) {

    private static final String ROCKET_NAME = "RocketName";
    private static final String PAYLOAD = "Payload";
    private static final Pattern PATTERN = Pattern.compile(STR. "(?<\{ ROCKET_NAME }>.*?)\\s*\\|\\s*"
            + STR. "(?<\{ PAYLOAD }>.*)" );

    public static Detail parse(String row) {
        Matcher matcher = PATTERN.matcher(row);

        if (!matcher.matches()) {
            throw new ParseException("Something went wrong parsing details: " + row);
        }

        return new Detail(matcher.group(ROCKET_NAME), matcher.group(PAYLOAD));
    }
}