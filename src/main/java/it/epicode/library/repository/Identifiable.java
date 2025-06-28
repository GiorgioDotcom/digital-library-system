package it.epicode.library.repository;

/**
 * Interface that entities must implement to be used in repositories.
 * Provides a common contract for entity identification.
 */
public interface Identifiable {
    /**
     * Returns the unique identifier of this entity.
     * @return the unique ID as a String
     */
    String getId();
}