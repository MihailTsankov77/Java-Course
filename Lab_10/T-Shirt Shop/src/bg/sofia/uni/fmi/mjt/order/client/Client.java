package bg.sofia.uni.fmi.mjt.order.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private static final int SERVER_PORT = 6969;

    public static void main(String[] args) {

        try (Socket socket = new Socket("localhost", SERVER_PORT);
             ObjectOutput writer = new ObjectOutputStream(socket.getOutputStream());
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             Scanner scanner = new Scanner(System.in)) {

            while (true) {
                System.out.print("=> ");
                String command = scanner.nextLine();

                Request request = Request.parse(command);

                if (request == null) {
                    System.out.println("=> Unknown command");
                    continue;
                }

                writer.writeObject(request);
                writer.flush();

                if (command.equals("disconnect")) {
                    System.out.println("=> Disconnected from the server");
                    break;
                }

                System.out.println("=> " + reader.readLine());

            }

        } catch (IOException e) {
            throw new RuntimeException("There is a problem with the network communication", e);
        }
    }
}
