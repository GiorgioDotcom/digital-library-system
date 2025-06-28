package it.epicode.library.model.structure;

import it.epicode.library.model.media.Media;
import it.epicode.library.iterator.MediaIterator;
import it.epicode.library.iterator.CompositeIterator;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Library extends LibraryComponent implements Serializable {
    private static final long serialVersionUID = 1L;

    private String address;
    private String phoneNumber;
    private String email;
    private LocalDateTime establishedDate;
    private int maxCapacity;

    // Costruttore senza parametri richiesto per la serializzazione
    public Library() {
        super("Default Library", "Default library system");
        this.address = "Default Address";
        this.establishedDate = LocalDateTime.now();
        this.maxCapacity = 10000;
    }

    public Library(String name, String address) {
        super(name, "Main library system");
        this.address = address;
        this.establishedDate = LocalDateTime.now();
        this.maxCapacity = 10000;
    }

    public Library(String name, String address, String description) {
        super(name, description);
        this.address = address;
        this.establishedDate = LocalDateTime.now();
        this.maxCapacity = 10000;
    }

    @Override
    public void add(LibraryComponent component) {
        if (component == null) {
            throw new IllegalArgumentException("Cannot add null component");
        }
        if (component == this) {
            throw new IllegalArgumentException("Cannot add library to itself");
        }

        // Only allow Sections at the library level
        if (!(component instanceof Section)) {
            throw new IllegalArgumentException("Library can only contain Sections");
        }

        children.add(component);
    }

    @Override
    public boolean remove(LibraryComponent component) {
        return children.remove(component);
    }

    @Override
    public List<Media> getAllMedia() {
        List<Media> allMedia = new ArrayList<>(mediaItems);

        // Collect media from all sections
        children.parallelStream()
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
        System.out.printf("%s📚 %s (Library) - %d sections, %d total media%n",
                indent, name, children.size(), getMediaCount());

        children.forEach(child -> child.displayStructure(depth + 1));
    }

    /**
     * Adds a new section to the library.
     */
    public Section addSection(String sectionName, String description) {
        Section section = new Section(sectionName, description);
        add(section);
        return section;
    }

    /**
     * Gets all sections in the library.
     */
    public List<Section> getSections() {
        return children.stream()
                .filter(Section.class::isInstance)
                .map(Section.class::cast)
                .collect(Collectors.toList());
    }

    /**
     * Finds a section by name.
     */
    public Optional<Section> findSection(String sectionName) {
        return children.stream()
                .filter(Section.class::isInstance)
                .map(Section.class::cast)
                .filter(section -> section.getName().equals(sectionName))
                .findFirst();
    }

    /**
     * Gets library statistics.
     */
    public LibraryStatistics getStatistics() {
        return new LibraryStatistics(this);
    }

    // Getters and setters
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public LocalDateTime getEstablishedDate() { return establishedDate; }
    public void setEstablishedDate(LocalDateTime establishedDate) {
        this.establishedDate = establishedDate;
    }

    public int getMaxCapacity() { return maxCapacity; }
    public void setMaxCapacity(int maxCapacity) {
        if (maxCapacity < 0) throw new IllegalArgumentException("Capacity cannot be negative");
        this.maxCapacity = maxCapacity;
    }

    /**
     * Inner class for library statistics.
     */
    public static class LibraryStatistics implements Serializable {
        private static final long serialVersionUID = 1L;

        private final int totalMedia;
        private final int totalSections;
        private final int totalCollections;
        private final Map<String, Integer> mediaTypeCount;
        private final int availableMedia;

        public LibraryStatistics(Library library) {
            this.totalMedia = library.getMediaCount();
            this.totalSections = library.getSections().size();
            this.totalCollections = library.getSections().stream()
                    .mapToInt(s -> s.getCollections().size())
                    .sum();

            List<Media> allMedia = library.getAllMedia();
            this.availableMedia = (int) allMedia.stream().filter(Media::isAvailable).count();
            this.mediaTypeCount = allMedia.stream()
                    .collect(Collectors.groupingBy(
                            Media::getMediaType,
                            Collectors.summingInt(m -> 1)
                    ));
        }

        // Getters
        public int getTotalMedia() { return totalMedia; }
        public int getTotalSections() { return totalSections; }
        public int getTotalCollections() { return totalCollections; }
        public Map<String, Integer> getMediaTypeCount() { return new HashMap<>(mediaTypeCount); }
        public int getAvailableMedia() { return availableMedia; }
        public int getUnavailableMedia() { return totalMedia - availableMedia; }

        @Override
        public String toString() {
            return String.format("LibraryStatistics{total=%d, available=%d, sections=%d, collections=%d, types=%s}",
                    totalMedia, availableMedia, totalSections, totalCollections, mediaTypeCount);
        }
    }
}