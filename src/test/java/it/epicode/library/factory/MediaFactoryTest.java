package it.epicode.library.factory;

import it.epicode.library.model.media.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Map;

class MediaFactoryTest {

    @Test
    @DisplayName("Should create Book with valid properties")
    void shouldCreateBookWithValidProperties() {
        // Given
        Map<String, Object> properties = Map.of(
                "title", "Clean Code",
                "author", "Robert Martin",
                "isbn", "978-0132350884"
        );

        // When
        Media media = MediaFactory.createMedia(MediaType.BOOK, properties);

        // Then
        assertInstanceOf(Book.class, media);
        assertEquals("Clean Code", media.getTitle());
        assertEquals("Robert Martin", media.getMainAuthor());
        assertEquals("BOOK", media.getMediaType());
    }

    @Test
    @DisplayName("Should create AudioBook with valid properties")
    void shouldCreateAudioBookWithValidProperties() {
        // Given
        Map<String, Object> properties = Map.of(
                "title", "Thinking in Java",
                "author", "Bruce Eckel",
                "narrator", "John Doe",
                "duration", 480
        );

        // When
        Media media = MediaFactory.createMedia(MediaType.AUDIOBOOK, properties);

        // Then
        assertInstanceOf(AudioBook.class, media);
        assertEquals("Thinking in Java", media.getTitle());
        assertEquals("Bruce Eckel", media.getMainAuthor());
        assertEquals("AUDIOBOOK", media.getMediaType());
    }

    @Test
    @DisplayName("Should create EBook with valid properties")
    void shouldCreateEBookWithValidProperties() {
        // Given
        Map<String, Object> properties = Map.of(
                "title", "Spring in Action",
                "author", "Craig Walls",
                "format", "PDF",
                "fileSize", 15.5
        );

        // When
        Media media = MediaFactory.createMedia(MediaType.EBOOK, properties);

        // Then
        assertInstanceOf(EBook.class, media);
        assertEquals("Spring in Action", media.getTitle());
        assertEquals("Craig Walls", media.getMainAuthor());
        assertEquals("EBOOK", media.getMediaType());
    }

    @Test
    @DisplayName("Should create DVD with valid properties")
    void shouldCreateDVDWithValidProperties() {
        // Given
        Map<String, Object> properties = Map.of(
                "title", "The Matrix",
                "director", "Wachowski Brothers",
                "runtime", 136
        );

        // When
        Media media = MediaFactory.createMedia(MediaType.DVD, properties);

        // Then
        assertInstanceOf(DVD.class, media);
        assertEquals("The Matrix", media.getTitle());
        assertEquals("Wachowski Brothers", media.getMainAuthor());
        assertEquals("DVD", media.getMediaType());
    }

    @Test
    @DisplayName("Should throw exception for null media type")
    void shouldThrowExceptionForNullMediaType() {
        // Given
        Map<String, Object> properties = Map.of("title", "Test");

        // When & Then
        assertThrows(NullPointerException.class, () ->
                MediaFactory.createMedia(null, properties));
    }

    @Test
    @DisplayName("Should throw exception for null properties")
    void shouldThrowExceptionForNullProperties() {
        // When & Then
        assertThrows(NullPointerException.class, () ->
                MediaFactory.createMedia(MediaType.BOOK, null));
    }

    @Test
    @DisplayName("Should throw exception for missing required property")
    void shouldThrowExceptionForMissingRequiredProperty() {
        // Given
        Map<String, Object> properties = Map.of(
                "title", "Test Book"
                // Missing required "author" and "isbn"
        );

        // When & Then
        assertThrows(IllegalArgumentException.class, () ->
                MediaFactory.createMedia(MediaType.BOOK, properties));
    }
}