package it.epicode.library.iterator;

import it.epicode.library.model.media.Media;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class AbstractMediaIterator implements MediaIterator {
    protected List<Media> mediaList;
    protected int currentPosition;
    protected Predicate<Media> filter;
    protected final List<Media> originalList;

    protected AbstractMediaIterator(List<Media> mediaList) {
        this.originalList = new ArrayList<>(Objects.requireNonNull(mediaList, "Media list cannot be null"));
        this.mediaList = new ArrayList<>(this.originalList);
        this.currentPosition = 0;
        this.filter = null;
    }

    protected AbstractMediaIterator(List<Media> mediaList, Predicate<Media> filter) {
        this.originalList = new ArrayList<>(Objects.requireNonNull(mediaList, "Media list cannot be null"));
        this.filter = filter;
        this.mediaList = applyFilter(this.originalList, filter);
        this.currentPosition = 0;
    }

    @Override
    public boolean hasNext() {
        return currentPosition < mediaList.size();
    }

    @Override
    public Media next() {
        if (!hasNext()) {
            throw new NoSuchElementException("No more elements in iterator");
        }
        return mediaList.get(currentPosition++);
    }

    @Override
    public int getCurrentPosition() {
        return currentPosition;
    }

    @Override
    public int getTotalItems() {
        return mediaList.size();
    }

    @Override
    public void reset() {
        currentPosition = 0;
    }

    @Override
    public boolean hasPrevious() {
        return currentPosition > 0;
    }

    @Override
    public Media previous() {
        if (!hasPrevious()) {
            throw new NoSuchElementException("No previous elements in iterator");
        }
        return mediaList.get(--currentPosition);
    }

    @Override
    public void skip(int count) {
        if (count < 0) {
            throw new IllegalArgumentException("Skip count cannot be negative");
        }
        currentPosition = Math.min(currentPosition + count, mediaList.size());
    }

    @Override
    public void setFilter(Predicate<Media> filter) {
        this.filter = filter;
        this.mediaList = applyFilter(originalList, filter);
        this.currentPosition = Math.min(currentPosition, mediaList.size());
    }

    @Override
    public Predicate<Media> getFilter() {
        return filter;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Remove operation not supported");
    }

    /**
     * Applies the filter to the media list.
     */
    protected List<Media> applyFilter(List<Media> list, Predicate<Media> filter) {
        if (filter == null) {
            return new ArrayList<>(list);
        }

        return list.stream()
                .filter(filter)
                .collect(Collectors.toList());
    }

    /**
     * Gets iterator statistics.
     */
    public IteratorStats getStats() {
        return new IteratorStats(getCurrentPosition(), getTotalItems(),
                getFilter() != null, originalList.size());
    }

    /**
     * Iterator statistics inner class.
     */
    public static class IteratorStats {
        private final int currentPosition;
        private final int totalItems;
        private final boolean hasFilter;
        private final int originalSize;

        public IteratorStats(int currentPosition, int totalItems, boolean hasFilter, int originalSize) {
            this.currentPosition = currentPosition;
            this.totalItems = totalItems;
            this.hasFilter = hasFilter;
            this.originalSize = originalSize;
        }

        public int getCurrentPosition() { return currentPosition; }
        public int getTotalItems() { return totalItems; }
        public boolean hasFilter() { return hasFilter; }
        public int getOriginalSize() { return originalSize; }
        public int getRemainingItems() { return totalItems - currentPosition; }
        public double getProgress() {
            return totalItems == 0 ? 1.0 : (double) currentPosition / totalItems;
        }

        @Override
        public String toString() {
            return String.format("IteratorStats{position=%d/%d, progress=%.1f%%, filtered=%s}",
                    currentPosition, totalItems, getProgress() * 100, hasFilter);
        }
    }
}