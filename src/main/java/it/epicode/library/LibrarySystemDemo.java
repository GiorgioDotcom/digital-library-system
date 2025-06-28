package it.epicode.library;

import it.epicode.library.model.exceptions.MediaNotFoundException;
import it.epicode.library.model.structure.*;
import it.epicode.library.model.media.*;
import it.epicode.library.factory.*;
import it.epicode.library.model.structure.Collection;
import it.epicode.library.service.*;
import it.epicode.library.repository.*;
import it.epicode.library.iterator.*;
import it.epicode.library.util.InputValidator;
import java.util.*;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Main application class demonstrating the Digital Library System.
 * Showcases all design patterns and core technologies.
 */
public class LibrarySystemDemo {
    private static final Logger logger = LoggingService.getInstance().getLogger(LibrarySystemDemo.class);

    private final LibraryService libraryService;
    private final DataPersistenceService persistenceService;
    private final Scanner scanner;

    public LibrarySystemDemo() {
        this.libraryService = new LibraryService();
        this.persistenceService = new DataPersistenceService("data");
        this.scanner = new Scanner(System.in);

        logger.info("Digital Library System initialized");
    }

    /**
     * Main demo flow showcasing all patterns and technologies.
     */
    public void runDemo() {
        System.out.println("🏛️ Welcome to Digital Library Management System");
        System.out.println("=".repeat(50));

        // Load or create library
        Library library = loadOrCreateLibrary();

        // Setup demo data if library is empty
        if (library.getMediaCount() == 0) {
            setupDemoData(library);
        }

        // Interactive demo loop
        boolean running = true;
        while (running) {
            displayMenu();
            int choice = getIntInput("Enter your choice: ");

            switch (choice) {
                case 1 -> demonstrateFactoryPattern(library);
                case 2 -> demonstrateCompositePattern(library);
                case 3 -> demonstrateIteratorPattern(library);
                case 4 -> demonstrateExceptionShielding();
                case 5 -> demonstrateCollectionsAndGenerics();
                case 6 -> demonstrateJavaIO(library);
                case 7 -> demonstrateLogging();
                case 8 -> demonstrateAdvancedFeatures(library);
                case 9 -> showLibraryStatistics(library);
                case 0 -> {
                    saveAndExit(library);
                    running = false;
                }
                default -> System.out.println("❌ Invalid choice. Please try again.");
            }
        }
    }

    private void displayMenu() {
        System.out.println("\n📚 Digital Library System - Demo Menu");
        System.out.println("-".repeat(40));
        System.out.println("1. 🏭 Factory Pattern Demo");
        System.out.println("2. 🌳 Composite Pattern Demo");
        System.out.println("3. 🔄 Iterator Pattern Demo");
        System.out.println("4. 🛡️ Exception Shielding Demo");
        System.out.println("5. 📦 Collections & Generics Demo");
        System.out.println("6. 💾 Java I/O Demo");
        System.out.println("7. 📋 Logging Demo");
        System.out.println("8. 🚀 Advanced Features Demo");
        System.out.println("9. 📊 Library Statistics");
        System.out.println("0. 💾 Save & Exit");
        System.out.println("-".repeat(40));
    }

