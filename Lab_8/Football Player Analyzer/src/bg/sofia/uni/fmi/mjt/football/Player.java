package bg.sofia.uni.fmi.mjt.football;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public record Player(String name, String fullName, LocalDate birthDate, int age, double heightCm, double weightKg,
                     List<Position> positions, String nationality, int overallRating, int potential, long valueEuro,
                     long wageEuro, Foot preferredFoot) {

    private static final int MAGIC_NUMBER_0 = 0;
    private static final int MAGIC_NUMBER_1 = 1;
    private static final int MAGIC_NUMBER_2 = 2;
    private static final int MAGIC_NUMBER_3 = 3;
    private static final int MAGIC_NUMBER_4 = 4;
    private static final int MAGIC_NUMBER_5 = 5;
    private static final int MAGIC_NUMBER_6 = 6;
    private static final int MAGIC_NUMBER_7 = 7;
    private static final int MAGIC_NUMBER_8 = 8;
    private static final int MAGIC_NUMBER_9 = 9;
    private static final int MAGIC_NUMBER_10 = 10;
    private static final int MAGIC_NUMBER_11 = 11;
    private static final int MAGIC_NUMBER_12 = 12;

    public static Player of(String line) {
        final String[] tokens = line.split(";");
        final String[] date = tokens[MAGIC_NUMBER_2].split("/");

        return new Player(tokens[MAGIC_NUMBER_0], tokens[MAGIC_NUMBER_1],
                LocalDate.of(
                        Integer.parseInt(date[MAGIC_NUMBER_2]),
                        Integer.parseInt(date[MAGIC_NUMBER_0]),
                        Integer.parseInt(date[MAGIC_NUMBER_1])
                ),
                Integer.parseInt(tokens[MAGIC_NUMBER_3]),
                Double.parseDouble(tokens[MAGIC_NUMBER_4]),
                Double.parseDouble(tokens[MAGIC_NUMBER_5]),
                Arrays.stream(tokens[MAGIC_NUMBER_6].split(","))
                        .map(Position::valueOf).toList(), tokens[MAGIC_NUMBER_7],
                Integer.parseInt(tokens[MAGIC_NUMBER_8]),
                Integer.parseInt(tokens[MAGIC_NUMBER_9]),
                Long.parseLong(tokens[MAGIC_NUMBER_10]),
                Long.parseLong(tokens[MAGIC_NUMBER_11]),
                Foot.valueOf(tokens[MAGIC_NUMBER_12].toUpperCase()));

    }

    public double getProspect() {
        return ((double) (potential + overallRating)) / age;
    }

    @Override
    public String toString() {
        return name;
    }
}