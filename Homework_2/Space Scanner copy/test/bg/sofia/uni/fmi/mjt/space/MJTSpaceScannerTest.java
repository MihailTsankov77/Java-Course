package bg.sofia.uni.fmi.mjt.space;

import bg.sofia.uni.fmi.mjt.space.algorithm.Rijndael;
import bg.sofia.uni.fmi.mjt.space.algorithm.SymmetricBlockCipher;
import bg.sofia.uni.fmi.mjt.space.exception.CipherException;
import bg.sofia.uni.fmi.mjt.space.exception.TimeFrameMismatchException;
import bg.sofia.uni.fmi.mjt.space.mission.Mission;
import bg.sofia.uni.fmi.mjt.space.mission.MissionStatus;
import bg.sofia.uni.fmi.mjt.space.rocket.Rocket;
import bg.sofia.uni.fmi.mjt.space.rocket.RocketStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MJTSpaceScannerTest {
    private static final String ENCRYPTION_ALGORITHM = "AES";
    private static final String BASIC_RAW_MISSIONS = """
            0,SpaceX,"LC-39A, Kennedy Space Center, Florida, USA","Fri Aug 07, 2020",Falcon 9 Block 5 | Starlink V1 L9 & BlackSky,StatusActive,"50.0 ",Success
            1,CASC,"Site 9401 (SLS-2), Jiuquan Satellite Launch Center, China","Thu Aug 06, 2020",Long March 2D | Gaofen-9 04 & Q-SAT,StatusActive,"29.75 ",Failure
            2,SpaceX,"Pad A, Boca Chica, Texas, USA","Tue Aug 04, 2020",Starship Prototype | 150 Meter Hop,StatusActive,,Failure
            3,Roscosmos,"Site 200/39, Baikonur Cosmodrome, Kazakhstan","Thu Jul 30, 2020",Proton-M/Briz-M | Ekspress-80 & Ekspress-103,StatusActive,"65.0 ",Failure
            4,ULA,"SLC-41, Cape Canaveral AFS, Florida, USA","Thu Jul 30, 2020",Atlas V 541 | Perseverance,StatusActive,"145.0 ",Success
            5,CASC,"LC-9, Taiyuan Satellite Launch Center, China","Mon Jul 20, 2020","Long March 4B | Ziyuan-3 03, Apocalypse-10 & NJU-HKU 1",StatusActive,"64.68 ",Success
            6,Roscosmos,"Site 31/6, Baikonur Cosmodrome, Kazakhstan","Tue Jun 23, 2020",Soyuz 2.1a | Progress MS-15,StatusActive,"48.5 ",Success
            7,CASC,"LC-101, Wenchang Satellite Launch Center, China","Thu Jul 23, 2020",Long March 5 | Tianwen-1,StatusActive,,Success
            8,SpaceX,"SLC-40, Cape Canaveral AFS, Florida, USA","Mon Jul 20, 2020",Falcon 9 Block 5 | ANASIS-II,StatusActive,"50.0 ",Success
            9,JAXA,"LA-Y1, Tanegashima Space Center, Japan","Sun Jul 19, 2020",H-IIA 202 | Hope Mars Mission,StatusActive,"90.0 ",Prelaunch Failure
            """;
    private static final String BASIC_RAW_ROCKETS = """
            0,ASLV,https://en.wikipedia.org/wiki/Augmented_Satellite_Launch_Vehicle,24.0 m
            1,Athena I,https://en.wikipedia.org/wiki/Athena_I,18.9 m
            2,Athena II,https://en.wikipedia.org/wiki/Athena_II,28.2 m
            3,Atlas-D Able,https://en.wikipedia.org/wiki/Atlas-Able,35.0 m
            4,Atlas-D Mercury,https://en.wikipedia.org/wiki/Atlas_LV-3B,28.7 m
            5,Atlas-D OV1,https://en.wikipedia.org/wiki/SM-65D_Atlas,
            6,Atlas-E/F Agena D,https://en.wikipedia.org/wiki/Atlas-Agena,
            7,Atlas-E/F Altair,https://en.wikipedia.org/wiki/Atlas_E/F,
            8,Atlas-E/F Burner,,
            9,Atlas-E/F MSD,https://en.wikipedia.org/wiki/Atlas_E/F#Atlas_E/F-MSD,""";
    private static final String TEST_TOP_N_LEAST_EXPENSIVE_MISSIONS_RAW_DATA = """
            0,SpaceX,"LC-39A, Kennedy Space Center, Florida, USA","Fri Aug 07, 2020",Falcon 9 Block 5 | Starlink V1 L9 & BlackSky,StatusActive,"50.0 ",Success
            1,CASC,"Site 9401 (SLS-2), Jiuquan Satellite Launch Center, China","Thu Aug 06, 2020",Long March 2D | Gaofen-9 04 & Q-SAT,StatusActive,"29.75 ",Failure
            2,SpaceX,"Pad A, Boca Chica, Texas, USA","Tue Aug 04, 2020",Starship Prototype | 150 Meter Hop,StatusActive,,Failure
            3,Roscosmos,"Site 200/39, Baikonur Cosmodrome, Kazakhstan","Thu Jul 30, 2020",Proton-M/Briz-M | Ekspress-80 & Ekspress-103,StatusActive,"65.0 ",Failure
            4,ULA,"SLC-41, Cape Canaveral AFS, Florida, USA","Thu Jul 30, 2020",Atlas V 541 | Perseverance,StatusActive,"145.0 ",Success
            5,CASC,"LC-9, Taiyuan Satellite Launch Center, China","Mon Jul 20, 2020","Long March 4B | Ziyuan-3 03, Apocalypse-10 & NJU-HKU 1",StatusActive,"64.68 ",Failure
            6,Roscosmos,"Site 31/6, Baikonur Cosmodrome, Kazakhstan","Tue Jun 23, 2020",Soyuz 2.1a | Progress MS-15,StatusActive,"1.5 ",Success
            7,CASC,"LC-101, Wenchang Satellite Launch Center, China","Thu Jul 23, 2020",Long March 5 | Tianwen-1,StatusActive,"2.0 ",Success
            8,SpaceX,"SLC-40, Cape Canaveral AFS, Florida, USA","Mon Jul 20, 2020",Falcon 9 Block 5 | ANASIS-II,StatusActive,"51.0 ",Success
            9,JAXA,"LA-Y1, Tanegashima Space Center, Japan","Sun Jul 19, 2020",H-IIA 202 | Hope Mars Mission,StatusRetired,"90.0 ",Success
            """;
    private static SpaceScannerAPI scanner;
    private static List<Mission> missions;
    private static List<Rocket> rockets;
    private static SecretKey secretKey = null;

    static void generateResources(String missionsRaw, String rocketRaw) {
        Reader missionReader = null;
        if (missionsRaw != null) {
            missions = Arrays.stream(missionsRaw.split("\n")).map(Mission::parse).toList();
            missionReader = new StringReader("Unnamed: 0,Company Name,Location,Datum,Detail,Status Rocket,\" Rocket\",Status Mission\n" + missionsRaw);
        }

        Reader rocketReader = null;
        if (rocketRaw != null) {
            rockets = Arrays.stream(rocketRaw.split("\n")).map(Rocket::parse).toList();
            rocketReader = new StringReader("Name,Wiki,Rocket Height\n" + rocketRaw);
        }

        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(ENCRYPTION_ALGORITHM);
            secretKey = keyGenerator.generateKey();
        } catch (Exception e) {
            throw new RuntimeException("Something went wrong generating key", e);
        }

        scanner = new MJTSpaceScanner(missionReader, rocketReader, secretKey);
    }

    private static <T> boolean areTwoCollectionsEqual(Collection<T> first, Collection<T> second) {
        return first.size() == second.size() && first.containsAll(second) && second.containsAll(first);
    }

    private static <K, V> boolean areTwoMapsWithCollectionsEqual(Map<K, Collection<V>> first, Map<K, Collection<V>> second) {
        if (first.size() != second.size()) {
            return false;
        }

        return first.entrySet().stream()
                .allMatch(e -> areTwoCollectionsEqual(e.getValue(), second.get(e.getKey())));
    }

    private static void generateResourcesForGetWikiPagesForRocketsUsedInMostExpensiveMissions() {
        generateResources("""
                0,SpaceX,"LC-39A, Kennedy Space Center, Florida, USA","Fri Aug 07, 2020",Falcon 9 Block 5 | Starlink V1 L9 & BlackSky,StatusActive,,Failure
                1,CASC,"Site 9401 (SLS-2), Jiuquan Satellite Launch Center, Blob","Thu Aug 06, 2020",ASLV | Gaofen-9 04 & Q-SAT,StatusActive,"999.9 ",Success
                2,SpaceX,"Pad A, Boca Chica, Texas, USA","Tue Aug 04, 2020",Mercury | 150 Meter Hop,StatusActive,,Success
                3,Roscosmos,"Site 200/39, Baikonur Cosmodrome, Kazakhstan","Thu Jul 30, 2020",Athena II | Ekspress-80 & Ekspress-103,StatusActive,"888.8 ",Success
                4,ULA,"SLC-41, Cape Canaveral AFS, Florida, USA","Thu Jul 30, 2020",Atlas V 541 | Perseverance,StatusActive,"145.0 ",Success
                5,CASC,"LC-9, Taiyuan Satellite Launch Center, China","Mon Jul 20, 2020","Long March 4B | Ziyuan-3 03, Apocalypse-10 & NJU-HKU 1",StatusActive,"64.68 ",Failure
                6,Roscosmos,"Site 200/39, Baikonur Cosmodrome, Kazakhstan","Thu Jul 30, 2020",Atlas-D Able | Progress MS-15,StatusActive,"1.5 ",Success
                7,CASC,"LC-101, Wenchang Satellite Launch Center, China","Thu Jul 23, 2020",Long March 5 | Tianwen-1,StatusActive,"2.0 ",Partial Failure
                8,SpaceX,"SLC-40, Cape Canaveral AFS, Florida, USA","Mon Jul 20, 2020",Athena I | ANASIS-II,StatusActive,"777.7 ",Failure
                9,Roscosmos,"LA-Y1, Tanegashima Space Center, Japan","Sun Jul 19, 2020",Atlas-D Mercury| Hope Mars Mission,StatusActive,"90.0 ",Success
                10,Roscosmos,"LA-Y1, Tanegashima Space Center, Japan","Sun Jul 19, 2020",Blob| Hope Mars Mission,StatusRetired,"91.0 ",Success
                """, """
                0,ASLV,https://en.wikipedia.org/wiki/Augmented_Satellite_Launch_Vehicle,1.0 m
                1,Athena I,https://en.wikipedia.org/wiki/Athena_I,0.0 m
                2,Athena II,,3.2 m
                3,Atlas-D Able,https://en.wikipedia.org/wiki/Atlas-Able,4.0 m
                4,Atlas-D Mercury,HII,28.7 m
                5,Mercury,Blob,28.7 m
                """);
    }

    @AfterEach
    void clearResources() {
        scanner = null;
        missions = null;
        rockets = null;
        secretKey = null;
    }

    @Test
    void testParseWithNull() {
        SpaceScannerAPI nullScanner = new MJTSpaceScanner(null, null, null);
        assertTrue(nullScanner.getAllMissions().isEmpty() && nullScanner.getAllRockets().isEmpty(),
                "Check if the null values are handled with empty collection");
    }

    @Test
    void testGetAllMission() {
        generateResources(BASIC_RAW_MISSIONS, null);
        Collection<Mission> allMissions = scanner.getAllMissions();
        assertTrue(areTwoCollectionsEqual(allMissions, missions),
                "Check if all missions are parsed and saved correctly");
    }

    @Test
    void testGetAllMissionEmpty() {
        Collection<Mission> allMissions = new MJTSpaceScanner(new StringReader(""), null, null).getAllMissions();
        assertTrue(allMissions.isEmpty(),
                "Check if empty reader is passed the collection is empty too");
    }

    @Test
    void testGetAllMissionWithStatusFilter() {
        generateResources(
                """
                        0,SpaceX,"LC-39A, Kennedy Space Center, Florida, USA","Fri Aug 07, 2020",Falcon 9 Block 5 | Starlink V1 L9 & BlackSky,StatusActive,"50.0 ",Success
                        1,CASC,"Site 9401 (SLS-2), Jiuquan Satellite Launch Center, China","Thu Aug 06, 2020",Long March 2D | Gaofen-9 04 & Q-SAT,StatusActive,"29.75 ",Failure
                        2,SpaceX,"Pad A, Boca Chica, Texas, USA","Tue Aug 04, 2020",Starship Prototype | 150 Meter Hop,StatusActive,,Failure
                        3,Roscosmos,"Site 200/39, Baikonur Cosmodrome, Kazakhstan","Thu Jul 30, 2020",Proton-M/Briz-M | Ekspress-80 & Ekspress-103,StatusActive,"65.0 ",Failure
                        4,ULA,"SLC-41, Cape Canaveral AFS, Florida, USA","Thu Jul 30, 2020",Atlas V 541 | Perseverance,StatusActive,"145.0 ",Success
                        5,CASC,"LC-9, Taiyuan Satellite Launch Center, China","Mon Jul 20, 2020","Long March 4B | Ziyuan-3 03, Apocalypse-10 & NJU-HKU 1",StatusActive,"64.68 ",Success
                        6,Roscosmos,"Site 31/6, Baikonur Cosmodrome, Kazakhstan","Tue Jun 23, 2020",Soyuz 2.1a | Progress MS-15,StatusActive,"48.5 ",Success
                        7,CASC,"LC-101, Wenchang Satellite Launch Center, China","Thu Jul 23, 2020",Long March 5 | Tianwen-1,StatusActive,,Success
                        8,SpaceX,"SLC-40, Cape Canaveral AFS, Florida, USA","Mon Jul 20, 2020",Falcon 9 Block 5 | ANASIS-II,StatusActive,"50.0 ",Success
                        9,JAXA,"LA-Y1, Tanegashima Space Center, Japan","Sun Jul 19, 2020",H-IIA 202 | Hope Mars Mission,StatusActive,"90.0 ",Prelaunch Failure
                        10,Northrop,"LP-0B, Wallops Flight Facility, Virginia, USA","Wed Jul 15, 2020",Minotaur IV | NROL-129,StatusActive,"46.0 ",Success
                        11,CASC,"LC-3, Xichang Satellite Launch Center, China","Thu Jul 09, 2020",Long March 3B/E | Apstar-6D,StatusActive,"29.15 ",Success
                        12,IAI,"Pad 1, Palmachim Airbase, Israel","Mon Jul 06, 2020",Shavit-2 | Ofek-16,StatusActive,,Success
                        13,CASC,"Site 9401 (SLS-2), Jiuquan Satellite Launch Center, China","Sat Jul 04, 2020",Long March 2D | Shiyan-6 02,StatusActive,"29.75 ",Success""", null);
        Collection<Mission> expectedResult = List.of(missions.get(1), missions.get(2), missions.get(3));
        Collection<Mission> actualResult = scanner.getAllMissions(MissionStatus.FAILURE);

        assertTrue(areTwoCollectionsEqual(expectedResult, actualResult),
                "Test filtering all missions based on a Mission status");
    }

    @Test
    void testGetAllMissionWithStatusFilterEmpty() {
        generateResources(
                """
                        0,SpaceX,"LC-39A, Kennedy Space Center, Florida, USA","Fri Aug 07, 2020",Falcon 9 Block 5 | Starlink V1 L9 & BlackSky,StatusActive,"50.0 ",Success
                        1,CASC,"Site 9401 (SLS-2), Jiuquan Satellite Launch Center, China","Thu Aug 06, 2020",Long March 2D | Gaofen-9 04 & Q-SAT,StatusActive,"29.75 ",Failure
                        2,SpaceX,"Pad A, Boca Chica, Texas, USA","Tue Aug 04, 2020",Starship Prototype | 150 Meter Hop,StatusActive,,Failure
                        3,Roscosmos,"Site 200/39, Baikonur Cosmodrome, Kazakhstan","Thu Jul 30, 2020",Proton-M/Briz-M | Ekspress-80 & Ekspress-103,StatusActive,"65.0 ",Failure
                        4,ULA,"SLC-41, Cape Canaveral AFS, Florida, USA","Thu Jul 30, 2020",Atlas V 541 | Perseverance,StatusActive,"145.0 ",Success
                        5,CASC,"LC-9, Taiyuan Satellite Launch Center, China","Mon Jul 20, 2020","Long March 4B | Ziyuan-3 03, Apocalypse-10 & NJU-HKU 1",StatusActive,"64.68 ",Success
                        6,Roscosmos,"Site 31/6, Baikonur Cosmodrome, Kazakhstan","Tue Jun 23, 2020",Soyuz 2.1a | Progress MS-15,StatusActive,"48.5 ",Success
                        7,CASC,"LC-101, Wenchang Satellite Launch Center, China","Thu Jul 23, 2020",Long March 5 | Tianwen-1,StatusActive,,Success
                        8,SpaceX,"SLC-40, Cape Canaveral AFS, Florida, USA","Mon Jul 20, 2020",Falcon 9 Block 5 | ANASIS-II,StatusActive,"50.0 ",Success
                        9,JAXA,"LA-Y1, Tanegashima Space Center, Japan","Sun Jul 19, 2020",H-IIA 202 | Hope Mars Mission,StatusActive,"90.0 ",Prelaunch Failure
                        10,Northrop,"LP-0B, Wallops Flight Facility, Virginia, USA","Wed Jul 15, 2020",Minotaur IV | NROL-129,StatusActive,"46.0 ",Success
                        11,CASC,"LC-3, Xichang Satellite Launch Center, China","Thu Jul 09, 2020",Long March 3B/E | Apstar-6D,StatusActive,"29.15 ",Success
                        12,IAI,"Pad 1, Palmachim Airbase, Israel","Mon Jul 06, 2020",Shavit-2 | Ofek-16,StatusActive,,Success
                        13,CASC,"Site 9401 (SLS-2), Jiuquan Satellite Launch Center, China","Sat Jul 04, 2020",Long March 2D | Shiyan-6 02,StatusActive,"29.75 ",Success""", null);
        Collection<Mission> actualResult = scanner.getAllMissions(MissionStatus.PARTIAL_FAILURE);
        assertTrue(actualResult.isEmpty(),
                "Test filtering all missions based on a Mission status with status that does not exist in the data");
    }

    @Test
    void testGetAllMissionWithStatusFilterWithStatusNull() {
        generateResources(BASIC_RAW_MISSIONS, null);
        assertThrows(IllegalArgumentException.class,
                () -> scanner.getAllMissions(null),
                "Test null as arg");
    }

    @Test
    void testGetCompanyWithMostSuccessfulMissionsWithNullArgs() {
        generateResources(BASIC_RAW_MISSIONS, null);
        assertThrows(IllegalArgumentException.class,
                () -> scanner.getCompanyWithMostSuccessfulMissions(null, LocalDate.now()),
                "Test from is null");

        assertThrows(IllegalArgumentException.class,
                () -> scanner.getCompanyWithMostSuccessfulMissions(LocalDate.now(), null),
                "Test to is null");
    }

    @Test
    void testGetCompanyWithMostSuccessfulMissionsWithFromAfterTo() {
        generateResources(BASIC_RAW_MISSIONS, null);
        assertThrows(TimeFrameMismatchException.class,
                () -> scanner.getCompanyWithMostSuccessfulMissions(LocalDate.of(2022, 12, 12), LocalDate.of(2022, 12, 11)),
                "Test to is before from");
    }

    @Test
    void testGetCompanyWithMostSuccessfulMissions() {
        generateResources("""
                0,Arianespace,"ELS, Guiana Space Centre, French Guiana, France","Wed Dec 18, 2019","Soyuz ST-A/Fregat-M | CSG-1, CHEOPS & Others",StatusActive,,Success
                1,Roscosmos,"SLC-40, Cape Canaveral AFS, Florida, USA","Tue Dec 17, 2019",Falcon 9 Block 5 | JCSAT-18 / Kacific-1,StatusActive,"50.0 ",Success
                2,Roscosmos,"LC-3, Xichang Satellite Launch Center, China","Mon Dec 16, 2019",Long March 3B/YZ-1 | BeiDou-3 M19 & M20,StatusActive,,Failure
                3,Roscosmos,"Blue Origin Launch Site, West Texas, Texas, USA","Wed Dec 11, 2019",New Shepard | NS-12,StatusActive,,Success
                4,Roscosmos,"First Launch Pad, Satish Dhawan Space Centre, India","Wed Dec 11, 2019",PSLV-QL | RISAT 2BR1,StatusActive,"21.0 ",Partial Failure
                5,VKS RF,"Site 43/3, Plesetsk Cosmodrome, Russia","Wed Dec 11, 2019",Soyuz 2.1b/Fregat | Cosmos 2544,StatusActive,"48.5 ",Success
                6,ExPace,"Site 9401 (SLS-2), Jiuquan Satellite Launch Center, China","Sat Dec 07, 2019",Kuaizhou 1A | HEAD-2A/B / SpaceTY 16/17 / Tianqi 4A/B,StatusActive,,Success
                7,ExPace,"Taiyuan Satellite Launch Center, China","Sat Dec 07, 2019",Kuaizhou 1A | Jilin-1 Gaofen-02B,StatusActive,,Success
                8,VKS RF,"Site 31/6, Baikonur Cosmodrome, Kazakhstan","Fri Dec 06, 2019",Soyuz 2.1a | Progress MS-13 (74P),StatusActive,"48.5 ",Success""", null);
        LocalDate from = LocalDate.of(2019, 12, 7);
        LocalDate to = LocalDate.of(2019, 12, 16);
        String expectedResult = "ExPace";
        String actualResult = scanner.getCompanyWithMostSuccessfulMissions(from, to);

        assertEquals(expectedResult, actualResult,
                "Test if getCompanyWithMostSuccessfulMissions returns the correct answer for a timeframe inclusive");
    }

    @Test
    void testGetCompanyWithMostSuccessfulMissionsNoMissions() {
        generateResources("""
                0,Arianespace,"ELS, Guiana Space Centre, French Guiana, France","Wed Dec 18, 2019","Soyuz ST-A/Fregat-M | CSG-1, CHEOPS & Others",StatusActive,,Success
                1,Roscosmos,"SLC-40, Cape Canaveral AFS, Florida, USA","Tue Dec 17, 2019",Falcon 9 Block 5 | JCSAT-18 / Kacific-1,StatusActive,"50.0 ",Success
                2,Roscosmos,"LC-3, Xichang Satellite Launch Center, China","Mon Dec 16, 2019",Long March 3B/YZ-1 | BeiDou-3 M19 & M20,StatusActive,,Failure
                3,Roscosmos,"Blue Origin Launch Site, West Texas, Texas, USA","Wed Dec 11, 2019",New Shepard | NS-12,StatusActive,,Success
                4,Roscosmos,"First Launch Pad, Satish Dhawan Space Centre, India","Wed Dec 11, 2019",PSLV-QL | RISAT 2BR1,StatusActive,"21.0 ",Partial Failure
                5,VKS RF,"Site 43/3, Plesetsk Cosmodrome, Russia","Wed Dec 11, 2019",Soyuz 2.1b/Fregat | Cosmos 2544,StatusActive,"48.5 ",Success
                6,ExPace,"Site 9401 (SLS-2), Jiuquan Satellite Launch Center, China","Sat Dec 07, 2019",Kuaizhou 1A | HEAD-2A/B / SpaceTY 16/17 / Tianqi 4A/B,StatusActive,,Success
                7,ExPace,"Taiyuan Satellite Launch Center, China","Sat Dec 07, 2019",Kuaizhou 1A | Jilin-1 Gaofen-02B,StatusActive,,Success
                8,VKS RF,"Site 31/6, Baikonur Cosmodrome, Kazakhstan","Fri Dec 06, 2019",Soyuz 2.1a | Progress MS-13 (74P),StatusActive,"48.5 ",Success""", null);
        LocalDate from = LocalDate.of(2022, 12, 7);
        LocalDate to = LocalDate.of(2022, 12, 16);
        String expectedResult = "";
        String actualResult = scanner.getCompanyWithMostSuccessfulMissions(from, to);

        assertEquals(expectedResult, actualResult,
                "Test if getCompanyWithMostSuccessfulMissions if there are no missions in the given timeframe");
    }

    @Test
    void testGetMissionsPerCountry() {
        generateResources("""
                0,SpaceX,"LC-39A, Kennedy Space Center, Florida, USA","Fri Aug 07, 2020",Falcon 9 Block 5 | Starlink V1 L9 & BlackSky,StatusActive,"50.0 ",Success
                1,CASC,"Site 9401 (SLS-2), Jiuquan Satellite Launch Center, China","Thu Aug 06, 2020",Long March 2D | Gaofen-9 04 & Q-SAT,StatusActive,"29.75 ",Failure
                2,SpaceX,"Pad A, Boca Chica, Texas, USA","Tue Aug 04, 2020",Starship Prototype | 150 Meter Hop,StatusActive,,Failure
                3,Roscosmos,"Site 200/39, Baikonur Cosmodrome, Kazakhstan","Thu Jul 30, 2020",Proton-M/Briz-M | Ekspress-80 & Ekspress-103,StatusActive,"65.0 ",Failure
                4,ULA,"SLC-41, Cape Canaveral AFS, Florida, USA","Thu Jul 30, 2020",Atlas V 541 | Perseverance,StatusActive,"145.0 ",Success
                5,CASC,"LC-9, Taiyuan Satellite Launch Center, China","Mon Jul 20, 2020","Long March 4B | Ziyuan-3 03, Apocalypse-10 & NJU-HKU 1",StatusActive,"64.68 ",Success
                6,Roscosmos,"Site 31/6, Baikonur Cosmodrome, Kazakhstan","Tue Jun 23, 2020",Soyuz 2.1a | Progress MS-15,StatusActive,"48.5 ",Success
                7,CASC,"LC-101, Wenchang Satellite Launch Center, China","Thu Jul 23, 2020",Long March 5 | Tianwen-1,StatusActive,,Success
                8,SpaceX,"SLC-40, Cape Canaveral AFS, Florida, USA","Mon Jul 20, 2020",Falcon 9 Block 5 | ANASIS-II,StatusActive,"50.0 ",Success
                9,JAXA,"LA-Y1, Tanegashima Space Center, Japan","Sun Jul 19, 2020",H-IIA 202 | Hope Mars Mission,StatusActive,"90.0 ",Prelaunch Failure
                """, null
        );

        Map<String, Collection<Mission>> expectedResult = Map.of(
                "USA", List.of(missions.get(0), missions.get(2), missions.get(4), missions.get(8)),
                "China", List.of(missions.get(1), missions.get(5), missions.get(7)),
                "Kazakhstan", List.of(missions.get(3), missions.get(6)),
                "Japan", List.of(missions.get(9))
        );

        Map<String, Collection<Mission>> actualResult = scanner.getMissionsPerCountry();

        assertTrue(areTwoMapsWithCollectionsEqual(expectedResult, actualResult),
                "Test if getMissionsPerCountry returns the right map");
    }

    @Test
    void testGetTopNLeastExpensiveMissionsWithInvalidArguments() {
        generateResources(BASIC_RAW_MISSIONS, null);
        assertThrows(IllegalArgumentException.class,
                () -> scanner.getTopNLeastExpensiveMissions(-1, MissionStatus.SUCCESS, RocketStatus.STATUS_ACTIVE),
                "Test when n<0");

        assertThrows(IllegalArgumentException.class,
                () -> scanner.getTopNLeastExpensiveMissions(0, MissionStatus.SUCCESS, RocketStatus.STATUS_ACTIVE),
                "Test when n=0");

        assertThrows(IllegalArgumentException.class,
                () -> scanner.getTopNLeastExpensiveMissions(2, null, RocketStatus.STATUS_ACTIVE),
                "Test when mission status is null");

        assertThrows(IllegalArgumentException.class,
                () -> scanner.getTopNLeastExpensiveMissions(2, MissionStatus.SUCCESS, null),
                "Test when rocket status is null");
    }

    @Test
    void testGetTopNLeastExpensiveMissions() {
        generateResources(TEST_TOP_N_LEAST_EXPENSIVE_MISSIONS_RAW_DATA, null);

        List<Mission> expectedResult = List.of(missions.get(6), missions.get(7), missions.get(0), missions.get(8), missions.get(4));
        List<Mission> actualResult = scanner.getTopNLeastExpensiveMissions(5, MissionStatus.SUCCESS, RocketStatus.STATUS_ACTIVE);

        assertEquals(expectedResult, actualResult,
                "Test if getTopNLeastExpensiveMissions returns the missions in right order and filter based on statuses");
    }

    @Test
    void testGetTopNLeastExpensiveMissionsLessThanLimit() {
        generateResources(TEST_TOP_N_LEAST_EXPENSIVE_MISSIONS_RAW_DATA, null);

        List<Mission> expectedResult = List.of(missions.get(6), missions.get(7), missions.get(0), missions.get(8), missions.get(4));
        List<Mission> actualResult = scanner.getTopNLeastExpensiveMissions(15, MissionStatus.SUCCESS, RocketStatus.STATUS_ACTIVE);

        assertEquals(expectedResult, actualResult,
                "Test if getTopNLeastExpensiveMissions returns all when the limit is more than the rockets");
    }

    @Test
    void testGetTopNLeastExpensiveMissionsMoreThanLimit() {
        generateResources(TEST_TOP_N_LEAST_EXPENSIVE_MISSIONS_RAW_DATA, null);

        List<Mission> expectedResult = List.of(missions.get(6), missions.get(7));
        List<Mission> actualResult = scanner.getTopNLeastExpensiveMissions(2, MissionStatus.SUCCESS, RocketStatus.STATUS_ACTIVE);

        assertEquals(expectedResult, actualResult,
                "Test if getTopNLeastExpensiveMissions returns only n rockets");
    }

    @Test
    void testGetTopNLeastExpensiveMissionsWhenThereIsNoPrice() {
        generateResources("""
                0,SpaceX,"LC-39A, Kennedy Space Center, Florida, USA","Fri Aug 07, 2020",Falcon 9 Block 5 | Starlink V1 L9 & BlackSky,StatusActive,,Success
                1,CASC,"Site 9401 (SLS-2), Jiuquan Satellite Launch Center, China","Thu Aug 06, 2020",Long March 2D | Gaofen-9 04 & Q-SAT,StatusActive,"29.75 ",Failure
                2,SpaceX,"Pad A, Boca Chica, Texas, USA","Tue Aug 04, 2020",Starship Prototype | 150 Meter Hop,StatusActive,,Failure
                3,Roscosmos,"Site 200/39, Baikonur Cosmodrome, Kazakhstan","Thu Jul 30, 2020",Proton-M/Briz-M | Ekspress-80 & Ekspress-103,StatusActive,"65.0 ",Failure
                4,ULA,"SLC-41, Cape Canaveral AFS, Florida, USA","Thu Jul 30, 2020",Atlas V 541 | Perseverance,StatusActive,"145.0 ",Success
                5,CASC,"LC-9, Taiyuan Satellite Launch Center, China","Mon Jul 20, 2020","Long March 4B | Ziyuan-3 03, Apocalypse-10 & NJU-HKU 1",StatusActive,"64.68 ",Failure
                6,Roscosmos,"Site 31/6, Baikonur Cosmodrome, Kazakhstan","Tue Jun 23, 2020",Soyuz 2.1a | Progress MS-15,StatusActive,"1.5 ",Success
                7,CASC,"LC-101, Wenchang Satellite Launch Center, China","Thu Jul 23, 2020",Long March 5 | Tianwen-1,StatusActive,"2.0 ",Success
                8,SpaceX,"SLC-40, Cape Canaveral AFS, Florida, USA","Mon Jul 20, 2020",Falcon 9 Block 5 | ANASIS-II,StatusActive,"51.0 ",Success
                9,JAXA,"LA-Y1, Tanegashima Space Center, Japan","Sun Jul 19, 2020",H-IIA 202 | Hope Mars Mission,StatusRetired,"90.0 ",Success
                """, null);

        List<Mission> expectedResult = List.of(missions.get(6), missions.get(7), missions.get(8), missions.get(4));
        List<Mission> actualResult = scanner.getTopNLeastExpensiveMissions(5, MissionStatus.SUCCESS, RocketStatus.STATUS_ACTIVE);

        assertEquals(expectedResult, actualResult,
                "Test if getTopNLeastExpensiveMissions returns only n rockets");
    }

    @Test
    void getMostDesiredLocationForMissionsPerCompany() {
        generateResources("""
                0,SpaceX,"LC-39A, Kennedy Space Center, Florida, USA","Fri Aug 07, 2020",Falcon 9 Block 5 | Starlink V1 L9 & BlackSky,StatusActive,,Success
                1,CASC,"Site 9401 (SLS-2), Jiuquan Satellite Launch Center, Blob","Thu Aug 06, 2020",Long March 2D | Gaofen-9 04 & Q-SAT,StatusActive,"29.75 ",Failure
                2,SpaceX,"Pad A, Boca Chica, Texas, USA","Tue Aug 04, 2020",Starship Prototype | 150 Meter Hop,StatusActive,,Failure
                3,Roscosmos,"Site 200/39, Baikonur Cosmodrome, Kazakhstan","Thu Jul 30, 2020",Proton-M/Briz-M | Ekspress-80 & Ekspress-103,StatusActive,"65.0 ",Failure
                4,ULA,"SLC-41, Cape Canaveral AFS, Florida, USA","Thu Jul 30, 2020",Atlas V 541 | Perseverance,StatusActive,"145.0 ",Success
                5,CASC,"LC-9, Taiyuan Satellite Launch Center, China","Mon Jul 20, 2020","Long March 4B | Ziyuan-3 03, Apocalypse-10 & NJU-HKU 1",StatusActive,"64.68 ",Failure
                6,Roscosmos,"Site 200/39, Baikonur Cosmodrome, Kazakhstan","Tue Jun 23, 2020",Soyuz 2.1a | Progress MS-15,StatusActive,"1.5 ",Success
                7,CASC,"LC-101, Wenchang Satellite Launch Center, China","Thu Jul 23, 2020",Long March 5 | Tianwen-1,StatusActive,"2.0 ",Success
                8,SpaceX,"SLC-40, Cape Canaveral AFS, Florida, USA","Mon Jul 20, 2020",Falcon 9 Block 5 | ANASIS-II,StatusActive,"51.0 ",Success
                9,Roscosmos,"LA-Y1, Tanegashima Space Center, Japan","Sun Jul 19, 2020",H-IIA 202 | Hope Mars Mission,StatusRetired,"90.0 ",Success
                """, null);

        Map<String, String> expectedResult = Map.of(
                "ULA", "SLC-41, Cape Canaveral AFS, Florida, USA",
                "SpaceX", "SLC-40, Cape Canaveral AFS, Florida, USA",
                "Roscosmos", "Site 200/39, Baikonur Cosmodrome, Kazakhstan",
                "CASC", "LC-9, Taiyuan Satellite Launch Center, China"
        );

        Map<String, String> actualResult = scanner.getMostDesiredLocationForMissionsPerCompany();

        assertEquals(expectedResult, actualResult,
                "Test if it gets the country with the most missions there for each company");
    }

    @Test
    void TestGetLocationWithMostSuccessfulMissionsPerCompanyWithInvalidArguments() {
        generateResources(BASIC_RAW_MISSIONS, null);
        assertThrows(IllegalArgumentException.class,
                () -> scanner.getLocationWithMostSuccessfulMissionsPerCompany(null, LocalDate.now()),
                "Test from is null");

        assertThrows(IllegalArgumentException.class,
                () -> scanner.getLocationWithMostSuccessfulMissionsPerCompany(LocalDate.now(), null),
                "Test to is null");
    }

    @Test
    void TestGetLocationWithMostSuccessfulMissionsPerCompanyWithFromAfterTo() {
        generateResources(BASIC_RAW_MISSIONS, null);
        assertThrows(TimeFrameMismatchException.class,
                () -> scanner.getLocationWithMostSuccessfulMissionsPerCompany(LocalDate.of(2022, 12, 12), LocalDate.of(2022, 12, 11)),
                "Test to is before from");
    }

    @Test
    void TestGetLocationWithMostSuccessfulMissionsPerCompanyWithBigTimePeriod() {
        generateResources("""
                0,SpaceX,"LC-39A, Kennedy Space Center, Florida, USA","Fri Aug 07, 2020",Falcon 9 Block 5 | Starlink V1 L9 & BlackSky,StatusActive,,Failure
                1,CASC,"Site 9401 (SLS-2), Jiuquan Satellite Launch Center, Blob","Thu Aug 06, 2020",Long March 2D | Gaofen-9 04 & Q-SAT,StatusActive,"29.75 ",Success
                2,SpaceX,"Pad A, Boca Chica, Texas, USA","Tue Aug 04, 2020",Starship Prototype | 150 Meter Hop,StatusActive,,Failure
                3,Roscosmos,"Site 200/39, Baikonur Cosmodrome, Kazakhstan","Thu Jul 30, 2020",Proton-M/Briz-M | Ekspress-80 & Ekspress-103,StatusActive,"65.0 ",Success
                4,ULA,"SLC-41, Cape Canaveral AFS, Florida, USA","Thu Jul 30, 2020",Atlas V 541 | Perseverance,StatusActive,"145.0 ",Success
                5,CASC,"LC-9, Taiyuan Satellite Launch Center, China","Mon Jul 20, 2020","Long March 4B | Ziyuan-3 03, Apocalypse-10 & NJU-HKU 1",StatusActive,"64.68 ",Failure
                6,Roscosmos,"Site 200/39, Baikonur Cosmodrome, Kazakhstan","Thu Jul 30, 2020",Soyuz 2.1a | Progress MS-15,StatusActive,"1.5 ",Success
                7,CASC,"LC-101, Wenchang Satellite Launch Center, China","Thu Jul 23, 2020",Long March 5 | Tianwen-1,StatusActive,"2.0 ",Partial Failure
                8,SpaceX,"SLC-40, Cape Canaveral AFS, Florida, USA","Mon Jul 20, 2020",Falcon 9 Block 5 | ANASIS-II,StatusActive,"51.0 ",Failure
                9,Roscosmos,"LA-Y1, Tanegashima Space Center, Japan","Sun Jul 19, 2020",H-IIA 202 | Hope Mars Mission,StatusRetired,"90.0 ",Success
                """, null);

        Map<String, String> expectedResult = Map.of(
                "CASC", "Site 9401 (SLS-2), Jiuquan Satellite Launch Center, Blob",
                "Roscosmos", "Site 200/39, Baikonur Cosmodrome, Kazakhstan",
                "ULA", "SLC-41, Cape Canaveral AFS, Florida, USA"
        );
        Map<String, String> actualResult = scanner.getLocationWithMostSuccessfulMissionsPerCompany(LocalDate.of(2020, 7, 19), LocalDate.of(2020, 8, 7));

        assertEquals(expectedResult, actualResult,
                "Test if getLocationWithMostSuccessfulMissionsPerCompany get the most successful location");
    }

    @Test
    void TestGetLocationWithMostSuccessfulMissionsPerCompanyWithSmallTimePeriod() {
        generateResources("""
                0,SpaceX,"LC-39A, Kennedy Space Center, Florida, USA","Fri Aug 07, 2020",Falcon 9 Block 5 | Starlink V1 L9 & BlackSky,StatusActive,,Failure
                1,CASC,"Site 9401 (SLS-2), Jiuquan Satellite Launch Center, Blob","Thu Aug 06, 2020",Long March 2D | Gaofen-9 04 & Q-SAT,StatusActive,"29.75 ",Success
                2,SpaceX,"Pad A, Boca Chica, Texas, USA","Tue Aug 04, 2020",Starship Prototype | 150 Meter Hop,StatusActive,,Failure
                3,Roscosmos,"Site 200/39, Baikonur Cosmodrome, Kazakhstan","Fri Aug 07, 2020",Proton-M/Briz-M | Ekspress-80 & Ekspress-103,StatusActive,"65.0 ",Success
                4,ULA,"SLC-41, Cape Canaveral AFS, Florida, USA","Sun Jul 19, 2020",Atlas V 541 | Perseverance,StatusActive,"145.0 ",Success
                5,CASC,"LC-9, Taiyuan Satellite Launch Center, China","Mon Jul 20, 2020","Long March 4B | Ziyuan-3 03, Apocalypse-10 & NJU-HKU 1",StatusActive,"64.68 ",Failure
                6,Roscosmos,"Site 200/39, Baikonur Cosmodrome, Kazakhstan","Fri Aug 07, 2020",Soyuz 2.1a | Progress MS-15,StatusActive,"1.5 ",Success
                7,CASC,"LC-101, Wenchang Satellite Launch Center, China","Thu Jul 23, 2020",Long March 5 | Tianwen-1,StatusActive,"2.0 ",Partial Failure
                8,SpaceX,"SLC-40, Cape Canaveral AFS, Florida, USA","Mon Jul 20, 2020",Falcon 9 Block 5 | ANASIS-II,StatusActive,"51.0 ",Failure
                9,Roscosmos,"LA-Y1, Tanegashima Space Center, Japan","Thu Jul 30, 2020",H-IIA 202 | Hope Mars Mission,StatusRetired,"90.0 ",Success
                """, null);

        Map<String, String> expectedResult = Map.of(
                "CASC", "Site 9401 (SLS-2), Jiuquan Satellite Launch Center, Blob",
                "Roscosmos", "LA-Y1, Tanegashima Space Center, Japan"
        );
        Map<String, String> actualResult = scanner.getLocationWithMostSuccessfulMissionsPerCompany(LocalDate.of(2020, 7, 20), LocalDate.of(2020, 8, 6));

        assertEquals(expectedResult, actualResult,
                "Test if getLocationWithMostSuccessfulMissionsPerCompany filters based on from and to");
    }

    @Test
    void testGetAllRockets() {
        generateResources(null, BASIC_RAW_ROCKETS);
        assertEquals(rockets, scanner.getAllRockets(),
                "Test if all rockets are parsed and saved correctly");
    }

    @Test
    void testGetTopNTallestRocketsInvalidArgument() {
        generateResources(null, BASIC_RAW_ROCKETS);
        assertThrows(IllegalArgumentException.class,
                () -> scanner.getTopNTallestRockets(-1),
                "Test when n is negative");
        assertThrows(IllegalArgumentException.class,
                () -> scanner.getTopNTallestRockets(0),
                "Test when n is zero");
    }

    @Test
    void testGetTopNTallestRocketsWithBigN() {
        generateResources(null, """
                0,ASLV,https://en.wikipedia.org/wiki/Augmented_Satellite_Launch_Vehicle,1.0 m
                1,Athena I,https://en.wikipedia.org/wiki/Athena_I,0.0 m
                2,Athena II,https://en.wikipedia.org/wiki/Athena_II,3.2 m
                3,Atlas-D Able,https://en.wikipedia.org/wiki/Atlas-Able,4.0 m
                4,Atlas-D Mercury,https://en.wikipedia.org/wiki/Atlas_LV-3B,28.7 m
                5,Atlas-D OV1,https://en.wikipedia.org/wiki/SM-65D_Atlas,
                6,Atlas-E/F Agena D,https://en.wikipedia.org/wiki/Atlas-Agena,
                7,Atlas-E/F Altair,https://en.wikipedia.org/wiki/Atlas_E/F,
                8,Atlas-E/F Burner,,
                9,Atlas-E/F MSD,https://en.wikipedia.org/wiki/Atlas_E/F#Atlas_E/F-MSD,""");

        List<Rocket> expectedResult = List.of(rockets.get(4), rockets.get(3), rockets.get(2), rockets.get(0), rockets.get(1));
        List<Rocket> actualResult = scanner.getTopNTallestRockets(10);

        assertEquals(expectedResult, actualResult,
                "Test if the method sorts correctly and filters empty");
    }

    @Test
    void testGetTopNTallestRocketsWithSmallN() {
        generateResources(null, """
                0,ASLV,https://en.wikipedia.org/wiki/Augmented_Satellite_Launch_Vehicle,1.0 m
                1,Athena I,https://en.wikipedia.org/wiki/Athena_I,0.0 m
                2,Athena II,https://en.wikipedia.org/wiki/Athena_II,3.2 m
                3,Atlas-D Able,https://en.wikipedia.org/wiki/Atlas-Able,4.0 m
                4,Atlas-D Mercury,https://en.wikipedia.org/wiki/Atlas_LV-3B,28.7 m
                5,Atlas-D OV1,https://en.wikipedia.org/wiki/SM-65D_Atlas,
                6,Atlas-E/F Agena D,https://en.wikipedia.org/wiki/Atlas-Agena,
                7,Atlas-E/F Altair,https://en.wikipedia.org/wiki/Atlas_E/F,
                8,Atlas-E/F Burner,,
                9,Atlas-E/F MSD,https://en.wikipedia.org/wiki/Atlas_E/F#Atlas_E/F-MSD,""");

        List<Rocket> expectedResult = List.of(rockets.get(4), rockets.get(3), rockets.get(2));
        List<Rocket> actualResult = scanner.getTopNTallestRockets(3);

        assertEquals(expectedResult, actualResult,
                "Test if the method sorts correctly and filters empty");
    }

    @Test
    void testGetWikiPageForRocket() {
        generateResources(null, """
                0,ASLV,https://en.wikipedia.org/wiki/Augmented_Satellite_Launch_Vehicle,1.0 m
                1,Athena I,https://en.wikipedia.org/wiki/Athena_I,0.0 m
                2,Athena II,,3.2 m
                3,Atlas-D Able,https://en.wikipedia.org/wiki/Atlas-Able,4.0 m
                4,Atlas-D Mercury,,28.7 m
                """);

        Map<String, Optional<String>> expectedResult = Map.of(
                "ASLV", Optional.of("https://en.wikipedia.org/wiki/Augmented_Satellite_Launch_Vehicle"),
                "Athena I", Optional.of("https://en.wikipedia.org/wiki/Athena_I"),
                "Athena II", Optional.empty(),
                "Atlas-D Able", Optional.of("https://en.wikipedia.org/wiki/Atlas-Able"),
                "Atlas-D Mercury", Optional.empty()
        );
        Map<String, Optional<String>> actualResult = scanner.getWikiPageForRocket();

        assertEquals(expectedResult, actualResult,
                "Test if the map is correct");
    }

    @Test
    void testGetWikiPagesForRocketsUsedInMostExpensiveMissionsWithInvalidArguments() {
        generateResources(BASIC_RAW_MISSIONS, BASIC_RAW_ROCKETS);
        assertThrows(IllegalArgumentException.class,
                () -> scanner.getWikiPagesForRocketsUsedInMostExpensiveMissions(-1, MissionStatus.SUCCESS, RocketStatus.STATUS_ACTIVE),
                "Test when n<0");

        assertThrows(IllegalArgumentException.class,
                () -> scanner.getWikiPagesForRocketsUsedInMostExpensiveMissions(0, MissionStatus.SUCCESS, RocketStatus.STATUS_ACTIVE),
                "Test when n=0");

        assertThrows(IllegalArgumentException.class,
                () -> scanner.getWikiPagesForRocketsUsedInMostExpensiveMissions(2, null, RocketStatus.STATUS_ACTIVE),
                "Test when mission status is null");

        assertThrows(IllegalArgumentException.class,
                () -> scanner.getWikiPagesForRocketsUsedInMostExpensiveMissions(2, MissionStatus.SUCCESS, null),
                "Test when rocket status is null");
    }

    @Test
    void testGetWikiPagesForRocketsUsedInMostExpensiveMissions() {
        generateResourcesForGetWikiPagesForRocketsUsedInMostExpensiveMissions();

        List<String> expectedResult = List.of(rockets.get(0).wiki().get(), rockets.get(4).wiki().get(), rockets.get(3).wiki().get());
        List<String> actualResult = scanner.getWikiPagesForRocketsUsedInMostExpensiveMissions(10, MissionStatus.SUCCESS, RocketStatus.STATUS_ACTIVE);

        assertEquals(expectedResult, actualResult,
                "Check if there is a filter based on mission status and rocket status also the filter empty cost and wiki");
    }

    @Test
    void testGetWikiPagesForRocketsUsedInMostExpensiveMissionsWithSmallN() {
        generateResourcesForGetWikiPagesForRocketsUsedInMostExpensiveMissions();

        List<String> expectedResult = List.of(rockets.get(0).wiki().get());
        List<String> actualResult = scanner.getWikiPagesForRocketsUsedInMostExpensiveMissions(2, MissionStatus.SUCCESS, RocketStatus.STATUS_ACTIVE);

        assertEquals(expectedResult, actualResult,
                "Test if we limit the answers to n and filter no wiki happens after limit");
    }

    @Test
    void testSaveMostReliableRocketWithInvalidArguments() {
        generateResources(BASIC_RAW_MISSIONS, BASIC_RAW_ROCKETS);
        assertThrows(IllegalArgumentException.class,
                () -> scanner.saveMostReliableRocket(null, LocalDate.now(), LocalDate.now()),
                "Test outputStream is null");

        assertThrows(IllegalArgumentException.class,
                () -> scanner.saveMostReliableRocket(new ByteArrayOutputStream(), null, LocalDate.now()),
                "Test from is null");

        assertThrows(IllegalArgumentException.class,
                () -> scanner.saveMostReliableRocket(new ByteArrayOutputStream(), LocalDate.now(), null),
                "Test to is null");
    }

    @Test
    void testSaveMostReliableRocketsWithFromAfterTo() {
        generateResources(BASIC_RAW_MISSIONS, BASIC_RAW_ROCKETS);
        assertThrows(TimeFrameMismatchException.class,
                () -> scanner.saveMostReliableRocket(new ByteArrayOutputStream(), LocalDate.of(2022, 12, 12), LocalDate.of(2022, 12, 11)),
                "Test to is before from");
    }

    @Test
    void testSaveMostReliableRockets() throws CipherException {
        generateResources("""
                0,SpaceX,"LC-39A, Kennedy Space Center, Florida, USA","Fri Aug 07, 2020",1 | Starlink V1 L9 & BlackSky,StatusActive,,Failure
                1,CASC,"Site 9401 (SLS-2), Jiuquan Satellite Launch Center, Blob","Thu Aug 06, 2020",2 | Gaofen-9 04 & Q-SAT,StatusActive,"29.75 ",Success
                2,SpaceX,"Pad A, Boca Chica, Texas, USA","Tue Aug 04, 2020",1 | 150 Meter Hop,StatusActive,,Failure
                3,Roscosmos,"Site 200/39, Baikonur Cosmodrome, Kazakhstan","Thu Jul 30, 2020",2 | Ekspress-80 & Ekspress-103,StatusActive,"65.0 ",Success
                4,ULA,"SLC-41, Cape Canaveral AFS, Florida, USA","Thu Jul 30, 2020",1 | Perseverance,StatusActive,"145.0 ",Success
                5,CASC,"LC-9, Taiyuan Satellite Launch Center, China","Mon Jul 20, 2020","2 | Ziyuan-3 03, Apocalypse-10 & NJU-HKU 1",StatusActive,"64.68 ",Failure
                6,Roscosmos,"Site 200/39, Baikonur Cosmodrome, Kazakhstan","Thu Jul 30, 2020",1 | Progress MS-15,StatusActive,"1.5 ",Success
                7,CASC,"LC-101, Wenchang Satellite Launch Center, China","Thu Jul 23, 2020",2 | Tianwen-1,StatusActive,"2.0 ",Partial Failure
                8,SpaceX,"SLC-40, Cape Canaveral AFS, Florida, USA","Mon Jul 20, 2020",1 | ANASIS-II,StatusActive,"51.0 ",Failure
                9,Roscosmos,"LA-Y1, Tanegashima Space Center, Japan","Sun Jul 19, 2020",1 | Hope Mars Mission,StatusRetired,"90.0 ",Success
                """, null);

        String expectedResult = "1";

        ByteArrayOutputStream encryptOutput = new ByteArrayOutputStream();
        OutputStream decryptedOutput = new ByteArrayOutputStream();

        scanner.saveMostReliableRocket(encryptOutput, LocalDate.of(2020, 7, 19), LocalDate.of(2020, 8, 7));

        SymmetricBlockCipher cipher = new Rijndael(secretKey);
        cipher.decrypt(new ByteArrayInputStream(encryptOutput.toByteArray()), decryptedOutput);

        assertEquals(expectedResult, decryptedOutput.toString(),
                "Test if the calculation and sort work");
    }
}
