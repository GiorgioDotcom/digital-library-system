package it.epicode.library.service;

import it.epicode.library.factory.MediaType;
import it.epicode.library.model.media.Media;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

class LibraryServiceTest {

    private LibraryService libraryService;

    @BeforeEach
    void setUp() {
        libraryService = new LibraryService();
    }

    @AfterEach
    void tearDown() {
        libraryService.shutdown();
    }

    @Test
    @DisplayName("Should add media using factory pattern")
    void shouldAddMediaUsingFactoryPattern() {
        // Given
        Map<String, Object> bookProperties = Map.of(
                "title", "Test Book",
                "author", "Test Author",
                "isbn", "978-0123456789"
        );

        // When
        Media savedMedia = libraryService.addMedia(MediaType.BOOK, bookProperties);

        // Then
        assertNotNull(savedMedia);
        assertEquals("Test Book", savedMedia.getTitle());
        assertEquals("Test Author", savedMedia.getMainAuthor());
        assertEquals("BOOK", savedMedia.getMediaType());
        assertTrue(savedMedia.isAvailable());
    }

    @Test
    @DisplayName("Should find media by ID")
    void shouldFindMediaById() {
        // Given
        Map<String, Object> properties = Map.of(
                "title", "Findable Book",
                "author", "Find Author",
                "isbn", "978-9876543210"
        );
        Media savedMedia = libraryService.addMedia(MediaType.BOOK, properties);

        // When
        Optional<Media> found = libraryService.findMediaById(savedMedia.getId());

        // Then
        assertTrue(found.isPresent());
        assertEquals(savedMedia.getId(), found.get().getId());
        assertEquals("Findable Book", found.get().getTitle());
    }

    @Test
    @DisplayName("Should return empty optional for non-existent media")
    void shouldReturnEmptyOptionalForNonExistentMedia() {
        // When
        Optional<Media> found = libraryService.findMediaById("non-existent-id");

        // Then
        assertTrue(found.isEmpty());
    }

    @Test
    @DisplayName("Should search media by query")
    void shouldSearchMediaByQuery() {
        // Given
        addTestMediaItems();

        // When
        List<Media> results = libraryService.searchMedia("Java");

        // Then
        assertFalse(results.isEmpty());
        assertTrue(results.stream().anyMatch(media ->
                media.getTitle().contains("Java") || media.getMainAuthor().contains("Java")));
    }

    @Test
    @DisplayName("Should perform advanced search with filters")
    void shouldPerformAdvancedSearchWithFilters() {
        // Given
        addTestMediaItems();

        // When
        List<Media> bookResults = libraryService.searchMedia("Test", "BOOK", true);
        List<Media> audioBookResults = libraryService.searchMedia("Test", "AUDIOBOOK", true);

        // Then
        assertTrue(bookResults.stream().allMatch(media -> media.getMediaType().equals("BOOK")));
        assertTrue(audioBookResults.stream().allMatch(media -> media.getMediaType().equals("AUDIOBOOK")));
    }

    @Test
    @DisplayName("Should get all media")
    void shouldGetAllMedia() {
        // Given
        addTestMediaItems();

        // When
        List<Media> allMedia = libraryService.getAllMedia();

        // Then
        assertEquals(3, allMedia.size()); // From addTestMediaItems()
    }

    @Test
    @DisplayName("Should get available media only")
    void shouldGetAvailableMediaOnly() {
        // Given
        addTestMediaItems();

        // Make one media unavailable
        List<Media> allMedia = libraryService.getAllMedia();
        String mediaId = allMedia.get(0).getId();
        libraryService.updateMediaAvailability(mediaId, false);

        // When
        List<Media> availableMedia = libraryService.getAvailableMedia();

        // Then
        assertEquals(2, availableMedia.size());
        assertTrue(availableMedia.stream().allMatch(Media::isAvailable));
    }

    @Test
    @DisplayName("Should get media by type")
    void shouldGetMediaByType() {
        // Given
        addTestMediaItems();

        // When
        List<Media> books = libraryService.getMediaByType("BOOK");
        List<Media> audioBooks = libraryService.getMediaByType("AUDIOBOOK");
        List<Media> eBooks = libraryService.getMediaByType("EBOOK");

        // Then
        assertEquals(1, books.size());
        assertEquals(1, audioBooks.size());
        assertEquals(1, eBooks.size());
        assertEquals("BOOK", books.get(0).getMediaType());
        assertEquals("AUDIOBOOK", audioBooks.get(0).getMediaType());
        assertEquals("EBOOK", eBooks.get(0).getMediaType());
    }

    @Test
    @DisplayName("Should update media availability")
    void shouldUpdateMediaAvailability() {
        // Given
        Media media = addSingleTestMedia();
        assertTrue(media.isAvailable());

        // When
        boolean updated = libraryService.updateMediaAvailability(media.getId(), false);

        // Then
        assertTrue(updated);
        Optional<Media> updatedMedia = libraryService.findMediaById(media.getId());
        assertTrue(updatedMedia.isPresent());
        assertFalse(updatedMedia.get().isAvailable());
    }

