package it.epicode.library.model.structure;

import it.epicode.library.model.media.Media;
import it.epicode.library.iterator.MediaIterator;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;

/**
 * Abstract component class implementing the Composite Pattern.
 * Defines the interface for all library structure components.
 */
public abstract class LibraryComponent implements Serializable {
    private static final long serialVersionUID = 1L;

    protected String id;
    protected String name;
    protected String description;
    protected final List<LibraryComponent> children;
    protected final List<Media> mediaItems;

    // Costruttore senza parametri per serializzazione
    protected LibraryComponent() {
        this.id = UUID.randomUUID().toString();
        this.name = "Default";
        this.description = "Default description";
        this.children = new CopyOnWriteArrayList<>();
        this.mediaItems = new CopyOnWriteArrayList<>();
    }

    protected LibraryComponent(String name, String description) {
        this.id = UUID.randomUUID().toString();
        this.name = Objects.requireNonNull(name, "Name cannot be null");
        this.description = description;
        // Thread-safe collections for concurrent access
        this.children = new CopyOnWriteArrayList<>();
        this.mediaItems = new CopyOnWriteArrayList<>();
    }

    // Abstract methods that define the component interface
    public abstract void add(LibraryComponent component);
    public abstract boolean remove(LibraryComponent component);
    public abstract List<Media> getAllMedia();
    public abstract MediaIterator iterator();
    public abstract MediaIterator iterator(Predicate<Media> filter);
    public abstract int getMediaCount();
    public abstract void displayStructure(int depth);

    // Common methods for all components
    public String getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) {
        this.name = Objects.requireNonNull(name, "Name cannot be null");
    }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<LibraryComponent> getChildren() {
        return new ArrayList<>(children);
    }

    public List<Media> getDirectMedia() {
        return new ArrayList<>(mediaItems);
    }

    /**
     * Finds a child component by name (recursive search).
     */
    public Optional<LibraryComponent> findChildByName(String name) {
        for (LibraryComponent child : children) {
            if (child.getName().equals(name)) {
                return Optional.of(child);
            }
            Optional<LibraryComponent> found = child.findChildByName(name);
            if (found.isPresent()) {
                return found;
            }
        }
        return Optional.empty();
    }

    /**
     * Gets the full path of this component in the hierarchy.
     */
    public abstract String getPath();

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        LibraryComponent that = (LibraryComponent) obj;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("%s{name='%s', children=%d, media=%d}",
                getClass().getSimpleName(), name, children.size(), mediaItems.size());
    }
}