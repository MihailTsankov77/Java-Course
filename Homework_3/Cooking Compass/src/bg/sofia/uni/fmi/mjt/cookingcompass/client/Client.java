package bg.sofia.uni.fmi.mjt.cookingcompass.client;

import bg.sofia.uni.fmi.mjt.cookingcompass.exceptions.BadRequestException;
import bg.sofia.uni.fmi.mjt.cookingcompass.exceptions.ConnectionException;
import bg.sofia.uni.fmi.mjt.cookingcompass.exceptions.NoMoreDataException;

import java.util.List;

public interface Client<T> {

    List<T> getData();

    List<T> loadMore() throws ConnectionException, BadRequestException, NoMoreDataException;

    List<T> loadMore(int numberOfPages) throws ConnectionException, BadRequestException, NoMoreDataException;
}
