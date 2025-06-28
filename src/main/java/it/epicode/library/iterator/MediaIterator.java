package it.epicode.library.iterator;

import it.epicode.library.model.media.Media;
import java.util.Iterator;
import java.util.function.Predicate;

/**
 * Custom iterator interface for media objects.
 * Extends the standard Iterator interface with additional functionality.
 */
public interface MediaIterator extends Iterator<Media> {

    /**
     * Returns the current position of the iterator.
     */
    int getCurrentPosition();

    /**
     * Returns the total number of items that this iterator will traverse.
     */
    int getTotalItems();

    /**
     * Resets the iterator to the beginning.
     */
    void reset();

    /**
     * Checks if there are previous elements.
     */
    boolean hasPrevious();

    /**
     * Returns the previous element.
     */
    Media previous();

    /**
     * Skips the specified number of elements.
     */
    void skip(int count);

    /**
     * Sets a filter for the remaining iteration.
     */
    void setFilter(Predicate<Media> filter);

    /**
     * Gets the current filter.
     */
    Predicate<Media> getFilter();
}