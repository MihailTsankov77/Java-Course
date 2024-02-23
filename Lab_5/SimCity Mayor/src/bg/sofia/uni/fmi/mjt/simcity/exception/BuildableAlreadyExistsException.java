package bg.sofia.uni.fmi.mjt.simcity.exception;

public class BuildableAlreadyExistsException extends RuntimeException {
    public BuildableAlreadyExistsException(String address) {
        super("BuildableAlreadyExistsException: building exist on address: " + address);
    }
}