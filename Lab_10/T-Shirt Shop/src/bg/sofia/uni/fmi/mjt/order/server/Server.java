package bg.sofia.uni.fmi.mjt.order.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private static final int SERVER_PORT = 6969;

    public static void main(String[] args) {
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
             ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {

            while (true) {
                executor.execute(new ClientRequestHandler(serverSocket.accept()));
            }
        } catch (IOException e) {
            throw new RuntimeException("There is a problem with the server socket", e);
        }
    }


}
