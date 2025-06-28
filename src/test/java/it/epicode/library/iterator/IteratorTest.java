package it.epicode.library.iterator;

import it.epicode.library.model.media.*;
import it.epicode.library.model.structure.*;
import it.epicode.library.factory.*;
import it.epicode.library.model.structure.Collection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
import java.util.function.Predicate;

class IteratorTest {

    private Library library;
    private Section programmingSection;
    private Collection javaCollection;
    private Media book1, book2, audioBook1;

    @BeforeEach
    void setUp() {
        // Create library structure
        library = new Library("Test Library", "Test Address");
        programmingSection = library.addSection("Programming", "Programming resources");
        javaCollection = programmingSection.addCollection("Java", "Java programming");

        // Create test media
        book1 = MediaFactory.createMedia(MediaType.BOOK, Map.of(
                "title", "Effective Java",
                "author", "Joshua Bloch",
                "isbn", "978-0134685991"
        ));

        book2 = MediaFactory.createMedia(MediaType.BOOK, Map.of(
                "title", "Clean Code",
                "author", "Robert Martin",
                "isbn", "978-0132350884"
        ));

        audioBook1 = MediaFactory.createMedia(MediaType.AUDIOBOOK, Map.of(
                "title", "Java Concurrency",
                "author", "Brian Goetz",
                "narrator", "Tech Reader",
                "duration", 600
        ));

        // Add media to collection
        javaCollection.addMedia(book1);
        javaCollection.addMedia(book2);
        javaCollection.addMedia(audioBook1);
    }

    @Test
    @DisplayName("Should iterate through collection media")
    void shouldIterateThroughCollectionMedia() {
        // When
        MediaIterator iterator = javaCollection.iterator();
        List<Media> iteratedMedia = new ArrayList<>();

        while (iterator.hasNext()) {
            iteratedMedia.add(iterator.next());
        }

        // Then
        assertEquals(3, iteratedMedia.size());
        assertEquals(3, iterator.getTotalItems());
        assertEquals(3, iterator.getCurrentPosition());
    }

    @Test
    @DisplayName("Should filter media during iteration")
    void shouldFilterMediaDuringIteration() {
        // Given
        Predicate<Media> bookFilter = media -> media.getMediaType().equals("BOOK");

        // When
        MediaIterator iterator = javaCollection.iterator(bookFilter);
        List<Media> books = new ArrayList<>();

        while (iterator.hasNext()) {
            books.add(iterator.next());
        }

        // Then
        assertEquals(2, books.size());
        assertEquals(2, iterator.getTotalItems());
        assertTrue(books.stream().allMatch(media -> media.getMediaType().equals("BOOK")));
    }

    @Test
    @DisplayName("Should support bidirectional iteration")
    void shouldSupportBidirectionalIteration() {
        // Given
        MediaIterator iterator = javaCollection.iterator();

        // When - Move forward
        Media first = iterator.next();
        Media second = iterator.next();

        // Then - Move backward
        assertTrue(iterator.hasPrevious());
        Media previousMedia = iterator.previous();
        assertEquals(second.getId(), previousMedia.getId());
        assertEquals(1, iterator.getCurrentPosition());
    }

    @Test
    @DisplayName("Should reset iterator position")
    void shouldResetIteratorPosition() {
        // Given
        MediaIterator iterator = javaCollection.iterator();
        iterator.next();
        iterator.next();
        assertEquals(2, iterator.getCurrentPosition());

        // When
        iterator.reset();

        // Then
        assertEquals(0, iterator.getCurrentPosition());
        assertTrue(iterator.hasNext());
    }

    @Test
    @DisplayName("Should skip elements")
    void shouldSkipElements() {
        // Given
        MediaIterator iterator = javaCollection.iterator();

        // When
        iterator.skip(2);

        // Then
        assertEquals(2, iterator.getCurrentPosition());
        assertTrue(iterator.hasNext()); // Should have one more element

        Media nextMedia = iterator.next();
        assertEquals(3, iterator.getCurrentPosition());
        assertFalse(iterator.hasNext());
    }

