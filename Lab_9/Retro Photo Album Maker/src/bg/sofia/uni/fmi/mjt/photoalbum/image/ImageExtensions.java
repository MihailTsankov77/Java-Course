package bg.sofia.uni.fmi.mjt.photoalbum.image;

public enum ImageExtensions {
    PNG,
    JPEG,
    JPG;

    public static boolean contains(String extension) {
        try {
            ImageExtensions.valueOf(extension.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

}
