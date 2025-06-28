package it.epicode.library.model.exceptions;

public class LoanException extends LibraryException {
    private final String userId;
    private final String mediaId;
    private final LoanErrorType loanErrorType;

    public enum LoanErrorType {
        MEDIA_NOT_AVAILABLE("Media is currently unavailable"),
        USER_LOAN_LIMIT_EXCEEDED("User has reached maximum loan limit"),
        MEDIA_ALREADY_ON_LOAN("Media is already on loan to this user"),
        LOAN_OVERDUE("Cannot process - user has overdue items"),
        INVALID_LOAN_PERIOD("Invalid loan period specified");

        private final String description;

        LoanErrorType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    public LoanException(LoanErrorType errorType, String userId, String mediaId) {
        super(
                String.format("%s for user %s and media %s", errorType.getDescription(), userId, mediaId),
                errorType.getDescription(),
                "LIB_LOAN_" + errorType.ordinal(),
                ErrorSeverity.MEDIUM
        );
        this.userId = userId;
        this.mediaId = mediaId;
        this.loanErrorType = errorType;
    }

    public String getUserId() { return userId; }
    public String getMediaId() { return mediaId; }
    public LoanErrorType getLoanErrorType() { return loanErrorType; }
}