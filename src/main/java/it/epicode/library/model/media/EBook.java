package it.epicode.library.model.media;

import java.util.Objects;

/**
 * Represents a digital ebook with format and file size information.
 */
public class EBook extends Media {
    private String author;
    private String format; // PDF, EPUB, MOBI
    private double fileSizeMB;
    private String isbn;
    private boolean hasDRM;
    private int downloadCount;

    public EBook(String title, String author, String format, double fileSizeMB) {
        super(title, "");
        this.author = Objects.requireNonNull(author, "Author cannot be null");
        this.format = Objects.requireNonNull(format, "Format cannot be null");
        this.fileSizeMB = fileSizeMB;
        this.hasDRM = false;
        this.downloadCount = 0;
    }

    @Override
    public String getMediaType() {
        return "EBOOK";
    }

    @Override
    public String getMainAuthor() {
        return author;
    }

    @Override
    public String getIdentifier() {
        return isbn != null ? isbn : String.format("EB-%s", getId().substring(0, 8));
    }

    @Override
    public String toCsvString() {
        return String.format("%s,%s,%s,%s,%.2f,%b",
                getMediaType(), title, author, format, fileSizeMB, isAvailable);
    }

    public void incrementDownloadCount() {
        this.downloadCount++;
    }

    // Getters and setters
    public String getAuthor() { return author; }
    public void setAuthor(String author) {
        this.author = Objects.requireNonNull(author, "Author cannot be null");
    }

    public String getFormat() { return format; }
    public void setFormat(String format) {
        this.format = Objects.requireNonNull(format, "Format cannot be null");
    }

    public double getFileSizeMB() { return fileSizeMB; }
    public void setFileSizeMB(double fileSizeMB) { this.fileSizeMB = fileSizeMB; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public boolean isHasDRM() { return hasDRM; }
    public void setHasDRM(boolean hasDRM) { this.hasDRM = hasDRM; }

    public int getDownloadCount() { return downloadCount; }
}