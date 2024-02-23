package bg.sofia.uni.fmi.mjt.bellot.server.room;

import bg.sofia.uni.fmi.mjt.bellot.filemanager.FileManager;
import bg.sofia.uni.fmi.mjt.bellot.filemanager.GamePlayedFileManager;
import bg.sofia.uni.fmi.mjt.bellot.game.GamePlay;
import bg.sofia.uni.fmi.mjt.bellot.game.GameResult;
import bg.sofia.uni.fmi.mjt.bellot.server.player.Player;

import java.io.IOException;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.Map;

public class Room {
    private static final FileManager GAME_FILE_MANAGER = GamePlayedFileManager.getInstance();
    private final int roomId;
    private final Map<SocketChannel, Player> players;
    private final List<Integer> playersIds;
    private final Selector selector;

    Room(int roomId, Map<SocketChannel, Player> players, Selector selector) {
        this.players = players;
        this.selector = selector;
        this.roomId = roomId;

        playersIds = players.values().stream().map(Player::getId).toList();
    }

    public void start() {
        try (Communicator communicator = new Communicator(players, selector, playersIds)) {
            communicator.getNames();

            GameResult result = GamePlay.start(playersIds, communicator);

            GAME_FILE_MANAGER.writeLine(result.toString(communicator.getPlayersNames()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
