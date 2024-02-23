package bg.sofia.uni.fmi.mjt.space.rocket;

import bg.sofia.uni.fmi.mjt.space.exception.ParseException;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.function.Predicate.not;

public record Rocket(String id, String name, Optional<String> wiki, Optional<Double> height) {
    private static final String ID = "Id";
    private static final String NAME = "Name";
    private static final String WIKI = "Wiki";
    private static final String HEIGHT = "Height";
    private static final Pattern PATTERN = Pattern.compile(STR. "(?<\{ ID }>.+?),(?<nameQ>\"?)(?<\{ NAME }>.+)"
            + STR. "(\\k<nameQ>),(?<\{ WIKI }>.*?),(?<\{ HEIGHT }>[\\.\\d]*)(?:\\s*m)?" );

    public static Rocket parse(String row) {
        Matcher matcher = PATTERN.matcher(row);

        if (!matcher.matches()) {
            throw new ParseException("Something went wrong parsing rocket: " + row);
        }

        Optional<String> parsedWiki = Optional.of(matcher.group(WIKI)).filter(not(String::isEmpty));
        Optional<Double> parsedHeight = Optional.of(matcher.group(HEIGHT)).filter(not(String::isEmpty))
                .map(Double::parseDouble);

        return new Rocket(matcher.group(ID), matcher.group(NAME), parsedWiki, parsedHeight);
    }

    public static double calculateReliability(long successfulMissionsCount, long failedMissionCount) {
        if (successfulMissionsCount + failedMissionCount == 0) {
            return 0;
        }

        double dividend = 2 * successfulMissionsCount + failedMissionCount;
        double divisor = 2 * (successfulMissionsCount + failedMissionCount);
        return dividend / divisor;
    }
}