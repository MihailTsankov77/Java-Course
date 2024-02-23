package bg.sofia.uni.fmi.mjt.cookingcompass.response;

import java.util.List;

public interface Response<T> {
    List<T> getData();
}
