package it.epicode.library.repository;

import it.epicode.library.model.media.Media;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class MediaRepository extends AbstractRepository<Media> {

    // Additional indexes for efficient querying
    private final Map<String, Set<String>> typeIndex = new ConcurrentHashMap<>(); // mediaType -> Set<mediaId>
    private final Map<String, Set<String>> authorIndex = new ConcurrentHashMap<>(); // author -> Set<mediaId>
    private final Map<String, Set<String>> availabilityIndex = new ConcurrentHashMap<>(); // "available"/"unavailable" -> Set<mediaId>

    public MediaRepository() {
        super();
        // Initialize availability index
        availabilityIndex.put("available", ConcurrentHashMap.newKeySet());
        availabilityIndex.put("unavailable", ConcurrentHashMap.newKeySet());
    }

    @Override
    protected void updateSecondaryIndexes(Media entity, Media previous) {
        // Remove from old indexes if updating
        if (previous != null) {
            removeFromSecondaryIndexes(previous);
        }

        // Add to indexes
        String entityId = entity.getId();

        // Type index
        typeIndex.computeIfAbsent(entity.getMediaType(), k -> ConcurrentHashMap.newKeySet())
                .add(entityId);

        // Author index
        authorIndex.computeIfAbsent(entity.getMainAuthor().toLowerCase(), k -> ConcurrentHashMap.newKeySet())
                .add(entityId);

        // Availability index
        String availabilityKey = entity.isAvailable() ? "available" : "unavailable";
        availabilityIndex.get(availabilityKey).add(entityId);
    }

    @Override
    protected void removeFromSecondaryIndexes(Media entity) {
        String entityId = entity.getId();

        // Remove from type index
        Set<String> typeSet = typeIndex.get(entity.getMediaType());
        if (typeSet != null) {
            typeSet.remove(entityId);
            if (typeSet.isEmpty()) {
                typeIndex.remove(entity.getMediaType());
            }
        }

        // Remove from author index
        Set<String> authorSet = authorIndex.get(entity.getMainAuthor().toLowerCase());
        if (authorSet != null) {
            authorSet.remove(entityId);
            if (authorSet.isEmpty()) {
                authorIndex.remove(entity.getMainAuthor().toLowerCase());
            }
        }

        // Remove from availability indexes
        availabilityIndex.get("available").remove(entityId);
        availabilityIndex.get("unavailable").remove(entityId);
    }

    /**
     * Finds media by type using index for O(1) lookup.
     */
    public List<Media> findByType(String mediaType) {
        Set<String> mediaIds = typeIndex.getOrDefault(mediaType, Collections.emptySet());
        return findByIds(mediaIds);
    }

    /**
     * Finds media by author using index.
     */
    public List<Media> findByAuthor(String author) {
        Set<String> mediaIds = authorIndex.getOrDefault(author.toLowerCase(), Collections.emptySet());
        return findByIds(mediaIds);
    }

    /**
     * Finds available media using index.
     */
    public List<Media> findAvailable() {
        Set<String> mediaIds = availabilityIndex.get("available");
        return findByIds(mediaIds);
    }

    /**
     * Finds unavailable media using index.
     */
    public List<Media> findUnavailable() {
        Set<String> mediaIds = availabilityIndex.get("unavailable");
        return findByIds(mediaIds);
    }

    /**
     * Advanced search with multiple criteria using Stream API.
     */
    public List<Media> search(String query, String mediaType, Boolean available) {
        return findAll().stream()
                .filter(media -> {
                    // Text search in title and author
                    boolean matchesQuery = query == null || query.trim().isEmpty() ||
                            media.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                            media.getMainAuthor().toLowerCase().contains(query.toLowerCase());

                    // Type filter
                    boolean matchesType = mediaType == null ||
                            media.getMediaType().equals(mediaType);

                    // Availability filter
                    boolean matchesAvailability = available == null ||
                            media.isAvailable() == available;

                    return matchesQuery && matchesType && matchesAvailability;
                })
                .collect(Collectors.toList());
    }

    /**
     * Gets media type statistics using Collections.
     */
    public Map<String, Integer> getTypeStatistics() {
        return typeIndex.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().size()
                ));
    }

    /**
     * Gets availability statistics.
     */
    public Map<String, Integer> getAvailabilityStatistics() {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("available", availabilityIndex.get("available").size());
        stats.put("unavailable", availabilityIndex.get("unavailable").size());
        stats.put("total", storage.size());
        return stats;
    }
}