package it.epicode.library.service;

import it.epicode.library.model.exceptions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Optional;
import java.io.IOException;

class ExceptionShieldingServiceTest {

    @BeforeEach
    void setUp() {
        // Reset any static state if needed
    }

    @Test
    @DisplayName("Should shield unexpected exceptions")
    void shouldShieldUnexpectedExceptions() {
        // Given
        RuntimeException unexpectedException = new RuntimeException("Unexpected error");

        // When
        ExceptionShieldingService.ErrorResponse response =
                ExceptionShieldingService.createErrorResponse(unexpectedException);

        // Then
        assertNotNull(response);
        assertEquals("LIB_UNKNOWN", response.getErrorCode());
        assertEquals("An unexpected error occurred. Please contact support.", response.getMessage());
        assertEquals("Error", response.getSeverity());
        assertFalse(response.isShowTechnicalDetails());
    }

    @Test
    @DisplayName("Should execute operation with shielding successfully")
    void shouldExecuteOperationWithShieldingSuccessfully() {
        // Given
        String expectedResult = "Success";

        // When
        Optional<String> result = ExceptionShieldingService.executeWithShielding(
                () -> expectedResult,
                "test-operation"
        );

        // Then
        assertTrue(result.isPresent());
        assertEquals(expectedResult, result.get());
    }

    @Test
    @DisplayName("Should handle null results gracefully")
    void shouldHandleNullResultsGracefully() {
        // When
        Optional<String> result = ExceptionShieldingService.executeWithShielding(
                () -> null,
                "null-operation"
        );

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should convert IllegalArgumentException to ValidationException")
    void shouldConvertIllegalArgumentExceptionToValidationException() {
        // Given
        IllegalArgumentException illegalArgException = new IllegalArgumentException("Invalid input");

        // When & Then
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            ExceptionShieldingService.executeWithShielding(() -> {
                throw illegalArgException;
            }, "validation-test");
        });

        assertEquals("LIB_VAL_001", exception.getErrorCode());
        assertTrue(exception.getMessage().contains("Invalid input"));
    }

    @Test
    @DisplayName("Should convert IOException to DataPersistenceException")
    void shouldConvertIOExceptionToDataPersistenceException() {
        // Given
        IOException ioException = new IOException("File not found");
        RuntimeException wrappedException = new RuntimeException(ioException);

        // When & Then
        DataPersistenceException exception = assertThrows(DataPersistenceException.class, () -> {
            ExceptionShieldingService.executeWithShielding(() -> {
                throw wrappedException;
            }, "io-test");
        });

        assertEquals("LIB_DATA_001", exception.getErrorCode());
        assertTrue(exception.getMessage().contains("io-test"));
    }

    @Test
    @DisplayName("Should execute void operation with shielding")
    void shouldExecuteVoidOperationWithShielding() {
        // Given
        boolean[] executed = {false};

        // When
        assertDoesNotThrow(() -> {
            ExceptionShieldingService.executeVoidWithShielding(() -> {
                executed[0] = true;
            }, "void-test");
        });

        // Then
        assertTrue(executed[0]);
    }

    @Test
    @DisplayName("Should preserve error codes from library exceptions")
    void shouldPreserveErrorCodesFromLibraryExceptions() {
        // Given
        LoanException loanException = new LoanException(
                LoanException.LoanErrorType.MEDIA_NOT_AVAILABLE,
                "user123",
                "media456"
        );

        // When
        ExceptionShieldingService.ErrorResponse response =
                ExceptionShieldingService.createErrorResponse(loanException);

        // Then
        assertEquals("LIB_LOAN_0", response.getErrorCode());
        assertEquals("Media is currently unavailable", response.getMessage());
    }

    @Test
    @DisplayName("Should include timestamp in error response")
    void shouldIncludeTimestampInErrorResponse() {
        // Given
        long startTime = System.currentTimeMillis();
        Exception testException = new RuntimeException("Test");

        // When
        ExceptionShieldingService.ErrorResponse response =
                ExceptionShieldingService.createErrorResponse(testException);

        // Then
        assertTrue(response.getTimestamp() >= startTime);
        assertTrue(response.getTimestamp() <= System.currentTimeMillis());
    }

    @Test
    @DisplayName("Should execute operation with transformation")
    void shouldExecuteOperationWithTransformation() {
        // Given
        String input = "test";

        // When - FIX: Specifica esplicitamente i tipi generici
        Optional<String> result = ExceptionShieldingService.<String, String>executeWithShielding(
                () -> input,
                (String value) -> value.toUpperCase(),
                "transform-test"
        );

        // Then
        assertTrue(result.isPresent());
        assertEquals("TEST", result.get());
    }

    @Test
    @DisplayName("Should handle transformation with null input")
    void shouldHandleTransformationWithNullInput() {
        // When - FIX: Specifica esplicitamente i tipi generici
        Optional<String> result = ExceptionShieldingService.<String, String>executeWithShielding(
                () -> null,
                (String value) -> value != null ? value.toUpperCase() : null,
                "null-transform-test"
        );

        // Then
        assertTrue(result.isEmpty());
    }
}