    @Test
    @DisplayName("Should iterate through composite structure")
    void shouldIterateThroughCompositeStructure() {
        // Given - Add another collection
        Collection pythonCollection = programmingSection.addCollection("Python", "Python programming");
        Media pythonBook = MediaFactory.createMedia(MediaType.BOOK, Map.of(
                "title", "Python Guide",
                "author", "Python Author",
                "isbn", "978-0123456789"
        ));
        pythonCollection.addMedia(pythonBook);

        // When
        MediaIterator libraryIterator = library.iterator();
        List<Media> allMedia = new ArrayList<>();

        while (libraryIterator.hasNext()) {
            allMedia.add(libraryIterator.next());
        }

        // Then
        assertEquals(4, allMedia.size()); // 3 from Java + 1 from Python
        assertEquals(4, libraryIterator.getTotalItems());
    }

    @Test
    @DisplayName("Should handle empty collection iteration")
    void shouldHandleEmptyCollectionIteration() {
        // Given
        Collection emptyCollection = programmingSection.addCollection("Empty", "Empty collection");

        // When
        MediaIterator iterator = emptyCollection.iterator();

        // Then
        assertFalse(iterator.hasNext());
        assertEquals(0, iterator.getTotalItems());
        assertEquals(0, iterator.getCurrentPosition());
    }

    @Test
    @DisplayName("Should throw exception when accessing beyond bounds")
    void shouldThrowExceptionWhenAccessingBeyondBounds() {
        // Given
        MediaIterator iterator = javaCollection.iterator();

        // Consume all elements
        while (iterator.hasNext()) {
            iterator.next();
        }

        // When & Then
        assertThrows(NoSuchElementException.class, iterator::next);
    }

    @Test
    @DisplayName("Should throw exception when accessing previous at start")
    void shouldThrowExceptionWhenAccessingPreviousAtStart() {
        // Given
        MediaIterator iterator = javaCollection.iterator();

        // When & Then
        assertFalse(iterator.hasPrevious());
        assertThrows(NoSuchElementException.class, iterator::previous);
    }

    @Test
    @DisplayName("Should support dynamic filter changes")
    void shouldSupportDynamicFilterChanges() {
        // Given
        MediaIterator iterator = javaCollection.iterator();
        assertEquals(3, iterator.getTotalItems());

        // When
        iterator.setFilter(media -> media.getMediaType().equals("BOOK"));

        // Then
        assertEquals(2, iterator.getTotalItems());

        List<Media> filteredMedia = new ArrayList<>();
        while (iterator.hasNext()) {
            filteredMedia.add(iterator.next());
        }

        assertEquals(2, filteredMedia.size());
        assertTrue(filteredMedia.stream().allMatch(media -> media.getMediaType().equals("BOOK")));
    }

    @Test
    @DisplayName("Should create sorted collection iterator")
    void shouldCreateSortedCollectionIterator() {
        // Given
        CollectionIterator iterator = new CollectionIterator(
                javaCollection,
                null,
                CollectionIterator.SortOrder.TITLE_ASC
        );

        // When
        List<String> titles = new ArrayList<>();
        while (iterator.hasNext()) {
            titles.add(iterator.next().getTitle());
        }

        // Then
        assertEquals(Arrays.asList("Clean Code", "Effective Java", "Java Concurrency"), titles);
    }

    @Test
    @DisplayName("Should get iterator statistics")
    void shouldGetIteratorStatistics() {
        // Given
        MediaIterator iterator = javaCollection.iterator();
        iterator.next();
        iterator.next();

        // When
        if (iterator instanceof AbstractMediaIterator abstractIterator) {
            AbstractMediaIterator.IteratorStats stats = abstractIterator.getStats();

            // Then
            assertEquals(2, stats.getCurrentPosition());
            assertEquals(3, stats.getTotalItems());
            assertEquals(1, stats.getRemainingItems());
            assertEquals(2.0/3.0, stats.getProgress(), 0.01);
        }
    }
}