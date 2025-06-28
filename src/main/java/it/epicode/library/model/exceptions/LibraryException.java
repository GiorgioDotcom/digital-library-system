package it.epicode.library.model.exceptions;

/**
 * Base exception class for the library system.
 * Provides structured error handling with user-friendly messages.
 */
public class LibraryException extends RuntimeException {
    private final String userFriendlyMessage;
    private final String errorCode;
    private final ErrorSeverity severity;

    public enum ErrorSeverity {
        LOW("Info"),
        MEDIUM("Warning"),
        HIGH("Error"),
        CRITICAL("Critical");

        private final String displayName;

        ErrorSeverity(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public LibraryException(String message) {
        super(message);
        this.userFriendlyMessage = message;
        this.errorCode = "LIB_000";
        this.severity = ErrorSeverity.MEDIUM;
    }

    public LibraryException(String message, Throwable cause) {
        super(message, cause);
        this.userFriendlyMessage = message;
        this.errorCode = "LIB_000";
        this.severity = ErrorSeverity.HIGH;
    }

    public LibraryException(String message, String userFriendlyMessage, String errorCode, ErrorSeverity severity) {
        super(message);
        this.userFriendlyMessage = userFriendlyMessage;
        this.errorCode = errorCode;
        this.severity = severity;
    }

    public LibraryException(String message, Throwable cause, String userFriendlyMessage, String errorCode, ErrorSeverity severity) {
        super(message, cause);
        this.userFriendlyMessage = userFriendlyMessage;
        this.errorCode = errorCode;
        this.severity = severity;
    }

    public String getUserFriendlyMessage() {
        return userFriendlyMessage;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public ErrorSeverity getSeverity() {
        return severity;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s - %s", errorCode, severity.getDisplayName(), userFriendlyMessage);
    }
}