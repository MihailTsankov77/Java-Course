package bg.sofia.uni.fmi.mjt.photoalbum.image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;

public class Image {
    private final String name;
    private final ImageExtensions extension;
    BufferedImage data;

    public Image(String name, BufferedImage data, ImageExtensions extension) {
        this.name = name;
        this.data = data;
        this.extension = extension;
    }

    public static Image loadImage(Path imagePath, ImageExtensions extension) {
        try {
            BufferedImage imageData = ImageIO.read(imagePath.toFile());
            return new Image(imagePath.getFileName().toString(), imageData, extension);
        } catch (IOException e) {
            throw new UncheckedIOException(String.format("Failed to load image %s", imagePath), e);
        }
    }

    public static void saveImage(Image image, Path directory) {
        try {
            ImageIO.write(image.data, image.extension.name().toLowerCase(), directory.resolve(image.name).toFile());
        } catch (IOException e) {
            throw new RuntimeException("Something went wrong when saving image:", e);
        }
    }

    public static Image convertToBlackAndWhite(Image image) {
        BufferedImage processedData = new BufferedImage(image.data.getWidth(),
                image.data.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        processedData.getGraphics().drawImage(image.data, 0, 0, null);

        return new Image(image.name, processedData, image.extension);
    }
}