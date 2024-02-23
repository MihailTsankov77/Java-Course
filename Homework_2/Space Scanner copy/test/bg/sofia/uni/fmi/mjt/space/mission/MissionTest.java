package bg.sofia.uni.fmi.mjt.space.mission;

import bg.sofia.uni.fmi.mjt.space.exception.ParseException;
import bg.sofia.uni.fmi.mjt.space.rocket.RocketStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MissionTest {

    @Test
    public void testParseBasic() {
        String row = "0,SpaceX,\"LC-39A, Kennedy Space Center, Florida, USA\",\"Fri Aug 07, 2020\",Falcon 9 Block 5 | Starlink V1 L9 & BlackSky,StatusActive,\"50.0 \",Success";
        Mission expectedMission = new Mission("0", "SpaceX", "LC-39A, Kennedy Space Center, Florida, USA", LocalDate.parse("Fri Aug 07, 2020", DateTimeFormatter.ofPattern("EEE MMM dd, yyyy", Locale.ENGLISH)),
                new Detail("Falcon 9 Block 5", "Starlink V1 L9 & BlackSky"), RocketStatus.STATUS_ACTIVE, Optional.of(50.0), MissionStatus.SUCCESS);
        assertEquals(expectedMission, Mission.parse(row),
                "Test mission parse for a row: " + row);
    }

    @Test
    public void testParseWithoutPrice() {
        String row = "0,SpaceX,\"LC-39A, Kennedy Space Center, Florida, USA\",\"Fri Aug 07, 2020\",Falcon 9 Block 5 | Starlink V1 L9 & BlackSky,StatusActive,,Success";
        Mission expectedMission = new Mission("0", "SpaceX", "LC-39A, Kennedy Space Center, Florida, USA", LocalDate.parse("Fri Aug 07, 2020", DateTimeFormatter.ofPattern("EEE MMM dd, yyyy", Locale.ENGLISH)),
                new Detail("Falcon 9 Block 5", "Starlink V1 L9 & BlackSky"), RocketStatus.STATUS_ACTIVE, Optional.empty(), MissionStatus.SUCCESS);
        assertEquals(expectedMission, Mission.parse(row),
                "Test mission parse without Price, row: " + row);
    }

    @Test
    public void testParseWithBigPrice() {
        String row = "0,SpaceX,\"LC-39A, Kennedy Space Center, Florida, USA\",\"Fri Aug 07, 2020\",Falcon 9 Block 5 | Starlink V1 L9 & BlackSky,StatusActive,\"5,000.00\",Success";
        Mission expectedMission = new Mission("0", "SpaceX", "LC-39A, Kennedy Space Center, Florida, USA", LocalDate.parse("Fri Aug 07, 2020", DateTimeFormatter.ofPattern("EEE MMM dd, yyyy", Locale.ENGLISH)),
                new Detail("Falcon 9 Block 5", "Starlink V1 L9 & BlackSky"), RocketStatus.STATUS_ACTIVE, Optional.of(5000.0), MissionStatus.SUCCESS);
        assertEquals(expectedMission, Mission.parse(row),
                "Test mission parse with price value with , -> row: " + row);
    }

    @Test
    public void testParseWithWrongValue() {
        assertThrows(ParseException.class, () -> Mission.parse(""),
                "Test if a parse exception is throw when there is something wrong with the data");
    }

    @Test
    public void testGetCountry() {
        String country = "BLOB xae32e3-2341dsa das";
        Mission mission = new Mission("0", "SpaceX", STR. "LC-39A, Kennedy Space Center, Florida, \{ country }" , LocalDate.parse("Fri Aug 07, 2020", DateTimeFormatter.ofPattern("EEE MMM dd, yyyy", Locale.ENGLISH)),
                new Detail("Falcon 9 Block 5", "Starlink V1 L9 & BlackSky"), RocketStatus.STATUS_ACTIVE, Optional.of(5000.0), MissionStatus.SUCCESS);
        assertEquals(country, mission.getCountry(),
                "The country is the last element of the location");
    }

    @Test
    public void testGetLocationWithWrongValue() {
        Mission mission = new Mission("0", "SpaceX", "", LocalDate.parse("Fri Aug 07, 2020", DateTimeFormatter.ofPattern("EEE MMM dd, yyyy", Locale.ENGLISH)),
                new Detail("Falcon 9 Block 5", "Starlink V1 L9 & BlackSky"), RocketStatus.STATUS_ACTIVE, Optional.of(5000.0), MissionStatus.SUCCESS);
        assertThrows(ParseException.class, mission::getCountry,
                "Test if a parse exception is throw when there is something wrong with the location");
    }

}
