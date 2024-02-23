package bg.sofia.uni.fmi.mjt.bellot.server;

import bg.sofia.uni.fmi.mjt.bellot.filemanager.GamePlayedFileManager;
import bg.sofia.uni.fmi.mjt.bellot.server.room.RoomBuilder;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    public static final int SERVER_PORT = 7777;
    private static final String SERVER_HOST = "localhost";

    private final RoomBuilder roomBuilder = new RoomBuilder();
    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    private Server() throws IOException {
    }

    public static void main(String[] args) throws IOException {
        Server.start();
    }

    public static void start() throws IOException {
        new Server().startServer();
    }

    private void accept(ServerSocketChannel sockChannel) throws IOException {
        roomBuilder.addPlayer(sockChannel.accept());

        if (roomBuilder.isRoomFull()) {
            executor.submit(roomBuilder.buildThread());

            roomBuilder.clear();
        }
    }

    private void startServer() {
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
            serverSocketChannel.bind(new InetSocketAddress(SERVER_HOST, SERVER_PORT));
            serverSocketChannel.configureBlocking(false);

            Selector selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            while (true) {
                int readyChannels = selector.select();
                if (readyChannels == 0) {
                    continue;
                }

                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

                while (keyIterator.hasNext()) {
                    SelectionKey key = keyIterator.next();
                    accept((ServerSocketChannel) key.channel());
                    keyIterator.remove();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("There is a problem with the bg.sofia.uni.fmi.mjt.bellot.server socket", e);
        } finally {
            stopServer();
        }
    }

    private void stopServer() {
        executor.shutdown();
    }
}