    /**
     * Demonstrates Factory Pattern with different media types.
     */
    private void demonstrateFactoryPattern(Library library) {
        System.out.println("\n🏭 FACTORY PATTERN DEMONSTRATION");
        System.out.println("=".repeat(35));

        try {
            // Create different types of media using Factory
            Map<String, Object> bookProps = Map.of(
                    "title", "Design Patterns",
                    "author", "Gang of Four",
                    "isbn", "978-0201633610",
                    "pages", 395
            );

            Map<String, Object> audioBookProps = Map.of(
                    "title", "Clean Code",
                    "author", "Robert Martin",
                    "narrator", "Mike Smith",
                    "duration", 720
            );

            Map<String, Object> eBookProps = Map.of(
                    "title", "Effective Java",
                    "author", "Joshua Bloch",
                    "format", "PDF",
                    "fileSize", 15.5
            );

            Map<String, Object> dvdProps = Map.of(
                    "title", "The Matrix",
                    "director", "The Wachowski Brothers",
                    "runtime", 136
            );

            // Demonstrate Factory Pattern
            System.out.println("Creating media using MediaFactory...\n");

            Media book = MediaFactory.createMedia(MediaType.BOOK, bookProps);
            Media audioBook = MediaFactory.createMedia(MediaType.AUDIOBOOK, audioBookProps);
            Media eBook = MediaFactory.createMedia(MediaType.EBOOK, eBookProps);
            Media dvd = MediaFactory.createMedia(MediaType.DVD, dvdProps);

            System.out.println("✅ Created: " + book);
            System.out.println("✅ Created: " + audioBook);
            System.out.println("✅ Created: " + eBook);
            System.out.println("✅ Created: " + dvd);

            // Add to library
            Section programmingSection = library.findSection("Programming")
                    .orElse(library.addSection("Programming", "Programming books and resources"));

            Collection designPatternsCollection = programmingSection.findCollection("Design Patterns")
                    .orElse(programmingSection.addCollection("Design Patterns", "Books about design patterns"));

            designPatternsCollection.addMedia(book);
            designPatternsCollection.addMedia(audioBook);
            designPatternsCollection.addMedia(eBook);
            designPatternsCollection.addMedia(dvd);

            System.out.println("\n📚 All media added to Programming > Design Patterns collection");

        } catch (Exception e) {
            System.out.println("❌ Error in Factory Pattern demo: " + e.getMessage());
            logger.log(Level.WARNING, "Factory pattern demo error", e);
        }
    }

    /**
     * Demonstrates Composite Pattern with hierarchical structure.
     */
    private void demonstrateCompositePattern(Library library) {
        System.out.println("\n🌳 COMPOSITE PATTERN DEMONSTRATION");
        System.out.println("=".repeat(35));

        // Display current structure
        System.out.println("Current Library Structure:");
        library.displayStructure(0);

        // Demonstrate uniform operations
        System.out.println("\n📊 Composite Operations:");
        System.out.println("Total media in library: " + library.getMediaCount());

        for (Section section : library.getSections()) {
            System.out.println("Section '" + section.getName() + "': " + section.getMediaCount() + " media");

            for (Collection collection : section.getCollections()) {
                System.out.println("  Collection '" + collection.getName() + "': " + collection.getMediaCount() + " media");
            }
        }

        // Demonstrate adding new hierarchical structure
        System.out.println("\n➕ Adding new hierarchical structure...");

        Section fictionSection = library.addSection("Fiction", "Fiction literature");
        Collection sciFiCollection = fictionSection.addCollection("Science Fiction", "Sci-fi books and media");
        Collection fantasyCollection = fictionSection.addCollection("Fantasy", "Fantasy literature");

        // Add some sample media
        Map<String, Object> sciFiBook = Map.of(
                "title", "Dune",
                "author", "Frank Herbert",
                "isbn", "978-0441013593"
        );

        sciFiCollection.addMedia(MediaFactory.createMedia(MediaType.BOOK, sciFiBook));

        System.out.println("✅ Added Fiction section with Science Fiction and Fantasy collections");

        // Show updated structure
        System.out.println("\nUpdated Library Structure:");
        library.displayStructure(0);
    }

