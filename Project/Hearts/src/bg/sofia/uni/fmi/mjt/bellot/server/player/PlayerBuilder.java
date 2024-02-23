package bg.sofia.uni.fmi.mjt.bellot.server.player;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.nio.channels.Channels;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class PlayerBuilder {
    private int id;
    private BufferedReader reader;
    private PrintWriter writer;

    PlayerBuilder() {
    }

    public PlayerBuilder setId(int id) {
        this.id = id;
        return this;
    }

    public PlayerBuilder setChannel(SocketChannel socketChannel) {
        reader = new BufferedReader(Channels.newReader(socketChannel, StandardCharsets.UTF_8));
        writer = new PrintWriter(Channels.newWriter(socketChannel, StandardCharsets.UTF_8), true);
        return this;
    }

    public Player build() {
        return new Player(id, reader, writer);
    }
}
