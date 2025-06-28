package it.epicode.library.model.media;

import java.util.Objects;

/**
 * Represents a DVD with director and runtime information.
 */
public class DVD extends Media {
    private String director;
    private int runtimeMinutes;
    private String genre;
    private String ageRating;
    private String language;
    private boolean hasSubtitles;

    public DVD(String title, String director, int runtimeMinutes) {
        super(title, "");
        this.director = Objects.requireNonNull(director, "Director cannot be null");
        this.runtimeMinutes = runtimeMinutes;
        this.language = "Italian";
        this.hasSubtitles = false;
    }

    @Override
    public String getMediaType() {
        return "DVD";
    }

    @Override
    public String getMainAuthor() {
        return director;
    }

    @Override
    public String getIdentifier() {
        return String.format("DVD-%s-%s", director.replaceAll("\\s+", ""),
                title.replaceAll("\\s+", "")).toUpperCase();
    }

    @Override
    public String toCsvString() {
        return String.format("%s,%s,%s,%d,%s,%b",
                getMediaType(), title, director, runtimeMinutes, genre, isAvailable);
    }

    public String getRuntimeFormatted() {
        int hours = runtimeMinutes / 60;
        int minutes = runtimeMinutes % 60;
        return String.format("%dh %02dm", hours, minutes);
    }

    // Getters and setters
    public String getDirector() { return director; }
    public void setDirector(String director) {
        this.director = Objects.requireNonNull(director, "Director cannot be null");
    }

    public int getRuntimeMinutes() { return runtimeMinutes; }
    public void setRuntimeMinutes(int runtimeMinutes) {
        if (runtimeMinutes < 0) throw new IllegalArgumentException("Runtime cannot be negative");
        this.runtimeMinutes = runtimeMinutes;
    }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    public String getAgeRating() { return ageRating; }
    public void setAgeRating(String ageRating) { this.ageRating = ageRating; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    public boolean isHasSubtitles() { return hasSubtitles; }
    public void setHasSubtitles(boolean hasSubtitles) { this.hasSubtitles = hasSubtitles; }
}