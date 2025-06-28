package it.epicode.library.integration;

import it.epicode.library.factory.MediaFactory;
import it.epicode.library.factory.MediaType;
import it.epicode.library.iterator.MediaIterator;
import it.epicode.library.model.media.Media;
import it.epicode.library.model.structure.*;
import it.epicode.library.model.structure.Collection;
import it.epicode.library.repository.MediaRepository;
import it.epicode.library.repository.Repository;
import it.epicode.library.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;
import java.nio.file.Path;
import java.util.*;

/**
 * Integration tests that verify the complete system works end-to-end.
 * Tests all patterns and technologies working together.
 */
class LibraryIntegrationTest {

    @TempDir
    Path tempDir;

    private Library library;
    private LibraryService libraryService;
    private DataPersistenceService persistenceService;

    @BeforeEach
    void setUp() {
        library = new Library("Integration Test Library", "Test Address");
        libraryService = new LibraryService();
        persistenceService = new DataPersistenceService(tempDir.toString());
    }

    @Test
    @DisplayName("Should demonstrate all design patterns working together")
    void shouldDemonstrateAllDesignPatternsWorkingTogether() {
        // === FACTORY PATTERN ===
        // Create different media types
        List<Media> factoryCreatedMedia = Arrays.asList(
                MediaFactory.createMedia(MediaType.BOOK, Map.of(
                        "title", "Pattern Book", "author", "Pattern Author", "isbn", "978-1111111111")),
                MediaFactory.createMedia(MediaType.AUDIOBOOK, Map.of(
                        "title", "Pattern Audio", "author", "Audio Author", "narrator", "Narrator", "duration", 300)),
                MediaFactory.createMedia(MediaType.EBOOK, Map.of(
                        "title", "Pattern EBook", "author", "EBook Author", "format", "EPUB", "fileSize", 5.0)),
                MediaFactory.createMedia(MediaType.DVD, Map.of(
                        "title", "Pattern Movie", "director", "Movie Director", "runtime", 120))
        );

        // === COMPOSITE PATTERN ===
        // Build hierarchical structure
        Section mainSection = library.addSection("Main", "Main section");
        Collection subCollection1 = mainSection.addCollection("Collection 1", "First collection");
        Collection subCollection2 = mainSection.addCollection("Collection 2", "Second collection");

        // Add media to different levels
        subCollection1.addMedia(factoryCreatedMedia.get(0));
        subCollection1.addMedia(factoryCreatedMedia.get(1));
        subCollection2.addMedia(factoryCreatedMedia.get(2));
        subCollection2.addMedia(factoryCreatedMedia.get(3));

        // Verify composite operations
        assertEquals(4, library.getMediaCount());
        assertEquals(4, mainSection.getMediaCount());
        assertEquals(2, subCollection1.getMediaCount());
        assertEquals(2, subCollection2.getMediaCount());

        // === ITERATOR PATTERN ===
        // Test different iteration strategies
        MediaIterator libraryIterator = library.iterator();
        int totalCount = 0;
        while (libraryIterator.hasNext()) {
            Media media = libraryIterator.next();
            assertNotNull(media);
            totalCount++;
        }
        assertEquals(4, totalCount);

        // Test filtered iteration
        MediaIterator bookIterator = library.iterator(media -> media.getMediaType().equals("BOOK"));
        int bookCount = 0;
        while (bookIterator.hasNext()) {
            Media book = bookIterator.next();
            assertEquals("BOOK", book.getMediaType());
            bookCount++;
        }
        assertEquals(1, bookCount);

        // === EXCEPTION SHIELDING ===
        // Test graceful error handling
        ExceptionShieldingService.ErrorResponse errorResponse =
                ExceptionShieldingService.createErrorResponse(new RuntimeException("Test error"));
        assertNotNull(errorResponse);
        assertEquals("LIB_UNKNOWN", errorResponse.getErrorCode());

        // Test shielded operations
        Optional<String> result = ExceptionShieldingService.executeWithShielding(
                () -> "Success",
                "test-operation"
        );
        assertTrue(result.isPresent());
        assertEquals("Success", result.get());

        // === INTEGRATION VERIFICATION ===
        // All patterns should work seamlessly together
        Library.LibraryStatistics stats = library.getStatistics();
        assertEquals(4, stats.getTotalMedia());
        assertEquals(1, stats.getTotalSections());
        assertEquals(2, stats.getTotalCollections());

        Map<String, Integer> typeCount = stats.getMediaTypeCount();
        assertEquals(1, typeCount.get("BOOK"));
        assertEquals(1, typeCount.get("AUDIOBOOK"));
        assertEquals(1, typeCount.get("EBOOK"));
        assertEquals(1, typeCount.get("DVD"));
    }

