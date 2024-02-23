package bg.sofia.uni.fmi.mjt.cookingcompass.response;

import bg.sofia.uni.fmi.mjt.cookingcompass.request.Request;

public interface PaginationResponse<T> extends Response<T> {
    boolean hasNext();

    Request getNextPageRequest();
}
