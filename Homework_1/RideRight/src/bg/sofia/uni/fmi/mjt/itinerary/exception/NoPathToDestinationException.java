package bg.sofia.uni.fmi.mjt.itinerary.exception;

import bg.sofia.uni.fmi.mjt.itinerary.City;

public class NoPathToDestinationException extends Exception {
    public NoPathToDestinationException(City start, City destination, boolean allowTransfer) {
        super(STR. "Path from \{ start } to \{ destination } allowTransfer: \{ allowTransfer } not found" );
    }
}