    @Test
    @DisplayName("Should demonstrate advanced technologies integration")
    void shouldDemonstrateAdvancedTechnologiesIntegration() {
        // === GENERICS AND COLLECTIONS ===
        MediaRepository repository = new MediaRepository();

        // Type-safe operations
        Media testMedia = MediaFactory.createMedia(MediaType.BOOK, Map.of(
                "title", "Generics Test", "author", "Test Author", "isbn", "978-2222222222"));

        Media saved = repository.save(testMedia);
        assertEquals(testMedia.getId(), saved.getId());

        // Collections framework usage
        List<Media> allFromRepo = repository.findAll();
        Set<String> ids = repository.getAllIds();
        Map<String, Media> mediaMap = repository.findAllAsMap();

        assertEquals(1, allFromRepo.size());
        assertEquals(1, ids.size());
        assertEquals(1, mediaMap.size());

        // === STREAM API AND LAMBDAS ===
        List<Media> testMediaList = Arrays.asList(
                MediaFactory.createMedia(MediaType.BOOK, Map.of("title", "Stream Book 1", "author", "Author 1", "isbn", "978-3333333333")),
                MediaFactory.createMedia(MediaType.BOOK, Map.of("title", "Stream Book 2", "author", "Author 2", "isbn", "978-4444444444")),
                MediaFactory.createMedia(MediaType.AUDIOBOOK, Map.of("title", "Stream Audio", "author", "Audio Author", "narrator", "Narrator", "duration", 200))
        );

        repository.saveAll(testMediaList);

        // Stream operations
        long bookCount = repository.findAll().stream()
                .filter(media -> media.getMediaType().equals("BOOK"))
                .count();
        assertEquals(3, bookCount); // 1 + 2 new books

        List<String> bookTitles = repository.findAll().stream()
                .filter(media -> media.getMediaType().equals("BOOK"))
                .map(Media::getTitle)
                .sorted()
                .collect(java.util.stream.Collectors.toList());
        assertEquals(3, bookTitles.size());

        // === MULTITHREADING ===
        // Test concurrent operations
        int threadCount = 3;
        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < threadCount; i++) {
            final int threadIndex = i;
            Thread thread = new Thread(() -> {
                for (int j = 0; j < 5; j++) {
                    Media threadMedia = MediaFactory.createMedia(MediaType.BOOK, Map.of(
                            "title", "Thread " + threadIndex + " Book " + j,
                            "author", "Thread Author " + threadIndex,
                            "isbn", "978-" + threadIndex + j + "0000000"
                    ));
                    repository.save(threadMedia);
                }
            });
            threads.add(thread);
            thread.start();
        }

        assertDoesNotThrow(() -> {
            for (Thread thread : threads) {
                thread.join();
            }
        });

        // Verify thread-safe operations
        assertEquals(19, repository.count()); // 4 + 15 from threads

        // === I/O OPERATIONS ===
        Section ioSection = library.addSection("I/O Test", "I/O testing");
        Collection ioCollection = ioSection.addCollection("I/O Collection", "I/O test collection");
        ioCollection.addMedia(testMedia);

