package it.epicode.library.service;

import it.epicode.library.model.structure.Library;
import it.epicode.library.repository.MediaRepository;
import it.epicode.library.model.media.Media;
import it.epicode.library.factory.MediaFactory;
import it.epicode.library.factory.MediaType;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;
import java.util.Map;

public class LibraryService {
    private static final Logger logger = LoggingService.getInstance().getLogger(LibraryService.class);

    private final MediaRepository mediaRepository;
    private final ExecutorService executorService;

    public LibraryService() {
        this.mediaRepository = new MediaRepository();
        this.executorService = Executors.newFixedThreadPool(4);
    }

    /**
     * Adds media using the Factory Pattern.
     */
    public Media addMedia(MediaType type, Map<String, Object> properties) {
        return ExceptionShieldingService.executeWithShielding(() -> {
            Media media = MediaFactory.createMedia(type, properties);
            return mediaRepository.save(media);
        }, "addMedia").orElse(null);
    }

    /**
     * Finds media by ID.
     */
    public Optional<Media> findMediaById(String id) {
        return mediaRepository.findById(id);
    }

    /**
     * Searches media with multiple criteria.
     */
    public List<Media> searchMedia(String query) {
        return mediaRepository.search(query, null, null);
    }

    /**
     * Advanced search with filters.
     */
    public List<Media> searchMedia(String query, String mediaType, Boolean available) {
        return mediaRepository.search(query, mediaType, available);
    }

    /**
     * Asynchronous media search.
     */
    public CompletableFuture<List<Media>> searchMediaAsync(String query) {
        return CompletableFuture.supplyAsync(() -> searchMedia(query), executorService);
    }

    /**
     * Gets all media.
     */
    public List<Media> getAllMedia() {
        return mediaRepository.findAll();
    }

    /**
     * Gets available media only.
     */
    public List<Media> getAvailableMedia() {
        return mediaRepository.findAvailable();
    }

    /**
     * Gets media by type.
     */
    public List<Media> getMediaByType(String mediaType) {
        return mediaRepository.findByType(mediaType);
    }

    /**
     * Gets media statistics.
     */
    public Map<String, Integer> getMediaStatistics() {
        return mediaRepository.getTypeStatistics();
    }

    /**
     * Gets availability statistics.
     */
    public Map<String, Integer> getAvailabilityStatistics() {
        return mediaRepository.getAvailabilityStatistics();
    }

    /**
     * Updates media availability.
     */
    public boolean updateMediaAvailability(String mediaId, boolean available) {
        return ExceptionShieldingService.executeWithShielding(() -> {
            Optional<Media> mediaOpt = mediaRepository.findById(mediaId);
            if (mediaOpt.isPresent()) {
                Media media = mediaOpt.get();
                media.setAvailable(available);
                mediaRepository.save(media);
                return true;
            }
            return false;
        }, "updateMediaAvailability").orElse(false);
    }

    /**
     * Deletes media by ID.
     */
    public boolean deleteMedia(String mediaId) {
        return mediaRepository.deleteById(mediaId);
    }

    /**
     * Gets repository statistics.
     */
    public MediaRepository.RepositoryStats getRepositoryStats() {
        return mediaRepository.getStats();
    }

    /**
     * Shuts down the service and cleanup resources.
     */
    public void shutdown() {
        executorService.shutdown();
    }
}