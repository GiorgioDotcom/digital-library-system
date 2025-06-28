package it.epicode.library.factory;

/**
 * Enumeration defining the supported media types in the library system.
 */
public enum MediaType {
    BOOK("Book", "Physical books"),
    AUDIOBOOK("AudioBook", "Digital audio books"),
    EBOOK("EBook", "Digital electronic books"),
    DVD("DVD", "Digital video discs");

    private final String displayName;
    private final String description;

    MediaType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return displayName;
    }
}