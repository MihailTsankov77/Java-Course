package bg.sofia.uni.fmi.mjt.space.mission;

import bg.sofia.uni.fmi.mjt.space.exception.ParseException;
import bg.sofia.uni.fmi.mjt.space.rocket.RocketStatus;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.function.Predicate.not;

public record Mission(String id, String company, String location, LocalDate date, Detail detail,
                      RocketStatus rocketStatus, Optional<Double> cost, MissionStatus missionStatus) {

    private static final String ID = "id";
    private static final String COMPANY_NAME = "CompanyName";
    private static final String LOCATION = "Location";
    private static final String DATA = "Data";
    private static final String DETAILS = "Details";
    private static final String ROCKET_STATUS = "RocketStatus";
    private static final String PRICE = "Price";
    private static final String MISSION_STATUS = "MissionStatus";
    private static final Pattern PARSE_PATTERN = Pattern.compile(STR. "(?<\{ ID }>.*?),(?<\{ COMPANY_NAME }>.*?),"
            + STR. "\"(?<\{ LOCATION }>.*?)\",\"(?<\{ DATA }>.*?)\",(?<detailsQ>\"?)(?<\{ DETAILS }>.*?)"
            + STR. "(\\k<detailsQ>),(?<\{ ROCKET_STATUS }>\\w*),(?<priceQ>\"?)(?<\{ PRICE }>[,\\.\\d]*)\\s*"
            + STR. "(\\k<priceQ>),(?<\{ MISSION_STATUS }>.*)" );
    private static final int COUNTRY_GROUP_ID = 1;
    private static final Pattern COUNTRY_PATTERN = Pattern.compile(".*,\\s*(.+)");

    public static Mission parse(String row) {
        Matcher matcher = PARSE_PATTERN.matcher(row);

        if (!matcher.matches()) {
            throw new ParseException("Something went wrong parsing mission: " + row);
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM dd, yyyy", Locale.ENGLISH);
        LocalDate parsedDate = LocalDate.parse(matcher.group(DATA), formatter);

        Optional<Double> parsedPrice = Optional.of(matcher.group(PRICE))
                .filter(not(String::isEmpty)).map((x) -> x.replaceAll(",", "")).map(Double::parseDouble);

        return new Mission(matcher.group(ID), matcher.group(COMPANY_NAME), matcher.group(LOCATION),
                parsedDate, Detail.parse(matcher.group(DETAILS)), RocketStatus.fromValue(matcher.group(ROCKET_STATUS)),
                parsedPrice, MissionStatus.fromValue(matcher.group(MISSION_STATUS)));
    }

    public String getCountry() {
        Matcher matcher = COUNTRY_PATTERN.matcher(location);

        if (!matcher.matches()) {
            throw new ParseException("Something went wrong parsing country: " + location);
        }

        return matcher.group(COUNTRY_GROUP_ID);
    }

}