package bg.sofia.uni.fmi.mjt.order.server.repository;

import bg.sofia.uni.fmi.mjt.order.server.Response;
import bg.sofia.uni.fmi.mjt.order.server.order.Order;

import java.util.ArrayList;
import java.util.List;

public class MJTOrderRepository implements OrderRepository {

    private final List<Order> orders = new ArrayList<>();

    @Override
    public Response request(String size, String color, String destination) {
        if (size == null || color == null || destination == null) {
            throw new IllegalArgumentException("args are null");
        }

        orders.add(Order.parse(orders.size() + 1, size, color, destination));

        if (orders.getLast().id() == -1) {
            return Response.decline(orders.getLast().getErrorMessage());
        }

        return Response.create(orders.getLast().id());
    }

    @Override
    public Response getOrderById(int id) {
        if (id < 0) {
            throw new IllegalArgumentException("args are -1");
        }

        return orders.stream()
                .filter(o -> o.id() == id).findFirst()
                .map(value -> Response.ok(List.of(value)))
                .orElse(Response.notFound(id));
    }

    @Override
    public Response getAllOrders() {
        return Response.ok(orders);
    }

    @Override
    public Response getAllSuccessfulOrders() {
        return Response.ok(orders.stream().filter(order -> order.id() != -1).toList());
    }
}
