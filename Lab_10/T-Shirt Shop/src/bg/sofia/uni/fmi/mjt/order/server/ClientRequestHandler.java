package bg.sofia.uni.fmi.mjt.order.server;

import bg.sofia.uni.fmi.mjt.order.client.Request;
import bg.sofia.uni.fmi.mjt.order.server.repository.MJTOrderRepository;
import bg.sofia.uni.fmi.mjt.order.server.repository.OrderRepository;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientRequestHandler implements Runnable {

    private final Socket socket;

    public ClientRequestHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        OrderRepository repository = new MJTOrderRepository();

        try (PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             ObjectInput in = new ObjectInputStream(socket.getInputStream())
        ) {

            Request request;
            while ((request = (Request) in.readObject()) != null) {

                if (request.type() == Request.Type.DISCONNECT) {
                    return;
                }

                Response response = switch (request.type()) {
                    case REQUEST -> {
                        String[] info = request.additionalInfo().split(";");
                        yield repository.request(info[0], info[1], info[2]);
                    }
                    case GET_ALL -> repository.getAllOrders();
                    case GET_ALL_SUCCESSFUL -> repository.getAllSuccessfulOrders();
                    case GET_ORDER -> repository.getOrderById(Integer.parseInt(request.additionalInfo()));
                    default -> throw new IllegalStateException("Unexpected value: " + request.type());
                };

                out.println(response);
            }

        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Something went wrong handling client" + socket.getInetAddress(), e);
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                throw new RuntimeException("Something went wrong closing client" + socket.getInetAddress(), e);
            }
        }

    }

}