        assertDoesNotThrow(() -> {
            // Test library persistence
            persistenceService.saveLibrary(library);
            Optional<Library> loaded = persistenceService.loadLibrary();
            assertTrue(loaded.isPresent());

            // Test CSV operations
            List<Media> allMedia = repository.findAll();
            persistenceService.exportCatalogToCsv(allMedia);
            List<Map<String, String>> csvData = persistenceService.importCatalogFromCsv();
            assertFalse(csvData.isEmpty());

            // Test configuration
            Properties config = new Properties();
            config.setProperty("test.integration", "true");
            config.setProperty("media.count", String.valueOf(allMedia.size()));
            persistenceService.saveConfiguration(config);

            Properties loadedConfig = persistenceService.loadConfiguration();
            assertEquals("true", loadedConfig.getProperty("test.integration"));

            // Test backup
            persistenceService.createBackup();
            List<Path> backups = persistenceService.listBackups();
            assertFalse(backups.isEmpty());
        });

        // === LOGGING ===
        LoggingService logging = LoggingService.getInstance();
        assertDoesNotThrow(() -> {
            logging.logLibraryEvent("INTEGRATION_COMPLETE", "SYSTEM", "integration-test",
                    Map.of("totalMedia", repository.count(), "sections", library.getSections().size()));

            logging.logPerformance("INTEGRATION_TEST", 500L,
                    Map.of("operationsPerformed", 50, "threadsUsed", threadCount));

            logging.logSecurityEvent("TEST_SECURITY", "test-user", "INTEGRATION_TEST", true);
        });
    }

    @Test
    @DisplayName("Should handle system boundaries and edge cases")
    void shouldHandleSystemBoundariesAndEdgeCases() {
        // Test empty system state
        assertEquals(0, library.getMediaCount());
        assertEquals(0, library.getSections().size());

        MediaIterator emptyIterator = library.iterator();
        assertFalse(emptyIterator.hasNext());
        assertEquals(0, emptyIterator.getTotalItems());

        // Test maximum capacity scenarios
        Section largeSection = library.addSection("Large", "Large section");
        Collection largeCollection = largeSection.addCollection("Large Collection", "Large");

        // Add many items to test scalability
        for (int i = 0; i < 100; i++) {
            Media media = MediaFactory.createMedia(MediaType.BOOK, Map.of(
                    "title", "Book " + i,
                    "author", "Author " + (i % 10),
                    "isbn", "978-" + String.format("%010d", i)
            ));
            largeCollection.addMedia(media);
        }

        assertEquals(100, library.getMediaCount());
        assertEquals(100, largeCollection.getMediaCount());

        // Test iterator performance with large dataset
        MediaIterator largeIterator = library.iterator();
        int count = 0;
        while (largeIterator.hasNext()) {
            assertNotNull(largeIterator.next());
            count++;
        }
        assertEquals(100, count);

        // Test filtered iteration on large dataset
        MediaIterator filteredIterator = library.iterator(media ->
                media.getMainAuthor().equals("Author 5"));

        List<Media> filtered = new ArrayList<>();
        while (filteredIterator.hasNext()) {
            filtered.add(filteredIterator.next());
        }
        assertEquals(10, filtered.size()); // Every 10th item

        // Test repository with large dataset
        MediaRepository repo = new MediaRepository();
        List<Media> allMedia = library.getAllMedia();
        repo.saveAll(allMedia);

        Map<String, Integer> typeStats = repo.getTypeStatistics();
        assertEquals(100, typeStats.get("BOOK"));

        // Test search performance
        List<Media> searchResults = repo.search("Book 5", null, null);
        assertFalse(searchResults.isEmpty());

        // Test persistence with large dataset
        assertDoesNotThrow(() -> {
            persistenceService.saveLibrary(library);
            Optional<Library> loaded = persistenceService.loadLibrary();
            assertTrue(loaded.isPresent());
            assertEquals(100, loaded.get().getMediaCount());
        });

        // Test error recovery
        assertDoesNotThrow(() -> {
            // Try invalid operations
            Optional<Media> notFound = repo.findById("invalid-id");
            assertTrue(notFound.isEmpty());

            // System should remain stable
            assertEquals(100, repo.count());
            assertEquals(100, library.getMediaCount());
        });
    }
}