    /**
     * Demonstrates Iterator Pattern with different navigation strategies.
     */
    private void demonstrateIteratorPattern(Library library) {
        System.out.println("\n🔄 ITERATOR PATTERN DEMONSTRATION");
        System.out.println("=".repeat(35));

        // Get a collection with media
        Section programmingSection = library.findSection("Programming").orElse(null);
        if (programmingSection == null || programmingSection.getMediaCount() == 0) {
            System.out.println("❌ No media found for iterator demonstration");
            return;
        }

        Collection designPatterns = programmingSection.getCollections().stream()
                .filter(c -> c.getMediaCount() > 0)
                .findFirst()
                .orElse(null);

        if (designPatterns == null) {
            System.out.println("❌ No collection with media found");
            return;
        }

        // Basic iteration
        System.out.println("🔍 Basic Iterator:");
        MediaIterator basicIterator = designPatterns.iterator();
        int count = 0;
        while (basicIterator.hasNext() && count < 3) {
            Media media = basicIterator.next();
            System.out.println("  " + (count + 1) + ". " + media.getTitle() + " (" + media.getMediaType() + ")");
            count++;
        }

        // Filtered iteration
        System.out.println("\n📚 Books Only (Filtered Iterator):");
        MediaIterator bookIterator = designPatterns.iterator(media -> media.getMediaType().equals("BOOK"));
        count = 0;
        while (bookIterator.hasNext() && count < 3) {
            Media media = bookIterator.next();
            System.out.println("  " + (count + 1) + ". " + media.getTitle() + " by " + media.getMainAuthor());
            count++;
        }

        // Sorted iteration
        System.out.println("\n📝 Sorted by Title (CollectionIterator):");
        CollectionIterator sortedIterator = new CollectionIterator(
                designPatterns, null, CollectionIterator.SortOrder.TITLE_ASC
        );
        count = 0;
        while (sortedIterator.hasNext() && count < 3) {
            Media media = sortedIterator.next();
            System.out.println("  " + (count + 1) + ". " + media.getTitle());
            count++;
        }

        // Composite iteration (entire library)
        System.out.println("\n🏛️ Library-wide Iterator (Composite):");
        MediaIterator libraryIterator = library.iterator();
        System.out.println("Total items to iterate: " + libraryIterator.getTotalItems());

        // Show iterator statistics
        System.out.println("\n📊 Iterator Statistics:");
        System.out.println("Current position: " + libraryIterator.getCurrentPosition());
        System.out.println("Remaining items: " + (libraryIterator.getTotalItems() - libraryIterator.getCurrentPosition()));
    }

    /**
     * Demonstrates Exception Shielding with various error scenarios.
     */
    private void demonstrateExceptionShielding() {
        System.out.println("\n🛡️ EXCEPTION SHIELDING DEMONSTRATION");
        System.out.println("=".repeat(35));

        // Test input validation
        System.out.println("🔍 Input Validation Tests:");

        try {
            String validTitle = InputValidator.validateTitle("Clean Code: A Handbook");
            System.out.println("✅ Valid title: '" + validTitle + "'");
        } catch (Exception e) {
            System.out.println("❌ Title validation failed: " + e.getMessage());
        }

        try {
            InputValidator.validateTitle("<script>alert('XSS')</script>");
            System.out.println("❌ Should have failed XSS validation!");
        } catch (Exception e) {
            System.out.println("✅ XSS attempt blocked: " + e.getMessage());
        }

        try {
            String validISBN = InputValidator.validateISBN("978-0132350884");
            System.out.println("✅ Valid ISBN: " + validISBN);
        } catch (Exception e) {
            System.out.println("❌ ISBN validation failed: " + e.getMessage());
        }

        try {
            InputValidator.validateISBN("invalid-isbn");
            System.out.println("❌ Should have failed ISBN validation!");
        } catch (Exception e) {
            System.out.println("✅ Invalid ISBN blocked: " + e.getMessage());
        }

        // Test exception shielding in repository
        System.out.println("\n🔒 Exception Shielding in Repository:");
        MediaRepository repository = new MediaRepository();

        // This should work fine
        Optional<Media> result1 = repository.findById("valid-id");
        System.out.println("✅ Safe repository call returned: " + (result1.isPresent() ? "Found" : "Not found"));

        // Demonstrate error response creation
        System.out.println("\n📋 Error Response Creation:");
        Exception testException = new MediaNotFoundException("test-id");
        ExceptionShieldingService.ErrorResponse response = ExceptionShieldingService.createErrorResponse(testException);
        System.out.println("Error Code: " + response.getErrorCode());
        System.out.println("User Message: " + response.getMessage());
        System.out.println("Severity: " + response.getSeverity());
        System.out.println("Show Technical: " + response.isShowTechnicalDetails());
    }

