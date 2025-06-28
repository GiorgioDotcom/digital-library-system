package it.epicode.library.model.media;

import it.epicode.library.repository.Identifiable;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

/**
 * Abstract base class for all media types in the library system.
 * Implements common functionality and defines contract for concrete media types.
 */
public abstract class Media implements Serializable, Identifiable {
    private static final long serialVersionUID = 1L;

    protected final String id;
    protected String title;
    protected String description;
    protected LocalDate acquisitionDate;
    protected boolean isAvailable;
    protected String location;

    protected Media(String title, String description) {
        this.id = UUID.randomUUID().toString();
        this.title = Objects.requireNonNull(title, "Title cannot be null");
        this.description = description;
        this.acquisitionDate = LocalDate.now();
        this.isAvailable = true;
        this.location = "General Collection";
    }

    // Abstract methods that concrete classes must implement
    public abstract String getMediaType();
    public abstract String getMainAuthor();
    public abstract String getIdentifier(); // ISBN, ISSN, etc.
    public abstract String toCsvString();

    // Getters and setters
    @Override
    public String getId() { return id; }

    public String getTitle() { return title; }
    public void setTitle(String title) {
        this.title = Objects.requireNonNull(title, "Title cannot be null");
    }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getAcquisitionDate() { return acquisitionDate; }
    public void setAcquisitionDate(LocalDate acquisitionDate) {
        this.acquisitionDate = acquisitionDate;
    }

    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { this.isAvailable = available; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Media media = (Media) obj;
        return Objects.equals(id, media.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("%s{id='%s', title='%s', available=%s}",
                getClass().getSimpleName(), id, title, isAvailable);
    }
}