    @Test
    @DisplayName("Should return false when updating non-existent media")
    void shouldReturnFalseWhenUpdatingNonExistentMedia() {
        // When
        boolean updated = libraryService.updateMediaAvailability("non-existent-id", false);

        // Then
        assertFalse(updated);
    }

    @Test
    @DisplayName("Should delete media by ID")
    void shouldDeleteMediaById() {
        // Given
        Media media = addSingleTestMedia();

        // When
        boolean deleted = libraryService.deleteMedia(media.getId());

        // Then
        assertTrue(deleted);
        Optional<Media> found = libraryService.findMediaById(media.getId());
        assertTrue(found.isEmpty());
    }

    @Test
    @DisplayName("Should get media statistics")
    void shouldGetMediaStatistics() {
        // Given
        addTestMediaItems();

        // When
        Map<String, Integer> stats = libraryService.getMediaStatistics();

        // Then
        assertNotNull(stats);
        assertEquals(1, stats.get("BOOK"));
        assertEquals(1, stats.get("AUDIOBOOK"));
        assertEquals(1, stats.get("EBOOK"));
    }

    @Test
    @DisplayName("Should get availability statistics")
    void shouldGetAvailabilityStatistics() {
        // Given
        addTestMediaItems();

        // Make one unavailable
        List<Media> allMedia = libraryService.getAllMedia();
        libraryService.updateMediaAvailability(allMedia.get(0).getId(), false);

        // When
        Map<String, Integer> stats = libraryService.getAvailabilityStatistics();

        // Then
        assertEquals(2, stats.get("available"));
        assertEquals(1, stats.get("unavailable"));
        assertEquals(3, stats.get("total"));
    }

    @Test
    @DisplayName("Should perform async search")
    void shouldPerformAsyncSearch() throws ExecutionException, InterruptedException {
        // Given
        addTestMediaItems();

        // When
        CompletableFuture<List<Media>> future = libraryService.searchMediaAsync("Test");
        List<Media> results = future.get();

        // Then
        assertNotNull(results);
        assertFalse(results.isEmpty());
    }

    @Test
    @DisplayName("Should get repository statistics")
    void shouldGetRepositoryStatistics() {
        // Given
        addTestMediaItems();

        // When
        var stats = libraryService.getRepositoryStats();

        // Then
        assertNotNull(stats);
        assertEquals(3, stats.getEntityCount());
        assertTrue(stats.getRepositoryType().contains("MediaRepository"));
    }

    @Test
    @DisplayName("Should handle search with empty results")
    void shouldHandleSearchWithEmptyResults() {
        // When
        List<Media> results = libraryService.searchMedia("NonExistentQuery");

        // Then
        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    @DisplayName("Should handle concurrent operations safely")
    void shouldHandleConcurrentOperationsSafely() throws InterruptedException {
        // Given
        int threadCount = 5;
        int itemsPerThread = 10;
        List<Thread> threads = new ArrayList<>();

        // When
        for (int i = 0; i < threadCount; i++) {
            final int threadIndex = i;
            Thread thread = new Thread(() -> {
                for (int j = 0; j < itemsPerThread; j++) {
                    Map<String, Object> properties = Map.of(
                            "title", "Concurrent Book " + threadIndex + "-" + j,
                            "author", "Concurrent Author " + threadIndex,
                            "isbn", "978-" + threadIndex + String.format("%09d", j)
                    );
                    libraryService.addMedia(MediaType.BOOK, properties);
                }
            });
            threads.add(thread);
            thread.start();
        }

        // Wait for completion
        for (Thread thread : threads) {
            thread.join();
        }

        // Then
        List<Media> allMedia = libraryService.getAllMedia();
        assertEquals(threadCount * itemsPerThread, allMedia.size());
    }

    // Helper methods
    private void addTestMediaItems() {
        // Book
        libraryService.addMedia(MediaType.BOOK, Map.of(
                "title", "Test Java Book",
                "author", "Test Author",
                "isbn", "978-0111111111"
        ));

        // AudioBook
        libraryService.addMedia(MediaType.AUDIOBOOK, Map.of(
                "title", "Test AudioBook",
                "author", "Audio Author",
                "narrator", "Test Narrator",
                "duration", 300
        ));

        // EBook
        libraryService.addMedia(MediaType.EBOOK, Map.of(
                "title", "Test EBook",
                "author", "EBook Author",
                "format", "PDF",
                "fileSize", 10.5
        ));
    }

    private Media addSingleTestMedia() {
        return libraryService.addMedia(MediaType.BOOK, Map.of(
                "title", "Single Test Book",
                "author", "Single Author",
                "isbn", "978-0222222222"
        ));
    }
}