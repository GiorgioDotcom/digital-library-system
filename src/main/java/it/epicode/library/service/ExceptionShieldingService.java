package it.epicode.library.service;

import it.epicode.library.model.exceptions.*;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.function.Function;
import java.util.concurrent.CompletableFuture;
import java.io.IOException;
import java.sql.SQLException;

public class ExceptionShieldingService {
    private static final Logger logger = Logger.getLogger(ExceptionShieldingService.class.getName());

    /**
     * Executes an operation with exception shielding, returning Optional result.
     */
    /**
     * Executes an operation with exception shielding, returning Optional result.
     */
    public static <T> Optional<T> executeWithShielding(
            Supplier<T> operation,
            String operationContext) {
        try {
            T result = operation.get();
            return Optional.ofNullable(result);
        } catch (LibraryException e) {
            // Library exceptions are already handled properly
            logger.log(Level.WARNING, "Library operation failed in context: " + operationContext, e);
            throw e;
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Invalid argument in context: " + operationContext, e);
            throw new ValidationException("input", "unknown", e.getMessage());
        } catch (RuntimeException e) {
            // Handle runtime exceptions that might wrap IO or SQL exceptions
            Throwable cause = e.getCause();
            if (cause instanceof IOException) {
                logger.log(Level.SEVERE, "IO error in context: " + operationContext, e);
                throw new DataPersistenceException("file operation", operationContext, e);
            } else {
                logger.log(Level.SEVERE, "Unexpected error in context: " + operationContext, e);
                throw new LibraryException(
                        "Unexpected system error: " + e.getMessage(),
                        e,
                        "An unexpected error occurred. Please try again or contact support.",
                        "LIB_SYS_001",
                        LibraryException.ErrorSeverity.CRITICAL
                );
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unexpected error in context: " + operationContext, e);
            throw new LibraryException(
                    "Unexpected system error: " + e.getMessage(),
                    e,
                    "An unexpected error occurred. Please try again or contact support.",
                    "LIB_SYS_001",
                    LibraryException.ErrorSeverity.CRITICAL
            );
        }
    }

    /**
     * Executes an operation with exception shielding and transformation.
     */
    public static <T, R> Optional<R> executeWithShielding(
            Supplier<T> operation,
            Function<T, R> transformer,
            String operationContext) {
        return executeWithShielding(() -> {
            T result = operation.get();
            return result != null ? transformer.apply(result) : null;
        }, operationContext);
    }

    /**
     * Executes a void operation with exception shielding.
     */
    public static void executeVoidWithShielding(
            Runnable operation,
            String operationContext) {
        executeWithShielding(() -> {
            operation.run();
            return true;
        }, operationContext);
    }

    /**
     * Executes an async operation with exception shielding.
     */
    public static <T> CompletableFuture<Optional<T>> executeAsyncWithShielding(
            Supplier<T> operation,
            String operationContext) {
        return CompletableFuture.supplyAsync(() ->
                executeWithShielding(operation, operationContext)
        );
    }

    /**
     * Creates an error response for user interface.
     */
    public static ErrorResponse createErrorResponse(Exception e) {
        if (e instanceof LibraryException le) {
            return new ErrorResponse(
                    le.getErrorCode(),
                    le.getUserFriendlyMessage(),
                    le.getSeverity().getDisplayName(),
                    false // Don't show stack trace for known errors
            );
        } else {
            logger.log(Level.SEVERE, "Unhandled exception", e);
            return new ErrorResponse(
                    "LIB_UNKNOWN",
                    "An unexpected error occurred. Please contact support.",
                    "Error",
                    false
            );
        }
    }

    /**
     * Error response class for API/UI consumption.
     */
    public static class ErrorResponse {
        private final String errorCode;
        private final String message;
        private final String severity;
        private final boolean showTechnicalDetails;
        private final long timestamp;

        public ErrorResponse(String errorCode, String message, String severity, boolean showTechnicalDetails) {
            this.errorCode = errorCode;
            this.message = message;
            this.severity = severity;
            this.showTechnicalDetails = showTechnicalDetails;
            this.timestamp = System.currentTimeMillis();
        }

        // Getters
        public String getErrorCode() { return errorCode; }
        public String getMessage() { return message; }
        public String getSeverity() { return severity; }
        public boolean isShowTechnicalDetails() { return showTechnicalDetails; }
        public long getTimestamp() { return timestamp; }

        @Override
        public String toString() {
            return String.format("[%s] %s: %s", errorCode, severity, message);
        }
    }
}