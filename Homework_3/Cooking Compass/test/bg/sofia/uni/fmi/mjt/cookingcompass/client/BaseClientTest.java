package bg.sofia.uni.fmi.mjt.cookingcompass.client;

import bg.sofia.uni.fmi.mjt.cookingcompass.client.BaseClient;
import bg.sofia.uni.fmi.mjt.cookingcompass.client.Client;
import bg.sofia.uni.fmi.mjt.cookingcompass.exceptions.BadRequestException;
import bg.sofia.uni.fmi.mjt.cookingcompass.exceptions.ConnectionException;
import bg.sofia.uni.fmi.mjt.cookingcompass.exceptions.NoMoreDataException;
import bg.sofia.uni.fmi.mjt.cookingcompass.recipe.Recipe;
import bg.sofia.uni.fmi.mjt.cookingcompass.request.Request;
import bg.sofia.uni.fmi.mjt.cookingcompass.response.PaginationResponse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.OngoingStubbing;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class BaseClientTest {

    private static HttpClient mockClientNormal;

    private static HttpClient mockClientError;

    private static HttpClient mockClientThrows;

    private static Request mockRequest;
    private static Request  mockRequestNoData;

    private static Function<String, PaginationResponse<String>> mockResponseParser;

    @BeforeAll
    public static void createMocks() throws IOException, InterruptedException {
        HttpResponse<String> mockHttpResponseNormal = mock();
        when(mockHttpResponseNormal.body()).thenReturn("""
                {"1":1}""");

        HttpResponse<String> mockHttpResponseError = mock();
        when(mockHttpResponseError.body()).thenReturn("""
                {"errors":[]}""");


        mockClientNormal = mock();
        when(mockClientNormal.send(any(), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockHttpResponseNormal);

        mockClientError = mock();
        when(mockClientError.send(any(), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockHttpResponseError);

        mockClientThrows = mock();
        when(mockClientThrows.send(any(), any(HttpResponse.BodyHandler.class)))
                .thenThrow(IOException.class);

        mockRequest = mock();
        when(mockRequest.buildURI()).thenReturn("https://blob");

        PaginationResponse<String> mockResponse = mock();
        when(mockResponse.getNextPageRequest()).thenReturn(mockRequest);
        when(mockResponse.getData()).thenReturn(List.of("blob"));

        mockRequestNoData = mock();
        when(mockRequestNoData.buildURI()).thenReturn(null);


        mockResponseParser = (String _)-> mockResponse;
    }


    @Test
    public void testBaseHttpClientCall() throws BadRequestException, ConnectionException, IOException, InterruptedException, NoMoreDataException {
        Client<String> client = BaseClient
                .builder(mockRequest, mockResponseParser)
                .seClient(mockClientNormal)
                .setNumberOfPages(3)
                .build();

        List<String> data = client.loadMore();

        verify(mockClientNormal, times(3))
                .send(any(), any(HttpResponse.BodyHandler.class));

        assertEquals(List.of("blob", "blob", "blob"), data, "test normal call");

        client.loadMore(1);

        verify(mockClientNormal, times(4))
                .send(any(), any(HttpResponse.BodyHandler.class));

        assertEquals(List.of("blob", "blob", "blob", "blob"), client.getData(), "test get data method");
    }

    @Test
    public void testHttpClientThrows() {
        Client<String> client = BaseClient
                .builder(mockRequest, mockResponseParser)
                .seClient(mockClientThrows)
                .build();

       assertThrows(ConnectionException.class, client::loadMore, "Test connections issues");
    }

    @Test
    public void testHttpClientServerError() {
        Client<String> client = BaseClient
                .builder(mockRequest, mockResponseParser)
                .seClient(mockClientError)
                .build();

        assertThrows(BadRequestException.class, client::loadMore);
    }

    @Test
    public void testHttpClientNoMoreData() {
        Client<String> client = BaseClient
                .builder(mockRequestNoData, mockResponseParser)
                .seClient(mockClientNormal)
                .build();

        assertThrows(NoMoreDataException.class, client::loadMore);
    }
}
