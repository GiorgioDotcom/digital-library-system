package it.epicode.library.repository;

import it.epicode.library.model.media.*;
import it.epicode.library.factory.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

class MediaRepositoryTest {

    private MediaRepository repository;
    private Media testBook;
    private Media testAudioBook;

    @BeforeEach
    void setUp() {
        repository = new MediaRepository();

        testBook = MediaFactory.createMedia(MediaType.BOOK, Map.of(
                "title", "Test Book",
                "author", "Test Author",
                "isbn", "978-0123456789"
        ));

        testAudioBook = MediaFactory.createMedia(MediaType.AUDIOBOOK, Map.of(
                "title", "Test AudioBook",
                "author", "Test Author",
                "narrator", "Test Narrator",
                "duration", 300
        ));
    }

    @Test
    @DisplayName("Should save and find media by ID")
    void shouldSaveAndFindMediaById() {
        // When
        Media saved = repository.save(testBook);
        Optional<Media> found = repository.findById(testBook.getId());

        // Then
        assertEquals(testBook, saved);
        assertTrue(found.isPresent());
        assertEquals(testBook.getId(), found.get().getId());
    }

    @Test
    @DisplayName("Should find media by type")
    void shouldFindMediaByType() {
        // Given
        repository.save(testBook);
        repository.save(testAudioBook);

        // When
        List<Media> books = repository.findByType("BOOK");
        List<Media> audioBooks = repository.findByType("AUDIOBOOK");

        // Then
        assertEquals(1, books.size());
        assertEquals("BOOK", books.get(0).getMediaType());
        assertEquals(1, audioBooks.size());
        assertEquals("AUDIOBOOK", audioBooks.get(0).getMediaType());
    }

    @Test
    @DisplayName("Should find media by author")
    void shouldFindMediaByAuthor() {
        // Given
        repository.save(testBook);
        repository.save(testAudioBook);

        // When
        List<Media> mediaByAuthor = repository.findByAuthor("Test Author");

        // Then
        assertEquals(2, mediaByAuthor.size());
    }

    @Test
    @DisplayName("Should find available media")
    void shouldFindAvailableMedia() {
        // Given
        testBook.setAvailable(true);
        testAudioBook.setAvailable(false);
        repository.save(testBook);
        repository.save(testAudioBook);

        // When
        List<Media> available = repository.findAvailable();
        List<Media> unavailable = repository.findUnavailable();

        // Then
        assertEquals(1, available.size());
        assertTrue(available.get(0).isAvailable());
        assertEquals(1, unavailable.size());
        assertFalse(unavailable.get(0).isAvailable());
    }

    @Test
    @DisplayName("Should perform advanced search")
    void shouldPerformAdvancedSearch() {
        // Given
        repository.save(testBook);
        repository.save(testAudioBook);

        // When
        List<Media> searchResults = repository.search("Test", "BOOK", true);

        // Then
        assertEquals(1, searchResults.size());
        assertEquals("BOOK", searchResults.get(0).getMediaType());
    }

    @Test
    @DisplayName("Should delete media by ID")
    void shouldDeleteMediaById() {
        // Given
        repository.save(testBook);
        String id = testBook.getId();

        // When
        boolean deleted = repository.deleteById(id);
        Optional<Media> found = repository.findById(id);

        // Then
        assertTrue(deleted);
        assertFalse(found.isPresent());
    }

    @Test
    @DisplayName("Should get type statistics")
    void shouldGetTypeStatistics() {
        // Given
        repository.save(testBook);
        repository.save(testAudioBook);

        // When
        Map<String, Integer> stats = repository.getTypeStatistics();

        // Then
        assertEquals(1, stats.get("BOOK"));
        assertEquals(1, stats.get("AUDIOBOOK"));
    }

    @Test
    @DisplayName("Should handle concurrent access safely")
    void shouldHandleConcurrentAccessSafely() throws InterruptedException {
        // Given
        int threadCount = 10;
        int itemsPerThread = 10;

        // When
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < threadCount; i++) {
            final int threadIndex = i;
            Thread thread = new Thread(() -> {
                for (int j = 0; j < itemsPerThread; j++) {
                    Media media = MediaFactory.createMedia(MediaType.BOOK, Map.of(
                            "title", "Book " + threadIndex + "-" + j,
                            "author", "Author " + threadIndex,
                            "isbn", "978-" + threadIndex + String.format("%09d", j)
                    ));
                    repository.save(media);
                }
            });
            threads.add(thread);
            thread.start();
        }

        // Wait for all threads to complete
        for (Thread thread : threads) {
            thread.join();
        }

        // Then
        assertEquals(threadCount * itemsPerThread, repository.count());
    }
}