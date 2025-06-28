package it.epicode.library.model.media;

import java.util.Objects;

/**
 * Represents an audiobook with narrator and duration information.
 */
public class AudioBook extends Media {
    private String narrator;
    private String author;
    private int durationMinutes;
    private String format; // MP3, WAV, etc.
    private double fileSizeMB;

    public AudioBook(String title, String author, String narrator, int durationMinutes) {
        super(title, "");
        this.author = Objects.requireNonNull(author, "Author cannot be null");
        this.narrator = Objects.requireNonNull(narrator, "Narrator cannot be null");
        this.durationMinutes = durationMinutes;
        this.format = "MP3";
    }

    @Override
    public String getMediaType() {
        return "AUDIOBOOK";
    }

    @Override
    public String getMainAuthor() {
        return author;
    }

    @Override
    public String getIdentifier() {
        return String.format("AUD-%s-%s", author.replaceAll("\\s+", ""),
                title.replaceAll("\\s+", "")).toUpperCase();
    }

    @Override
    public String toCsvString() {
        return String.format("%s,%s,%s,%s,%d,%b",
                getMediaType(), title, author, narrator, durationMinutes, isAvailable);
    }

    public String getDurationFormatted() {
        int hours = durationMinutes / 60;
        int minutes = durationMinutes % 60;
        return String.format("%dh %02dm", hours, minutes);
    }

    // Getters and setters
    public String getNarrator() { return narrator; }
    public void setNarrator(String narrator) {
        this.narrator = Objects.requireNonNull(narrator, "Narrator cannot be null");
    }

    public String getAuthor() { return author; }
    public void setAuthor(String author) {
        this.author = Objects.requireNonNull(author, "Author cannot be null");
    }

    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes) {
        if (durationMinutes < 0) throw new IllegalArgumentException("Duration cannot be negative");
        this.durationMinutes = durationMinutes;
    }

    public String getFormat() { return format; }
    public void setFormat(String format) { this.format = format; }

    public double getFileSizeMB() { return fileSizeMB; }
    public void setFileSizeMB(double fileSizeMB) { this.fileSizeMB = fileSizeMB; }
}