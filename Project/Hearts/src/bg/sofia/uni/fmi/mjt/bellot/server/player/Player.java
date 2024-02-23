package bg.sofia.uni.fmi.mjt.bellot.server.player;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.PrintWriter;

public class Player implements Closeable {
    private final int id;

    private final BufferedReader reader;
    private final PrintWriter writer;

    Player(int id, BufferedReader reader, PrintWriter writer) {
        this.id = id;
        this.reader = reader;
        this.writer = writer;
    }

    public static PlayerBuilder builder() {
        return new PlayerBuilder();
    }

    public int getId() {
        return id;
    }

    public String read() throws IOException {
        return reader.readLine();
    }

    public void write(String input) {
        writer.println(input);
    }

    @Override
    public void close() throws IOException {
        reader.close();
        writer.close();
    }
}
