package bg.sofia.uni.fmi.mjt.itinerary.exception;

import bg.sofia.uni.fmi.mjt.itinerary.City;

public class CityNotKnownException extends Exception {
    public CityNotKnownException(City city) {
        super(STR. "City \{ city } not found" );
    }
}
