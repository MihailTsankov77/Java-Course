package bg.sofia.uni.fmi.mjt.itinerary;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class CityJourneyBook implements Comparable<CityJourneyBook> {

    private static final BigDecimal AVERAGE_KM_COST = BigDecimal.valueOf(20);

    private final City location;
    private final List<Journey> journeys;
    private final BigDecimal priceToTarget;
    private Journey howIGotHere = null;
    private BigDecimal totalWeight = BigDecimal.valueOf(-1);
    private BigDecimal totalPrice = BigDecimal.valueOf(0);

    public CityJourneyBook(City location, List<Journey> journeys, City destination) {
        this.location = location;
        this.journeys = Collections.unmodifiableList(journeys);

        double distanceToTarget = Location.getDistanceBetween(location.location(), destination.location());
        priceToTarget = AVERAGE_KM_COST.multiply(BigDecimal.valueOf(distanceToTarget));
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    /**
     * Set the total price of the Node and calculates and updates the total weight
     * @param totalPrice - the combined value of all parents of the current Node
     */
    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
        this.totalWeight = totalPrice.add(priceToTarget);
    }

    public List<Journey> getJourneys() {
        return journeys;
    }

    public City getLocation() {
        return location;
    }

    /**
     * This function travels through the nodes in an A* algorithm
     * @param journey - the edge of the current node, from which the algorithm have come,
     *                used to trace back the path in the graph
     * @param totalPrice - the combined value of all parents of the current Node
     */
    public void travel(Journey journey, BigDecimal totalPrice) {
        setTotalPrice(totalPrice);
        howIGotHere = journey;
    }

    /**
     * @return - journey that represents the edge of the graph from which we have come to this node
     * used to trace back the path to the beginning
     */
    public Journey getHowIGotHere() {
        return howIGotHere;
    }

    @Override
    public int compareTo(CityJourneyBook other) {
        int result = totalWeight.compareTo(other.totalWeight);

        if (result == 0) {
            return location.name().compareTo(other.location.name());
        }

        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CityJourneyBook that = (CityJourneyBook) o;
        return Objects.equals(location, that.location); // The City is unique
    }

    @Override
    public int hashCode() {
        return Objects.hash(location);
    }
}

