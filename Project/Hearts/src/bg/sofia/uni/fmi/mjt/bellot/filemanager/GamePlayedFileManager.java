package bg.sofia.uni.fmi.mjt.bellot.filemanager;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class GamePlayedFileManager implements FileManager {

    private static final String GAME_FILE_PATH = "game_results.txt";
    private static final GamePlayedFileManager INSTANCE = new GamePlayedFileManager();

    private GamePlayedFileManager() {
    }

    public static FileManager getInstance() {
        return INSTANCE;
    }

    @Override
    public synchronized void writeLine(String line) {
        String row = line + "\n";
        try {
            Files.write(Paths.get(GAME_FILE_PATH), row.getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public synchronized String read() {
        StringBuilder results = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(GAME_FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                results.append(line).append("\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return results.toString();
    }
}