    /**
     * Demonstrates Collections Framework and Generics usage.
     */
    private void demonstrateCollectionsAndGenerics() {
        System.out.println("\n📦 COLLECTIONS & GENERICS DEMONSTRATION");
        System.out.println("=".repeat(40));

        MediaRepository repository = new MediaRepository();

        // Add sample data to repository
        List<Media> sampleMedia = Arrays.asList(
                MediaFactory.createMedia(MediaType.BOOK, Map.of(
                        "title", "Java: The Complete Reference",
                        "author", "Herbert Schildt",
                        "isbn", "978-1260440232"
                )),
                MediaFactory.createMedia(MediaType.AUDIOBOOK, Map.of(
                        "title", "Thinking in Java",
                        "author", "Bruce Eckel",
                        "narrator", "John Doe",
                        "duration", 480
                )),
                MediaFactory.createMedia(MediaType.EBOOK, Map.of(
                        "title", "Spring in Action",
                        "author", "Craig Walls",
                        "format", "EPUB",
                        "fileSize", 12.3
                ))
        );

        // Demonstrate generic repository operations
        System.out.println("💾 Generic Repository Operations:");
        List<Media> saved = repository.saveAll(sampleMedia);
        System.out.println("✅ Saved " + saved.size() + " media items");

        // Demonstrate Collections Framework features
        System.out.println("\n📚 Collections Framework Features:");

        // Type-based queries using indexes
        List<Media> books = repository.findByType("BOOK");
        System.out.println("📖 Books found: " + books.size());

        List<Media> audioBooks = repository.findByType("AUDIOBOOK");
        System.out.println("🎧 AudioBooks found: " + audioBooks.size());

        // Generic search with predicate
        List<Media> javaBooks = repository.findAll(media ->
                media.getTitle().toLowerCase().contains("java"));
        System.out.println("☕ Java-related media: " + javaBooks.size());

        // Demonstrate advanced collections usage
        System.out.println("\n🔍 Advanced Collections Usage:");

        // Type statistics using Map
        Map<String, Integer> typeStats = repository.getTypeStatistics();
        System.out.println("📊 Type Statistics:");
        typeStats.forEach((type, count) ->
                System.out.println("  " + type + ": " + count + " items"));

        // Availability statistics
        Map<String, Integer> availabilityStats = repository.getAvailabilityStatistics();
        System.out.println("📈 Availability Statistics:");
        availabilityStats.forEach((status, count) ->
                System.out.println("  " + status + ": " + count + " items"));

        // Demonstrate Set operations
        Set<String> allIds = repository.getAllIds();
        System.out.println("🆔 Total unique IDs: " + allIds.size());
    }

    /**
     * Demonstrates Java I/O operations.
     */
    private void demonstrateJavaIO(Library library) {
        System.out.println("\n💾 JAVA I/O DEMONSTRATION");
        System.out.println("=".repeat(25));

        try {
            // Demonstrate library persistence
            System.out.println("💿 Binary Serialization:");
            persistenceService.saveLibrary(library);
            System.out.println("✅ Library saved successfully");

            Optional<Library> loaded = persistenceService.loadLibrary();
            if (loaded.isPresent()) {
                System.out.println("✅ Library loaded successfully");
                System.out.println("📊 Loaded " + loaded.get().getMediaCount() + " media items");
            }

            // Demonstrate CSV export/import
            System.out.println("\n📄 CSV Import/Export:");
            List<Media> allMedia = library.getAllMedia();
            persistenceService.exportCatalogToCsv(allMedia);
            System.out.println("✅ Exported " + allMedia.size() + " items to CSV");

            List<Map<String, String>> importedRecords = persistenceService.importCatalogFromCsv();
            System.out.println("✅ Imported " + importedRecords.size() + " records from CSV");

            // Demonstrate configuration management
            System.out.println("\n⚙️ Configuration Management:");
            Properties config = persistenceService.loadConfiguration();
            System.out.println("📋 Loaded configuration with " + config.size() + " properties");

            config.setProperty("demo.timestamp", String.valueOf(System.currentTimeMillis()));
            persistenceService.saveConfiguration(config);
            System.out.println("✅ Configuration updated and saved");

            // Demonstrate backup creation
            System.out.println("\n🔄 Backup Operations:");
            persistenceService.createBackup();
            System.out.println("✅ Backup created");

            List<java.nio.file.Path> backups = persistenceService.listBackups();
            System.out.println("📦 Available backups: " + backups.size());

        } catch (Exception e) {
            System.out.println("❌ I/O Error: " + e.getMessage());
            logger.log(Level.WARNING, "I/O demonstration error", e);
        }
    }

