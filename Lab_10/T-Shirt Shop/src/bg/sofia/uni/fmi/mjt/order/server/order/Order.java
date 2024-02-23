package bg.sofia.uni.fmi.mjt.order.server.order;

import bg.sofia.uni.fmi.mjt.order.server.destination.Destination;
import bg.sofia.uni.fmi.mjt.order.server.tshirt.Color;
import bg.sofia.uni.fmi.mjt.order.server.tshirt.Size;
import bg.sofia.uni.fmi.mjt.order.server.tshirt.TShirt;

import java.util.stream.Stream;

public record Order(int id, TShirt tShirt, Destination destination) {

    public static Order parse(int id, String size, String color, String destination) {
        Size parsedSize = Size.from(size);
        Color parsedColor = Color.from(color);
        Destination parsedDestination = Destination.from(destination);

        if (Stream.of(parsedSize, parsedColor, parsedDestination)
                .anyMatch(anEnum -> anEnum.name().equals("UNKNOWN"))) {
            id = -1;
        }


        return new Order(id, new TShirt(parsedSize, parsedColor), parsedDestination);
    }

    public String getErrorMessage() {
        String error = "invalid:";
        boolean hasErrors = false;

        if (tShirt.size() == Size.UNKNOWN) {
            error += "size";
            hasErrors = true;
        }

        if (tShirt.color() == Color.UNKNOWN) {
            if (hasErrors) {
                error += ",";
            }

            error += "color";
            hasErrors = true;
        }

        if (destination == Destination.UNKNOWN) {
            if (hasErrors) {
                error += ",";
            }

            error += "destination";
        }

        return error;
    }
}