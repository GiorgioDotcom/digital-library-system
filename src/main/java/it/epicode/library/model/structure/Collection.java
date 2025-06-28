package it.epicode.library.model.structure;

import it.epicode.library.model.media.Media;
import it.epicode.library.iterator.MediaIterator;
import it.epicode.library.iterator.CollectionIterator;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Collection extends LibraryComponent {
    private String theme; // Specific theme like "Italian Literature", "Science Fiction", etc.
    private String curator;
    private boolean isSpecialCollection;
    private int maxItems;

    public Collection(String name, String description) {
        super(name, description);
        this.theme = "General";
        this.isSpecialCollection = false;
        this.maxItems = 500; // Default max items
    }

    public Collection(String name, String description, String theme) {
        super(name, description);
        this.theme = theme;
        this.isSpecialCollection = false;
        this.maxItems = 500;
    }

    @Override
    public void add(LibraryComponent component) {
        // Collections are leaf nodes - they cannot contain other components
        throw new UnsupportedOperationException("Collections cannot contain other components. Use addMedia() instead.");
    }

    @Override
    public boolean remove(LibraryComponent component) {
        // Collections are leaf nodes
        throw new UnsupportedOperationException("Collections cannot contain components. Use removeMedia() instead.");
    }

    /**
     * Adds media to this collection.
     */
    public void addMedia(Media media) {
        if (media == null) {
            throw new IllegalArgumentException("Cannot add null media");
        }

        if (mediaItems.size() >= maxItems) {
            throw new IllegalStateException("Collection has reached maximum capacity: " + maxItems);
        }

        mediaItems.add(media);
        media.setLocation(getPath());
    }

    /**
     * Removes media from this collection.
     */
    public boolean removeMedia(Media media) {
        return mediaItems.remove(media);
    }

    /**
     * Adds multiple media items to this collection.
     */
    public void addAllMedia(List<Media> mediaList) {
        if (mediaList == null) {
            throw new IllegalArgumentException("Media list cannot be null");
        }

        if (mediaItems.size() + mediaList.size() > maxItems) {
            throw new IllegalStateException("Adding these items would exceed maximum capacity: " + maxItems);
        }

        mediaList.forEach(this::addMedia);
    }

    @Override
    public List<Media> getAllMedia() {
        return new ArrayList<>(mediaItems);
    }

    @Override
    public MediaIterator iterator() {
        return new CollectionIterator(this);
    }

    @Override
    public MediaIterator iterator(Predicate<Media> filter) {
        return new CollectionIterator(this, filter);
    }

    @Override
    public int getMediaCount() {
        return mediaItems.size();
    }

    @Override
    public String getPath() {
        return "/" + name;
    }

    @Override
    public void displayStructure(int depth) {
        String indent = "  ".repeat(depth);
        String specialMark = isSpecialCollection ? "⭐" : "📁";
        System.out.printf("%s%s %s (Collection) - %d media items%n",
                indent, specialMark, name, mediaItems.size());

        if (!mediaItems.isEmpty()) {
            Map<String, Long> typeCount = mediaItems.stream()
                    .collect(Collectors.groupingBy(
                            Media::getMediaType,
                            Collectors.counting()
                    ));

            System.out.printf("%s    Types: %s%n", indent, typeCount);
        }
    }

    /**
     * Gets media by type.
     */
    public List<Media> getMediaByType(String mediaType) {
        return mediaItems.stream()
                .filter(media -> media.getMediaType().equals(mediaType))
                .collect(Collectors.toList());
    }

    /**
     * Gets available media.
     */
    public List<Media> getAvailableMedia() {
        return mediaItems.stream()
                .filter(Media::isAvailable)
                .collect(Collectors.toList());
    }

    /**
     * Checks if collection is full.
     */
    public boolean isFull() {
        return mediaItems.size() >= maxItems;
    }

    /**
     * Gets the remaining capacity.
     */
    public int getRemainingCapacity() {
        return maxItems - mediaItems.size();
    }

    /**
     * Finds media by title (case-insensitive).
     */
    public List<Media> findByTitle(String title) {
        String lowerTitle = title.toLowerCase();
        return mediaItems.stream()
                .filter(media -> media.getTitle().toLowerCase().contains(lowerTitle))
                .collect(Collectors.toList());
    }

    /**
     * Finds media by author/director (case-insensitive).
     */
    public List<Media> findByAuthor(String author) {
        String lowerAuthor = author.toLowerCase();
        return mediaItems.stream()
                .filter(media -> media.getMainAuthor().toLowerCase().contains(lowerAuthor))
                .collect(Collectors.toList());
    }

    // Getters and setters
    public String getTheme() { return theme; }
    public void setTheme(String theme) { this.theme = theme; }

    public String getCurator() { return curator; }
    public void setCurator(String curator) { this.curator = curator; }

    public boolean isSpecialCollection() { return isSpecialCollection; }
    public void setSpecialCollection(boolean specialCollection) {
        this.isSpecialCollection = specialCollection;
    }

    public int getMaxItems() { return maxItems; }
    public void setMaxItems(int maxItems) {
        if (maxItems < 0) throw new IllegalArgumentException("Max items cannot be negative");
        if (maxItems < mediaItems.size()) {
            throw new IllegalArgumentException("Max items cannot be less than current item count");
        }
        this.maxItems = maxItems;
    }
}