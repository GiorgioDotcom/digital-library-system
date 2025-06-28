package it.epicode.library.model.media;

import java.util.Objects;

/**
 * Represents a physical book in the library.
 * Contains book-specific attributes like ISBN, author, publisher.
 */
public class Book extends Media {
    private String author;
    private String isbn;
    private String publisher;
    private int pages;
    private String genre;
    private String language;

    public Book(String title, String author, String isbn) {
        super(title, "");
        this.author = Objects.requireNonNull(author, "Author cannot be null");
        this.isbn = Objects.requireNonNull(isbn, "ISBN cannot be null");
        this.language = "Italian";
    }

    public Book(String title, String author, String isbn, String publisher, int pages) {
        this(title, author, isbn);
        this.publisher = publisher;
        this.pages = pages;
    }

    @Override
    public String getMediaType() {
        return "BOOK";
    }

    @Override
    public String getMainAuthor() {
        return author;
    }

    @Override
    public String getIdentifier() {
        return isbn;
    }

    @Override
    public String toCsvString() {
        return String.format("%s,%s,%s,%s,%s,%b",
                getMediaType(), title, author, isbn, publisher, isAvailable);
    }

    // Getters and setters
    public String getAuthor() { return author; }
    public void setAuthor(String author) {
        this.author = Objects.requireNonNull(author, "Author cannot be null");
    }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) {
        this.isbn = Objects.requireNonNull(isbn, "ISBN cannot be null");
    }

    public String getPublisher() { return publisher; }
    public void setPublisher(String publisher) { this.publisher = publisher; }

    public int getPages() { return pages; }
    public void setPages(int pages) {
        if (pages < 0) throw new IllegalArgumentException("Pages cannot be negative");
        this.pages = pages;
    }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
}