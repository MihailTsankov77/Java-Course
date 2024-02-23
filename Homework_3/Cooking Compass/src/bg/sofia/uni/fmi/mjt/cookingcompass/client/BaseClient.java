package bg.sofia.uni.fmi.mjt.cookingcompass.client;

import bg.sofia.uni.fmi.mjt.cookingcompass.exceptions.BadRequestException;
import bg.sofia.uni.fmi.mjt.cookingcompass.exceptions.ConnectionException;
import bg.sofia.uni.fmi.mjt.cookingcompass.exceptions.NoMoreDataException;
import bg.sofia.uni.fmi.mjt.cookingcompass.request.Request;
import bg.sofia.uni.fmi.mjt.cookingcompass.response.PaginationResponse;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class BaseClient<T> implements Client<T> {

    private final HttpClient client;

    private final List<T> recipes = new ArrayList<>();
    private final Function<String, PaginationResponse<T>> responseParser;

    private final int defaultNumberOfPages;
    private Request currentRequest;

    protected BaseClient(ClientBaseBuilder<T> builder) {
        defaultNumberOfPages = builder.numberOfPages;
        currentRequest = builder.baseRequest;
        responseParser = builder.responseParser;
        client = builder.client;
    }

    public static <T> ClientBaseBuilder<T> builder(Request baseRequest,
                                                   Function<String, PaginationResponse<T>> responseParser) {
        return new ClientBaseBuilder<>(baseRequest, responseParser);
    }

    private void checkForErrors(String json) throws BadRequestException {
        JsonElement errors = JsonParser.parseString(json).getAsJsonObject().get("errors");

        if (errors != null) {
            throw new BadRequestException("Bad request triggered the following errors: " + errors.getAsJsonArray());
        }
    }

    private List<T> executeRequest() throws ConnectionException, BadRequestException, NoMoreDataException {
        if (currentRequest.buildURI() == null) {
            throw new NoMoreDataException("There is no more data to be fetched form this request");
        }

        URI uri = URI.create(currentRequest.buildURI());
        HttpRequest request = HttpRequest.newBuilder().uri(uri).build();

        String json;

        try {
            json = client.send(request, HttpResponse.BodyHandlers.ofString()).body();
        } catch (IOException | InterruptedException e) {
            throw new ConnectionException("Something went wrong trying to retrieve data", e);
        }

        checkForErrors(json);

        PaginationResponse<T> response = responseParser.apply(json);

        recipes.addAll(response.getData());
        currentRequest = response.getNextPageRequest();

        return response.getData();
    }

    @Override
    public List<T> loadMore() throws ConnectionException, BadRequestException, NoMoreDataException {
        return loadMore(defaultNumberOfPages);
    }

    @Override
    public List<T> loadMore(int numberOfPages) throws ConnectionException, BadRequestException, NoMoreDataException {
        List<T> currentData = new ArrayList<>();
        for (int i = 0; i < numberOfPages; i++) {
            currentData.addAll(executeRequest());
        }

        return currentData;
    }

    @Override
    public List<T> getData() {
        return Collections.unmodifiableList(recipes);
    }

    public static class ClientBaseBuilder<T> {
        private final Request baseRequest;
        private final Function<String, PaginationResponse<T>> responseParser;
        private int numberOfPages = 2;

        private HttpClient client;

        public ClientBaseBuilder(Request baseRequest, Function<String, PaginationResponse<T>> responseParser) {
            this.baseRequest = baseRequest;
            this.responseParser = responseParser;
        }

        public ClientBaseBuilder<T> setNumberOfPages(int numberOfPages) {
            this.numberOfPages = numberOfPages;
            return this;
        }

        public ClientBaseBuilder<T> seClient(HttpClient client) {
            this.client = client;
            return this;
        }

        public BaseClient<T> build() {
            if (client == null) {
                client = HttpClient.newBuilder().build();
            }

            return new BaseClient<T>(this);
        }
    }
}
