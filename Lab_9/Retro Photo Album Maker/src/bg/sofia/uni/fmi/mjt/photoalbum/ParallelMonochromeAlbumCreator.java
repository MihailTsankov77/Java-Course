package bg.sofia.uni.fmi.mjt.photoalbum;

import bg.sofia.uni.fmi.mjt.photoalbum.image.Image;
import bg.sofia.uni.fmi.mjt.photoalbum.image.ImageExtensions;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Optional;

public class ParallelMonochromeAlbumCreator implements MonochromeAlbumCreator {
    private final int imageProcessorsCount;
    private final MyBlockingQueue<Image> imagesForProcessing = new MyBlockingQueue<>();
    private Path outputPath;
    private boolean areAllImagesConsumed = false;

    public ParallelMonochromeAlbumCreator(int imageProcessorsCount) {
        this.imageProcessorsCount = imageProcessorsCount;
    }

    public static String getExtension(Path path) {
        String filename = path.getFileName().toString();
        return Optional.of(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(filename.lastIndexOf(".") + 1)).orElse("");
    }

    @Override
    public void processImages(String sourceDirectory, String outputDirectory) {
        Thread.ofPlatform().start(() -> readImages(sourceDirectory));
        Thread.ofPlatform().start(() -> consume(outputDirectory));
    }

    private void consume(String outputDirectory) {
        try {
            this.outputPath = Files.createDirectories(Paths.get(outputDirectory));
        } catch (IOException e) {
            throw new RuntimeException("Something went wrong writing in the directory", e);
        }

        Thread[] threads = new Thread[imageProcessorsCount];
        for (int i = 0; i < imageProcessorsCount; i++) {
            threads[i] = Thread.ofVirtual().start(this::processedImage);
            threads[i].setName("Consumer " + i);
        }

        try {
            for (Thread thread : threads) {
                thread.join();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void readImages(String sourceDirectory) {
        ArrayList<Thread> threads = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(sourceDirectory))) {
            for (Path path : stream) {
                String extension = getExtension(path);

                if (!Files.isDirectory(path) && ImageExtensions.contains(extension)) {
                    threads.add(Thread.ofVirtual().start(() -> {
                        Image image = Image.loadImage(path, ImageExtensions.valueOf(extension.toUpperCase()));
                        imagesForProcessing.add(image);
                    }));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Something went wrong reading from the directory", e);
        }

        try {
            for (Thread thread :
                    threads) {
                thread.join();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        areAllImagesConsumed = true;
    }

    private void processedImage() {
        while (!areAllImagesConsumed || !imagesForProcessing.isEmpty()) {

            Image image = this.imagesForProcessing.get();
            Image processedImage = Image.convertToBlackAndWhite(image);
            saveImage(processedImage);
        }
    }

    private synchronized void saveImage(Image image) {
        Image.saveImage(image, outputPath);
    }
}
