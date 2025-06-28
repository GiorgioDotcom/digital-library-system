package it.epicode.library.model.structure;

import it.epicode.library.model.media.Media;
import it.epicode.library.iterator.MediaIterator;
import it.epicode.library.iterator.CompositeIterator;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Section extends LibraryComponent {
    private String category; // Fiction, Non-Fiction, Reference, etc.
    private String floor;
    private String responsibleLibrarian;

    public Section(String name, String description) {
        super(name, description);
        this.category = "General";
        this.floor = "Ground Floor";
    }

    public Section(String name, String description, String category) {
        super(name, description);
        this.category = category;
        this.floor = "Ground Floor";
    }

    @Override
    public void add(LibraryComponent component) {
        if (component == null) {
            throw new IllegalArgumentException("Cannot add null component");
        }

        // Sections can contain Collections
        if (component instanceof Collection) {
            children.add(component);
        } else {
            throw new IllegalArgumentException("Section can only contain Collections");
        }
    }

    @Override
    public boolean remove(LibraryComponent component) {
        return children.remove(component);
    }

    /**
     * Adds media directly to this section (without a collection).
     */
    public void addMedia(Media media) {
        if (media == null) {
            throw new IllegalArgumentException("Cannot add null media");
        }

        mediaItems.add(media);
        media.setLocation(getPath());
    }

    /**
     * Removes media from this section.
     */
    public boolean removeMedia(Media media) {
        return mediaItems.remove(media);
    }

    @Override
    public List<Media> getAllMedia() {
        List<Media> allMedia = new ArrayList<>(mediaItems);

        // Collect media from all collections
        children.stream()
                .forEach(child -> allMedia.addAll(child.getAllMedia()));

        return allMedia;
    }

    @Override
    public MediaIterator iterator() {
        return new CompositeIterator(this);
    }

    @Override
    public MediaIterator iterator(Predicate<Media> filter) {
        return new CompositeIterator(this, filter);
    }

    @Override
    public int getMediaCount() {
        return mediaItems.size() +
                children.stream().mapToInt(LibraryComponent::getMediaCount).sum();
    }

    @Override
    public String getPath() {
        return "/" + name;
    }

    @Override
    public void displayStructure(int depth) {
        String indent = "  ".repeat(depth);
        System.out.printf("%s📂 %s (Section) - %d collections, %d direct media%n",
                indent, name, children.size(), mediaItems.size());

        children.forEach(child -> child.displayStructure(depth + 1));

        if (!mediaItems.isEmpty()) {
            System.out.printf("%s  📄 Direct media: %d items%n", indent, mediaItems.size());
        }
    }

    /**
     * Adds a new collection to this section.
     */
    public Collection addCollection(String collectionName, String description) {
        Collection collection = new Collection(collectionName, description);
        add(collection);
        return collection;
    }

    /**
     * Gets all collections in this section.
     */
    public List<Collection> getCollections() {
        return children.stream()
                .filter(Collection.class::isInstance)
                .map(Collection.class::cast)
                .collect(Collectors.toList());
    }

    /**
     * Finds a collection by name.
     */
    public Optional<Collection> findCollection(String collectionName) {
        return children.stream()
                .filter(Collection.class::isInstance)
                .map(Collection.class::cast)
                .filter(collection -> collection.getName().equals(collectionName))
                .findFirst();
    }

    // Getters and setters
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getFloor() { return floor; }
    public void setFloor(String floor) { this.floor = floor; }

    public String getResponsibleLibrarian() { return responsibleLibrarian; }
    public void setResponsibleLibrarian(String responsibleLibrarian) {
        this.responsibleLibrarian = responsibleLibrarian;
    }
}