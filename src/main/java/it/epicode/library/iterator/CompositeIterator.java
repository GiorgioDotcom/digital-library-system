package it.epicode.library.iterator;

import it.epicode.library.model.media.Media;
import it.epicode.library.model.structure.LibraryComponent;
import java.util.function.Predicate;
import java.util.List;
import java.util.ArrayList;

public class CompositeIterator extends AbstractMediaIterator {
    private final LibraryComponent component;
    private final boolean depthFirst;

    public CompositeIterator(LibraryComponent component) {
        super(collectAllMedia(component, true));
        this.component = component;
        this.depthFirst = true;
    }

    public CompositeIterator(LibraryComponent component, Predicate<Media> filter) {
        super(collectAllMedia(component, true), filter);
        this.component = component;
        this.depthFirst = true;
    }

    public CompositeIterator(LibraryComponent component, boolean depthFirst) {
        super(collectAllMedia(component, depthFirst));
        this.component = component;
        this.depthFirst = depthFirst;
    }

    public CompositeIterator(LibraryComponent component, Predicate<Media> filter, boolean depthFirst) {
        super(collectAllMedia(component, depthFirst), filter);
        this.component = component;
        this.depthFirst = depthFirst;
    }

    /**
     * Collects all media from the component hierarchy.
     */
    private static List<Media> collectAllMedia(LibraryComponent component, boolean depthFirst) {
        List<Media> allMedia = new ArrayList<>();

        if (depthFirst) {
            collectDepthFirst(component, allMedia);
        } else {
            collectBreadthFirst(component, allMedia);
        }

        return allMedia;
    }

    /**
     * Depth-first collection of media.
     */
    private static void collectDepthFirst(LibraryComponent component, List<Media> result) {
        // Add direct media first
        result.addAll(component.getDirectMedia());

        // Then recursively collect from children
        for (LibraryComponent child : component.getChildren()) {
            collectDepthFirst(child, result);
        }
    }

    /**
     * Breadth-first collection of media.
     */
    private static void collectBreadthFirst(LibraryComponent component, List<Media> result) {
        List<LibraryComponent> queue = new ArrayList<>();
        queue.add(component);

        while (!queue.isEmpty()) {
            LibraryComponent current = queue.remove(0);

            // Add direct media
            result.addAll(current.getDirectMedia());

            // Add children to queue
            queue.addAll(current.getChildren());
        }
    }

    /**
     * Gets the component this iterator is traversing.
     */
    public LibraryComponent getComponent() {
        return component;
    }

    /**
     * Checks if this iterator uses depth-first traversal.
     */
    public boolean isDepthFirst() {
        return depthFirst;
    }
}