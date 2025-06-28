package it.epicode.library.model.exceptions;

public class MediaNotFoundException extends LibraryException {
    private final String mediaId;
    private final String searchCriteria;

    public MediaNotFoundException(String mediaId) {
        super(
                "Media not found with ID: " + mediaId,
                "The requested item could not be found in our catalog.",
                "LIB_001",
                ErrorSeverity.MEDIUM
        );
        this.mediaId = mediaId;
        this.searchCriteria = "ID: " + mediaId;
    }

    public MediaNotFoundException(String searchCriteria, String searchValue) {
        super(
                String.format("Media not found with %s: %s", searchCriteria, searchValue),
                "No items match your search criteria. Please try different keywords.",
                "LIB_002",
                ErrorSeverity.MEDIUM
        );
        this.mediaId = null;
        this.searchCriteria = searchCriteria + ": " + searchValue;
    }

    public String getMediaId() { return mediaId; }
    public String getSearchCriteria() { return searchCriteria; }
}