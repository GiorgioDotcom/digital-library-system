package it.epicode.library.iterator;

import it.epicode.library.model.media.Media;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.ArrayList;

public class FilteredIterator extends AbstractMediaIterator {
    private final String originalSource;
    private final List<Predicate<Media>> filterChain;

    public FilteredIterator(List<Media> mediaList, String source) {
        super(mediaList);
        this.originalSource = source;
        this.filterChain = new ArrayList<>();
    }

    /**
     * Adds a filter to the filter chain.
     */
    public FilteredIterator addFilter(Predicate<Media> filter) {
        filterChain.add(filter);
        applyFilterChain();
        return this;
    }

    /**
     * Removes a filter from the chain.
     */
    public FilteredIterator removeFilter(Predicate<Media> filter) {
        filterChain.remove(filter);
        applyFilterChain();
        return this;
    }

    /**
     * Clears all filters.
     */
    public FilteredIterator clearFilters() {
        filterChain.clear();
        this.mediaList = new ArrayList<>(originalList);
        reset();
        return this;
    }

    /**
     * Applies all filters in the chain.
     */
    private void applyFilterChain() {
        List<Media> filteredList = new ArrayList<>(originalList);

        for (Predicate<Media> filter : filterChain) {
            filteredList = filteredList.stream()
                    .filter(filter)
                    .collect(Collectors.toList());
        }

        this.mediaList = filteredList;
        this.currentPosition = Math.min(currentPosition, mediaList.size());
    }

    /**
     * Gets the number of active filters.
     */
    public int getFilterCount() {
        return filterChain.size();
    }

    /**
     * Gets the original source description.
     */
    public String getOriginalSource() {
        return originalSource;
    }

    /**
     * Gets filter chain information.
     */
    public String getFilterInfo() {
        if (filterChain.isEmpty()) {
            return "No filters applied";
        }
        return String.format("%d filters applied, %d/%d items match",
                filterChain.size(), mediaList.size(), originalList.size());
    }
}