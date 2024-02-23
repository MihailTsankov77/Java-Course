package bg.sofia.uni.fmi.mjt.order.client;

import java.io.Serializable;
import java.util.Arrays;
import java.util.stream.Collectors;

public record Request(bg.sofia.uni.fmi.mjt.order.client.Request.Type type,
                      String additionalInfo) implements Serializable {
    public static Request parse(String command) {
        switch (command.split(" ")[0]) {
            case "request": {
                return new Request(Type.REQUEST,
                        Arrays.stream(command.split(" ")).filter(c -> c.contains("="))
                                .map(c -> c.split("=")[1])
                                .collect(Collectors.joining(";")));
            }
            case "get": {
                switch (command.split(" ")[1]) {
                    case "all":
                        return new Request(Type.GET_ALL, null);
                    case "all-successful":
                        return new Request(Type.GET_ALL_SUCCESSFUL, null);
                    case "my-order":
                        return new Request(Type.GET_ORDER, command.split(" ")[2].split("=")[1]);
                }
            }
            case "disconnect":
                return new Request(Type.DISCONNECT, null);

            default:
                return null;
        }
    }

    public enum Type {
        REQUEST,
        GET_ALL,
        GET_ALL_SUCCESSFUL,
        GET_ORDER,
        DISCONNECT
    }

}