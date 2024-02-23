package bg.sofia.uni.fmi.mjt.bellot.filemanager;

public interface FileManager {

    /**
     * Save one line in a file it adds the new row
     *
     * @param line
     */
    void writeLine(String line);

    /**
     * Read the whole file
     *
     * @return the file split by rows
     */
    String read();
}
