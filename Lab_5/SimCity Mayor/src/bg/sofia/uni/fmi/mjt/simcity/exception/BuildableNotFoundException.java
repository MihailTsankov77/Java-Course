package bg.sofia.uni.fmi.mjt.simcity.exception;

public class BuildableNotFoundException extends RuntimeException {
    public BuildableNotFoundException(String address) {
        super("BuildableNotFoundException: building does not exist on address: " + address);
    }
}