    /**
     * Demonstrates logging capabilities.
     */
    private void demonstrateLogging() {
        System.out.println("\n📋 LOGGING DEMONSTRATION");
        System.out.println("=".repeat(25));

        LoggingService loggingService = LoggingService.getInstance();
        Logger demoLogger = loggingService.getLogger(LibrarySystemDemo.class);

        // Different log levels
        System.out.println("📝 Generating logs at different levels...");

        demoLogger.info("This is an INFO level message");
        demoLogger.warning("This is a WARNING level message");
        demoLogger.severe("This is a SEVERE level message");

        // Structured logging
        System.out.println("🏗️ Structured logging examples...");

        Map<String, Object> eventDetails = Map.of(
                "userId", "user123",
                "action", "ADD_MEDIA",
                "mediaType", "BOOK",
                "success", true
        );

        loggingService.logLibraryEvent("MEDIA_ADDED", "BOOK", "book-456", eventDetails);

        // Performance logging
        long startTime = System.currentTimeMillis();
        try {
            Thread.sleep(100); // Simulate operation
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        long duration = System.currentTimeMillis() - startTime;

        Map<String, Object> perfMetrics = Map.of(
                "itemCount", 150,
                "cacheHitRatio", 0.85
        );

        loggingService.logPerformance("SEARCH_OPERATION", duration, perfMetrics);

        // Security logging
        loggingService.logSecurityEvent("LOGIN_ATTEMPT", "admin", "AUTHENTICATE", true);
        loggingService.logSecurityEvent("INVALID_INPUT", "user456", "ADD_MEDIA", false);

        System.out.println("✅ Logging demonstrations completed - check log files in 'logs' directory");
    }

    /**
     * Demonstrates advanced features like Stream API.
     */
    private void demonstrateAdvancedFeatures(Library library) {
        System.out.println("\n🚀 ADVANCED FEATURES DEMONSTRATION");
        System.out.println("=".repeat(40));

        // Stream API demonstration
        System.out.println("🌊 Stream API & Lambda Expressions:");

        List<Media> allMedia = library.getAllMedia();

        // Complex stream operations
        Map<String, Long> mediaTypeCount = allMedia.stream()
                .collect(java.util.stream.Collectors.groupingBy(Media::getMediaType, java.util.stream.Collectors.counting()));

        System.out.println("📊 Media count by type:");
        mediaTypeCount.forEach((type, count) ->
                System.out.println("  " + type + ": " + count));

        // Find most recent acquisitions
        List<Media> recentMedia = allMedia.stream()
                .filter(media -> media.getAcquisitionDate().isAfter(java.time.LocalDate.now().minusMonths(6)))
                .sorted(Comparator.comparing(Media::getAcquisitionDate).reversed())
                .limit(3)
                .collect(java.util.stream.Collectors.toList());

        System.out.println("\n📅 Recent acquisitions (last 6 months):");
        recentMedia.forEach(media ->
                System.out.println("  " + media.getTitle() + " (" + media.getAcquisitionDate() + ")"));

        // Optional demonstration
        System.out.println("\n🔍 Optional Usage:");

        Optional<Media> firstBook = allMedia.stream()
                .filter(media -> media.getMediaType().equals("BOOK"))
                .findFirst();

        firstBook.ifPresentOrElse(
                book -> System.out.println("📖 First book found: " + book.getTitle()),
                () -> System.out.println("📖 No books found in library")
        );

        // Functional interfaces demonstration
        System.out.println("\n🎯 Functional Interfaces:");

        java.util.function.Predicate<Media> isAvailable = Media::isAvailable;
        java.util.function.Predicate<Media> isBook = media -> media.getMediaType().equals("BOOK");
        java.util.function.Predicate<Media> availableBooks = isAvailable.and(isBook);

        long availableBookCount = allMedia.stream()
                .filter(availableBooks)
                .count();

        System.out.println("📚 Available books: " + availableBookCount);
    }

    /**
     * Shows comprehensive library statistics.
     */
    private void showLibraryStatistics(Library library) {
        System.out.println("\n📊 LIBRARY STATISTICS");
        System.out.println("=".repeat(25));

        Library.LibraryStatistics stats = library.getStatistics();

        System.out.println("🏛️ Library Overview:");
        System.out.println("  Total Media: " + stats.getTotalMedia());
        System.out.println("  Available: " + stats.getAvailableMedia());
        System.out.println("  Unavailable: " + stats.getUnavailableMedia());
        System.out.println("  Sections: " + stats.getTotalSections());
        System.out.println("  Collections: " + stats.getTotalCollections());

        System.out.println("\n📈 Media Distribution:");
        stats.getMediaTypeCount().forEach((type, count) ->
                System.out.println("  " + type + ": " + count + " items"));

        // Section breakdown
        System.out.println("\n📂 Section Breakdown:");
        for (Section section : library.getSections()) {
            System.out.println("  " + section.getName() + ": " + section.getMediaCount() + " items");
            for (Collection collection : section.getCollections()) {
                System.out.println("    └─ " + collection.getName() + ": " + collection.getMediaCount() + " items");
            }
        }
    }

    // Helper methods

    private Library loadOrCreateLibrary() {
        System.out.println("🔄 Loading library data...");

        Optional<Library> loaded = persistenceService.loadLibrary();
        if (loaded.isPresent()) {
            System.out.println("✅ Existing library loaded successfully");
            return loaded.get();
        } else {
            System.out.println("🆕 Creating new library...");
            return new Library("Digital Library System", "Main Campus Library");
        }
    }

    private void setupDemoData(Library library) {
        System.out.println("🔧 Setting up demo data...");

        // Create sections
        Section programmingSection = library.addSection("Programming", "Programming books and resources");
        Section fictionSection = library.addSection("Fiction", "Fiction literature");
        Section referenceSection = library.addSection("Reference", "Reference materials");

        // Create collections
        Collection javaCollection = programmingSection.addCollection("Java", "Java programming resources");
        Collection designPatternsCollection = programmingSection.addCollection("Design Patterns", "Software design patterns");
        Collection sciFiCollection = fictionSection.addCollection("Science Fiction", "Science fiction literature");

        // Add sample media
        javaCollection.addMedia(MediaFactory.createMedia(MediaType.BOOK, Map.of(
                "title", "Effective Java",
                "author", "Joshua Bloch",
                "isbn", "978-0134685991"
        )));

        javaCollection.addMedia(MediaFactory.createMedia(MediaType.AUDIOBOOK, Map.of(
                "title", "Java Concurrency in Practice",
                "author", "Brian Goetz",
                "narrator", "Technical Reader",
                "duration", 600
        )));

        designPatternsCollection.addMedia(MediaFactory.createMedia(MediaType.EBOOK, Map.of(
                "title", "Head First Design Patterns",
                "author", "Eric Freeman",
                "format", "PDF",
                "fileSize", 25.7
        )));

        sciFiCollection.addMedia(MediaFactory.createMedia(MediaType.DVD, Map.of(
                "title", "Blade Runner 2049",
                "director", "Denis Villeneuve",
                "runtime", 164
        )));

        System.out.println("✅ Demo data setup complete");
    }

    private void saveAndExit(Library library) {
        System.out.println("\n💾 Saving library data...");

        try {
            persistenceService.saveLibrary(library);
            persistenceService.createBackup();
            System.out.println("✅ Library data saved successfully");
        } catch (Exception e) {
            System.out.println("❌ Error saving library: " + e.getMessage());
            logger.log(Level.SEVERE, "Error saving library on exit", e);
        }

        System.out.println("👋 Thank you for using Digital Library System!");
    }

    private int getIntInput(String prompt) {
        System.out.print(prompt);
        try {
            return scanner.nextInt();
        } catch (Exception e) {
            scanner.nextLine(); // Clear invalid input
            return -1;
        }
    }

    public void cleanup() {
        if (scanner != null) {
            scanner.close();
        }
        libraryService.shutdown();
        System.out.println("🔧 Cleanup completed");
    }
}