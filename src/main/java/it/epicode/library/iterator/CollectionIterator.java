package it.epicode.library.iterator;

import it.epicode.library.model.media.Media;
import it.epicode.library.model.structure.Collection;
import java.util.function.Predicate;
import java.util.Comparator;

public class CollectionIterator extends AbstractMediaIterator {
    private final Collection collection;
    private SortOrder sortOrder;

    public enum SortOrder {
        NONE,
        TITLE_ASC,
        TITLE_DESC,
        AUTHOR_ASC,
        AUTHOR_DESC,
        ACQUISITION_DATE_ASC,
        ACQUISITION_DATE_DESC,
        AVAILABILITY
    }

    public CollectionIterator(Collection collection) {
        super(collection.getAllMedia());
        this.collection = collection;
        this.sortOrder = SortOrder.NONE;
    }

    public CollectionIterator(Collection collection, Predicate<Media> filter) {
        super(collection.getAllMedia(), filter);
        this.collection = collection;
        this.sortOrder = SortOrder.NONE;
    }

    public CollectionIterator(Collection collection, Predicate<Media> filter, SortOrder sortOrder) {
        super(collection.getAllMedia(), filter);
        this.collection = collection;
        this.sortOrder = sortOrder;
        applySorting();
    }

    /**
     * Sets the sort order and re-sorts the media list.
     */
    public void setSortOrder(SortOrder sortOrder) {
        this.sortOrder = sortOrder;
        applySorting();
        reset(); // Reset position after sorting
    }

    /**
     * Applies sorting to the current media list.
     */
    private void applySorting() {
        if (sortOrder == SortOrder.NONE) {
            return;
        }

        Comparator<Media> comparator = getComparator(sortOrder);
        if (comparator != null) {
            mediaList.sort(comparator);
        }
    }

    /**
     * Gets the appropriate comparator for the sort order.
     */
    private Comparator<Media> getComparator(SortOrder order) {
        return switch (order) {
            case TITLE_ASC -> Comparator.comparing(Media::getTitle, String.CASE_INSENSITIVE_ORDER);
            case TITLE_DESC -> Comparator.comparing(Media::getTitle, String.CASE_INSENSITIVE_ORDER).reversed();
            case AUTHOR_ASC -> Comparator.comparing(Media::getMainAuthor, String.CASE_INSENSITIVE_ORDER);
            case AUTHOR_DESC -> Comparator.comparing(Media::getMainAuthor, String.CASE_INSENSITIVE_ORDER).reversed();
            case ACQUISITION_DATE_ASC -> Comparator.comparing(Media::getAcquisitionDate);
            case ACQUISITION_DATE_DESC -> Comparator.comparing(Media::getAcquisitionDate).reversed();
            case AVAILABILITY -> Comparator.comparing(Media::isAvailable).reversed(); // Available first
            default -> null;
        };
    }

    /**
     * Gets only available media.
     */
    public MediaIterator getAvailableIterator() {
        return new CollectionIterator(collection, Media::isAvailable, sortOrder);
    }

    /**
     * Gets media by type.
     */
    public MediaIterator getByTypeIterator(String mediaType) {
        Predicate<Media> typeFilter = media -> media.getMediaType().equals(mediaType);
        return new CollectionIterator(collection, typeFilter, sortOrder);
    }

    /**
     * Gets the collection this iterator is traversing.
     */
    public Collection getCollection() {
        return collection;
    }

    /**
     * Gets the current sort order.
     */
    public SortOrder getSortOrder() {
        return sortOrder;
    }
}