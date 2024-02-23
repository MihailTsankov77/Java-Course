package bg.sofia.uni.fmi.mjt.space.rocket;

import bg.sofia.uni.fmi.mjt.space.exception.ParseException;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RocketTest {

    @Test
    public void testParseBasic() {
        String row = "0,Tsyklon-3,https://en.wikipedia.org/wiki/Tsyklon-3,39.0 m";
        Rocket expectedRocket = new Rocket("0", "Tsyklon-3", Optional.of("https://en.wikipedia.org/wiki/Tsyklon-3"), Optional.of(39.0));
        assertEquals(expectedRocket, Rocket.parse(row),
                "Test rocket parse for a row: " + row);
    }

    @Test
    public void testParseWithoutHeight() {
        String row = "0,Tsyklon-3,https://en.wikipedia.org/wiki/Tsyklon-3,";
        Rocket expectedRocket = new Rocket("0", "Tsyklon-3", Optional.of("https://en.wikipedia.org/wiki/Tsyklon-3"), Optional.empty());
        assertEquals(expectedRocket, Rocket.parse(row),
                "Test rocket parse for a row: " + row);
    }

    @Test
    public void testParseWithoutWiki() {
        String row = "0,Tsyklon-3,,39.0 m";
        Rocket expectedRocket = new Rocket("0", "Tsyklon-3", Optional.empty(), Optional.of(39.0));
        assertEquals(expectedRocket, Rocket.parse(row),
                "Test rocket parse for a row: " + row);
    }

    @Test
    public void testParseWithoutWikiAndHeight() {
        String row = "0,Tsyklon-3,,";
        Rocket expectedRocket = new Rocket("0", "Tsyklon-3", Optional.empty(), Optional.empty());
        assertEquals(expectedRocket, Rocket.parse(row),
                "Test rocket parse for a row: " + row);
    }

    @Test
    public void testParseWithWrongValue() {
        String row = ",,,";
        assertThrows(ParseException.class, () -> Rocket.parse(row),
                "Test if a parse exception is throw when there is something wrong with the data");
    }

    @Test
    public void testCalculateReliabilityBasic() {
        long successfulMissionsCount = 5;
        long failedMissionCount = 5;

        assertEquals(0.75, Rocket.calculateReliability(successfulMissionsCount, failedMissionCount),
                "The formula is (2 * (count of successful mission) + (count of failedMissions)) / (2 * (count of all missions))");
    }

    @Test
    public void testCalculateReliabilityWithNoMissions() {
        long successfulMissionsCount = 0;
        long failedMissionCount = 0;

        assertEquals(0, Rocket.calculateReliability(successfulMissionsCount, failedMissionCount),
                "If there are no missions the reliability is 0");
    }
}
