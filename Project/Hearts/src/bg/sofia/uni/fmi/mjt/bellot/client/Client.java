package bg.sofia.uni.fmi.mjt.bellot.client;

import bg.sofia.uni.fmi.mjt.bellot.client.player.ClientPlayer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.nio.channels.Channels;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class Client {

    private static final int SERVER_PORT = 7777;
    private static final String SERVER_HOST = "localhost";

    public static void main(String[] args) {
        Client.start();
    }

    private static void start() {
        try (SocketChannel socketChannel = SocketChannel.open();
             BufferedReader reader = new BufferedReader(Channels.newReader(socketChannel, StandardCharsets.UTF_8));
             PrintWriter writer = new PrintWriter(Channels.newWriter(socketChannel, StandardCharsets.UTF_8), true)) {

            socketChannel.connect(new InetSocketAddress(SERVER_HOST, SERVER_PORT));

            ClientPlayer.start(reader, writer);

        } catch (IOException e) {
            throw new RuntimeException("There is a problem with the network communication", e);
        }
    }
}