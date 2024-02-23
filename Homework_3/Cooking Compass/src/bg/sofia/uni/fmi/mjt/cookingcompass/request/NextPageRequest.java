package bg.sofia.uni.fmi.mjt.cookingcompass.request;

public class NextPageRequest implements Request {
    private final String nextPageURI;

    public NextPageRequest(String nextPageURI) {
        this.nextPageURI = nextPageURI;
    }

    @Override
    public String buildURI() {
        return nextPageURI;
    }
}
