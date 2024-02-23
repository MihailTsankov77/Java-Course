package bg.sofia.uni.fmi.mjt.space;

import bg.sofia.uni.fmi.mjt.space.algorithm.Rijndael;
import bg.sofia.uni.fmi.mjt.space.algorithm.SymmetricBlockCipher;
import bg.sofia.uni.fmi.mjt.space.exception.CipherException;
import bg.sofia.uni.fmi.mjt.space.exception.TimeFrameMismatchException;
import bg.sofia.uni.fmi.mjt.space.mission.Mission;
import bg.sofia.uni.fmi.mjt.space.mission.MissionStatus;
import bg.sofia.uni.fmi.mjt.space.rocket.Rocket;
import bg.sofia.uni.fmi.mjt.space.rocket.RocketStatus;

import javax.crypto.SecretKey;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MJTSpaceScanner implements SpaceScannerAPI {

    private final SymmetricBlockCipher cipher;
    private final Collection<Mission> missions;
    private final Collection<Rocket> rockets;

    public MJTSpaceScanner(Reader missionsReader, Reader rocketsReader, SecretKey secretKey) {
        this.cipher = new Rijndael(secretKey);
        missions = parseData(missionsReader, Mission::parse);
        rockets = parseData(rocketsReader, Rocket::parse);
    }

    private static <T> Collection<T> parseData(Reader reader, Function<String, T> parser) {
        Collection<T> collection = new ArrayList<>();

        if (reader == null) {
            return collection;
        }

        try (BufferedReader bufferedReader = new BufferedReader(reader)) {
            String row;
            bufferedReader.readLine(); // Skip the first row of the table as it is useless to us
            while ((row = bufferedReader.readLine()) != null) {
                collection.add(parser.apply(row));
            }
        } catch (IOException e) {
            throw new IllegalStateException("Something went wrong while parsing data", e);
        }

        return collection;
    }

    private static boolean getIsDateBetweenInclusive(LocalDate date, LocalDate from, LocalDate to) {
        return (date.isAfter(from) && date.isBefore(to)) || date.isEqual(from) || date.isEqual(to);
    }

    @Override
    public Collection<Mission> getAllMissions() {
        return missions;
    }

    @Override
    public Collection<Mission> getAllMissions(MissionStatus missionStatus) {
        if (missionStatus == null) {
            throw new IllegalArgumentException("Mission status is null");
        }

        return missions.stream().filter(mission -> mission.missionStatus().equals(missionStatus)).toList();
    }

    @Override
    public String getCompanyWithMostSuccessfulMissions(LocalDate from, LocalDate to) {
        if (from == null || to == null) {
            throw new IllegalArgumentException("Args should not be null");
        }

        if (to.isBefore(from)) {
            throw new TimeFrameMismatchException(to + " is before" + from);
        }

        return missions.stream()
                .filter(mission -> getIsDateBetweenInclusive(mission.date(), from, to)
                        && mission.missionStatus().equals(MissionStatus.SUCCESS))
                .collect(Collectors.groupingBy(Mission::company, Collectors.counting()))
                .entrySet().stream()
                .max(Comparator.comparingLong(Map.Entry::getValue))
                .map(Map.Entry::getKey)
                .orElse("");
    }

    @Override
    public Map<String, Collection<Mission>> getMissionsPerCountry() {
        return missions.stream().collect(Collectors.groupingBy(
                Mission::getCountry,
                Collectors.toCollection(ArrayList::new)
        ));
    }

    @Override
    public List<Mission> getTopNLeastExpensiveMissions(int n, MissionStatus missionStatus, RocketStatus rocketStatus) {
        if (n <= 0 || missionStatus == null || rocketStatus == null) {
            throw new IllegalArgumentException("Args are invalid");
        }

        return missions.stream()
                .filter(mission -> mission.missionStatus().equals(missionStatus)
                        && mission.rocketStatus().equals(rocketStatus)
                        && mission.cost().isPresent())
                .sorted(Comparator.comparingDouble(mission -> mission.cost().get()))
                .limit(n)
                .toList();
    }

    @Override
    public Map<String, String> getMostDesiredLocationForMissionsPerCompany() {
        return missions.stream()
                .collect(Collectors.groupingBy(
                        Mission::company,
                        Collectors.collectingAndThen(
                                Collectors.groupingBy(Mission::location, Collectors.counting()),
                                locations -> locations.entrySet().stream()
                                        .max(Map.Entry.comparingByValue())
                                        .map(Map.Entry::getKey)
                                        .orElse(null)
                        )
                ));
    }

    @Override
    public Map<String, String> getLocationWithMostSuccessfulMissionsPerCompany(LocalDate from, LocalDate to) {
        if (from == null || to == null) {
            throw new IllegalArgumentException("Args should not be null");
        }

        if (to.isBefore(from)) {
            throw new TimeFrameMismatchException(to + " is before" + from);
        }

        return missions.stream()
                .filter(mission -> mission.missionStatus().equals(MissionStatus.SUCCESS)
                        && getIsDateBetweenInclusive(mission.date(), from, to))
                .collect(Collectors.groupingBy(
                        Mission::company,
                        Collectors.collectingAndThen(
                                Collectors.groupingBy(Mission::location, Collectors.counting()),
                                locations -> locations.entrySet().stream()
                                        .max(Map.Entry.comparingByValue())
                                        .map(Map.Entry::getKey)
                                        .orElse(null)
                        )
                ));
    }

    @Override
    public Collection<Rocket> getAllRockets() {
        return rockets;
    }

    @Override
    public List<Rocket> getTopNTallestRockets(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("N should be >0");
        }

        return rockets.stream()
                .filter(rocket -> rocket.height().isPresent())
                .sorted(Comparator.comparing(rocket -> rocket.height().get(), Comparator.reverseOrder()))
                .limit(n)
                .toList();
    }

    @Override
    public Map<String, Optional<String>> getWikiPageForRocket() {
        return rockets.stream().collect(Collectors.toMap(Rocket::name, Rocket::wiki));
    }

    @Override
    public List<String> getWikiPagesForRocketsUsedInMostExpensiveMissions(
            int n, MissionStatus missionStatus, RocketStatus rocketStatus) {
        if (n <= 0 || missionStatus == null || rocketStatus == null) {
            throw new IllegalArgumentException("Args are invalid");
        }

        Map<String, Optional<String>> wikiPageForRocket = getWikiPageForRocket();

        return missions.stream()
                .filter(mission -> mission.missionStatus().equals(missionStatus)
                        && mission.rocketStatus().equals(rocketStatus)
                        && mission.cost().isPresent())
                .sorted(Comparator.comparing(mission -> mission.cost().get(), Comparator.reverseOrder()))
                .limit(n)
                .map(mission -> mission.detail().rocketName())
                .distinct()
                .filter(rocketName -> wikiPageForRocket.containsKey(rocketName)
                        && wikiPageForRocket.get(rocketName).isPresent())
                .map(rocketName -> wikiPageForRocket.get(rocketName).orElse(null))
                .toList();
    }

    private String getMostReliableRocket(LocalDate from, LocalDate to) {
        if (from == null || to == null) {
            throw new IllegalArgumentException("Args should not be null");
        }

        if (to.isBefore(from)) {
            throw new TimeFrameMismatchException(to + " is before" + from);
        }

        return missions.stream()
                .filter(mission -> getIsDateBetweenInclusive(mission.date(), from, to))
                .collect(Collectors.groupingBy(
                        mission -> mission.detail().rocketName(),
                        Collectors.collectingAndThen(
                                Collectors.groupingBy(Mission::missionStatus, Collectors.counting()),
                                missionStatuses -> Rocket.calculateReliability(
                                        missionStatuses.getOrDefault(MissionStatus.SUCCESS, 0L),
                                        missionStatuses.getOrDefault(MissionStatus.FAILURE, 0L)
                                                + missionStatuses.getOrDefault(MissionStatus.PRELAUNCH_FAILURE, 0L)
                                                + missionStatuses.getOrDefault(MissionStatus.PARTIAL_FAILURE, 0L))
                        )))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    @Override
    public void saveMostReliableRocket(OutputStream outputStream, LocalDate from, LocalDate to)
            throws CipherException {
        if (outputStream == null) {
            throw new IllegalArgumentException("Args should not be null");
        }

        String mostReliableRocket = getMostReliableRocket(from, to);

        if (mostReliableRocket != null) {
            cipher.encrypt(new ByteArrayInputStream(mostReliableRocket.getBytes()), outputStream);
        }
    }
}
