package it.epicode.library.iterator;

import it.epicode.library.model.media.Media;
import it.epicode.library.model.structure.LibraryComponent;
import java.time.LocalDate;
import java.util.function.Predicate;

public class IteratorFactory {

    /**
     * Creates common media filters.
     */
    public static class Filters {

        public static Predicate<Media> availableOnly() {
            return Media::isAvailable;
        }

        public static Predicate<Media> byType(String mediaType) {
            return media -> media.getMediaType().equals(mediaType);
        }

        public static Predicate<Media> byAuthor(String author) {
            return media -> media.getMainAuthor().toLowerCase().contains(author.toLowerCase());
        }

        public static Predicate<Media> byTitle(String title) {
            return media -> media.getTitle().toLowerCase().contains(title.toLowerCase());
        }

        public static Predicate<Media> acquiredAfter(LocalDate date) {
            return media -> media.getAcquisitionDate().isAfter(date);
        }

        public static Predicate<Media> acquiredBefore(LocalDate date) {
            return media -> media.getAcquisitionDate().isBefore(date);
        }

        public static Predicate<Media> inLocation(String location) {
            return media -> media.getLocation().toLowerCase().contains(location.toLowerCase());
        }

        /**
         * Combines multiple filters with AND logic.
         */
        public static Predicate<Media> and(Predicate<Media>... filters) {
            Predicate<Media> combined = media -> true;
            for (Predicate<Media> filter : filters) {
                combined = combined.and(filter);
            }
            return combined;
        }

        /**
         * Combines multiple filters with OR logic.
         */
        public static Predicate<Media> or(Predicate<Media>... filters) {
            Predicate<Media> combined = media -> false;
            for (Predicate<Media> filter : filters) {
                combined = combined.or(filter);
            }
            return combined;
        }
    }

    /**
     * Creates iterators for common use cases.
     */
    public static MediaIterator createAvailableMediaIterator(LibraryComponent component) {
        return new CompositeIterator(component, Filters.availableOnly());
    }

    public static MediaIterator createBookIterator(LibraryComponent component) {
        return new CompositeIterator(component, Filters.byType("BOOK"));
    }

    public static MediaIterator createRecentAcquisitionsIterator(LibraryComponent component, int days) {
        LocalDate cutoffDate = LocalDate.now().minusDays(days);
        return new CompositeIterator(component, Filters.acquiredAfter(cutoffDate));
    }

    public static MediaIterator createSearchIterator(LibraryComponent component, String query) {
        Predicate<Media> searchFilter = Filters.or(
                Filters.byTitle(query),
                Filters.byAuthor(query)
        );
        return new CompositeIterator(component, searchFilter);
    }
}