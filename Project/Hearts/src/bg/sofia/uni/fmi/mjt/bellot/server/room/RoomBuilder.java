package bg.sofia.uni.fmi.mjt.bellot.server.room;

import bg.sofia.uni.fmi.mjt.bellot.game.exceptions.RoomIsFullException;
import bg.sofia.uni.fmi.mjt.bellot.server.player.Player;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;

public class RoomBuilder {

    private static final int MAX_PLAYERS = 4;
    private static int roomId = 0;
    private int currentPlayerId = 0;
    private Selector selector;
    private Map<SocketChannel, Player> players = new HashMap<>();

    public RoomBuilder() throws IOException {
        selector = Selector.open();
    }

    public void addPlayer(SocketChannel channel) throws IOException {
        if (isRoomFull()) {
            throw new RoomIsFullException("Max players " + MAX_PLAYERS);
        }

        channel.configureBlocking(false);
        channel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);

        Player newPlayer = Player.builder()
                .setId(currentPlayerId++)
                .setChannel(channel)
                .build();

        players.put(channel, newPlayer);
    }

    public boolean isRoomFull() {
        return currentPlayerId >= MAX_PLAYERS;
    }

    public Room build() {
        return new Room(roomId++, players, selector);
    }

    public RoomThead buildThread() {
        return new RoomThead(build());
    }

    public void clear() throws IOException {
        players = new HashMap<>();
        currentPlayerId = 0;
        selector = Selector.open();
    }
}
