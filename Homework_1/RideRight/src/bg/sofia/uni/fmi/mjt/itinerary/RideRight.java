package bg.sofia.uni.fmi.mjt.itinerary;

import bg.sofia.uni.fmi.mjt.itinerary.exception.CityNotKnownException;
import bg.sofia.uni.fmi.mjt.itinerary.exception.NoPathToDestinationException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.SequencedCollection;

public class RideRight implements ItineraryPlanner {

    private final Map<City, List<Journey>> journeysByCity = new HashMap<>();

    public RideRight(List<Journey> schedule) {
        for (Journey journey : schedule) {
            City startCity = journey.from();
            City endCity = journey.to();

            journeysByCity.putIfAbsent(startCity, new ArrayList<>());
            journeysByCity.get(startCity).add(journey);

            journeysByCity.putIfAbsent(endCity, new ArrayList<>());
        }
    }

    /**
     * Find path between two nodes without transfers
     *
     * @param start       - the starting city of a journey
     * @param destination - the city to which we are searching path to
     * @return - SequencedCollection of all the journeys that lead from the start to the destination
     * @throws NoPathToDestinationException - if there is no path found
     */
    public SequencedCollection<Journey> findCheapestWayWithoutTransfer(City start, City destination)
            throws NoPathToDestinationException {
        if (start == null || destination == null) {
            throw new IllegalArgumentException("RideRight#findCheapestWayWithoutTransfer: args should not be null");
        }

        Journey cheapestWay = null;

        for (Journey journey : journeysByCity.get(start)) {
            if (journey.to().equals(destination)) {
                if (cheapestWay == null || cheapestWay.getFullPrice().compareTo(journey.getFullPrice()) > 0) {
                    cheapestWay = journey;
                }
            }
        }

        if (cheapestWay != null) {
            return List.of(cheapestWay);
        }

        throw new NoPathToDestinationException(start, destination, false);
    }

    /**
     * Find path between two nodes with transfers using A* algorithm
     *
     * @param start       - the starting city of a journey
     * @param destination - the city to which we are searching path to
     * @return - SequencedCollection of all the journeys that lead from the start to the destination
     * @throws NoPathToDestinationException - if there is no path found
     */
    public SequencedCollection<Journey> findCheapestWayWithTransfer(City start, City destination)
            throws NoPathToDestinationException {
        if (start == null || destination == null) {
            throw new IllegalArgumentException("RideRight#findCheapestWayWithTransfer: args should not be null");
        }

        Map<City, CityJourneyBook> cityJourneyBookByCity = getCityJourneyBookByCity(destination);

        CityJourneyBook lastCity = findCheapestWayFollowingAStar(start, destination, cityJourneyBookByCity);

        ArrayList<Journey> route = new ArrayList<>();

        Journey currentJourney = lastCity.getHowIGotHere();

        while (currentJourney != null) {
            route.addFirst(currentJourney);
            currentJourney = cityJourneyBookByCity.get(currentJourney.from()).getHowIGotHere();
        }

        return route;
    }

    /**
     * Helper function used to start the A* algorithm
     *
     * @param destination- the city to which we are searching path to
     * @return - Map of all the journeys that starts from a City
     */
    private Map<City, CityJourneyBook> getCityJourneyBookByCity(City destination) {
        Map<City, CityJourneyBook> cityJourneyBookByCity = new HashMap<>();

        for (Map.Entry<City, List<Journey>> entry : journeysByCity.entrySet()) {
            cityJourneyBookByCity.put(entry.getKey(),
                    new CityJourneyBook(entry.getKey(), entry.getValue(), destination));
        }

        return cityJourneyBookByCity;
    }

    /**
     * !!AntiStyleChecker TO-DO: Aks what is the best practice for the logic to be split
     *
     * @param current               - the node that is consumed currently
     * @param cityJourneyBookByCity - Map of all the journeys that starts from a City
     * @param open                  - Queue with nodes that need to be consumed
     * @param closed                - Queue with nodes that have been consumed
     */
    private static void consumeNeighboursOfANode(CityJourneyBook current, Map<City,
            CityJourneyBook> cityJourneyBookByCity, Queue<CityJourneyBook> open, Queue<CityJourneyBook> closed) {
        for (Journey journey : current.getJourneys()) {
            CityJourneyBook newRoute = cityJourneyBookByCity.get(journey.to());

            if (newRoute == null) {
                continue;
            }

            BigDecimal newTotalPrice = current.getTotalPrice().add(journey.getFullPrice());

            if (!open.contains(newRoute) && !closed.contains(newRoute)) {
                newRoute.travel(journey, newTotalPrice);

                open.add(newRoute);
            } else if (newTotalPrice.compareTo(newRoute.getTotalPrice()) < 0) {
                newRoute.travel(journey, newTotalPrice);

                if (closed.contains(newRoute)) {
                    closed.remove(newRoute);
                    open.add(newRoute);
                }
            }
        }
    }

    /**
     * When you lose your way, ask a star.
     * * Ahhh, what A* this algorithm is...
     *
     * @param start                 - the starting city of a journey
     * @param destination           - the city to which we are searching path to
     * @param cityJourneyBookByCity - Map of all the journeys that starts from a City
     * @return - the CityJourneyBook of the destination city
     * @throws NoPathToDestinationException - if there is no path found
     */
    private static CityJourneyBook findCheapestWayFollowingAStar(City start, City destination,
                                                                 Map<City, CityJourneyBook> cityJourneyBookByCity)
            throws NoPathToDestinationException {
        Queue<CityJourneyBook> open = new PriorityQueue<>();
        Queue<CityJourneyBook> closed = new PriorityQueue<>();

        CityJourneyBook startJourneyBook = cityJourneyBookByCity.get(start);
        startJourneyBook.setTotalPrice(BigDecimal.valueOf(0));
        open.add(startJourneyBook);

        while (!open.isEmpty()) {
            CityJourneyBook current = open.remove();
            closed.add(current);

            if (current.getLocation().equals(destination)) {
                return current;
            }

            consumeNeighboursOfANode(current, cityJourneyBookByCity, open, closed);
        }

        throw new NoPathToDestinationException(start, destination, true);
    }

    @Override
    public SequencedCollection<Journey> findCheapestPath(City start, City destination, boolean allowTransfer)
            throws CityNotKnownException, NoPathToDestinationException {
        if (start == null || destination == null) {
            throw new IllegalArgumentException("RideRight#findCheapestPath: args should not be null");
        }

        if (!journeysByCity.containsKey(start) || !journeysByCity.containsKey(destination)) {
            throw new CityNotKnownException(start);
        }

        if (start.equals(destination)) {
            return List.of();
        }

        if (!allowTransfer) {
            return findCheapestWayWithoutTransfer(start, destination);
        }

        return findCheapestWayWithTransfer(start, destination);
    }
}