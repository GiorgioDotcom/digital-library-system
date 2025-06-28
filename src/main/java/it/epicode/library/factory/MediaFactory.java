package it.epicode.library.factory;

import it.epicode.library.model.media.*;
import java.util.Map;
import java.util.Objects;

/**
 * Factory class for creating different types of media objects.
 * Implements the Factory Pattern to encapsulate object creation logic.
 */
public class MediaFactory {

    /**
     * Creates a media object based on the specified type and properties.
     *
     * @param type The type of media to create
     * @param properties Map containing the properties needed for media creation
     * @return A new Media object of the specified type
     * @throws IllegalArgumentException if the media type is unsupported or properties are invalid
     */
    public static Media createMedia(MediaType type, Map<String, Object> properties) {
        Objects.requireNonNull(type, "Media type cannot be null");
        Objects.requireNonNull(properties, "Properties cannot be null");

        return switch (type) {
            case BOOK -> createBook(properties);
            case AUDIOBOOK -> createAudioBook(properties);
            case EBOOK -> createEBook(properties);
            case DVD -> createDVD(properties);
        };
    }

    private static Book createBook(Map<String, Object> props) {
        String title = getRequiredString(props, "title");
        String author = getRequiredString(props, "author");
        String isbn = getRequiredString(props, "isbn");

        Book book = new Book(title, author, isbn);

        // Optional properties
        if (props.containsKey("publisher")) {
            book.setPublisher((String) props.get("publisher"));
        }
        if (props.containsKey("pages")) {
            book.setPages((Integer) props.get("pages"));
        }
        if (props.containsKey("genre")) {
            book.setGenre((String) props.get("genre"));
        }

        return book;
    }

    private static AudioBook createAudioBook(Map<String, Object> props) {
        String title = getRequiredString(props, "title");
        String author = getRequiredString(props, "author");
        String narrator = getRequiredString(props, "narrator");
        Integer duration = getRequiredInteger(props, "duration");

        return new AudioBook(title, author, narrator, duration);
    }

    private static EBook createEBook(Map<String, Object> props) {
        String title = getRequiredString(props, "title");
        String author = getRequiredString(props, "author");
        String format = getRequiredString(props, "format");
        Double fileSize = getRequiredDouble(props, "fileSize");

        return new EBook(title, author, format, fileSize);
    }

    private static DVD createDVD(Map<String, Object> props) {
        String title = getRequiredString(props, "title");
        String director = getRequiredString(props, "director");
        Integer runtime = getRequiredInteger(props, "runtime");

        return new DVD(title, director, runtime);
    }

    // Helper methods for type-safe property extraction
    private static String getRequiredString(Map<String, Object> props, String key) {
        Object value = props.get(key);
        if (value == null || !(value instanceof String)) {
            throw new IllegalArgumentException("Required string property '" + key + "' is missing or invalid");
        }
        return (String) value;
    }

    private static Integer getRequiredInteger(Map<String, Object> props, String key) {
        Object value = props.get(key);
        if (value == null || !(value instanceof Integer)) {
            throw new IllegalArgumentException("Required integer property '" + key + "' is missing or invalid");
        }
        return (Integer) value;
    }

    private static Double getRequiredDouble(Map<String, Object> props, String key) {
        Object value = props.get(key);
        if (value == null || !(value instanceof Double)) {
            throw new IllegalArgumentException("Required double property '" + key + "' is missing or invalid");
        }
        return (Double) value;
    }
}