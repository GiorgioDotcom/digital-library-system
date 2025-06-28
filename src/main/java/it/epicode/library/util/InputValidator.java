package it.epicode.library.util;

import it.epicode.library.model.exceptions.ValidationException;
import java.util.regex.Pattern;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class InputValidator {

    // Common validation patterns
    private static final Pattern ISBN_PATTERN = Pattern.compile(
            "^(?:ISBN(?:-1[03])?:? )?(?=[0-9X]{10}$|(?=(?:[0-9]+[- ]){3})[- 0-9X]{13}$|97[89][0-9]{10}$|(?=(?:[0-9]+[- ]){4})[- 0-9]{17}$).*"
    );

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );

    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^[+]?[0-9\\s\\-\\(\\)]{8,15}$"
    );

    private static final Pattern SAFE_TEXT_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9\\s\\-_.,!?'\"\\(\\)]+$"
    );

    /**
     * Sanitizes string input to prevent injection attacks.
     */
    public static String sanitizeInput(String input) {
        if (input == null) {
            return "";
        }

        return input.trim()
                .replaceAll("[<>\"'&\\\\]", "") // Remove dangerous characters
                .replaceAll("\\s+", " ") // Normalize whitespace
                .substring(0, Math.min(input.length(), 500)); // Limit length
    }

    /**
     * Validates and sanitizes string input.
     */
    public static String validateAndSanitizeString(String input, String fieldName, int maxLength) {
        if (input == null || input.trim().isEmpty()) {
            throw new ValidationException(fieldName, input, "Cannot be empty");
        }

        String sanitized = sanitizeInput(input);

        if (sanitized.length() > maxLength) {
            throw new ValidationException(fieldName, input, "Cannot exceed " + maxLength + " characters");
        }

        return sanitized;
    }

    /**
     * Validates and sanitizes a title.
     */
    public static String validateTitle(String title) {
        String sanitized = validateAndSanitizeString(title, "title", 200);

        if (!SAFE_TEXT_PATTERN.matcher(sanitized).matches()) {
            throw new ValidationException("title", title, "Contains invalid characters");
        }

        return sanitized;
    }

    /**
     * Validates and sanitizes an author name.
     */
    public static String validateAuthor(String author) {
        String sanitized = validateAndSanitizeString(author, "author", 100);

        if (!Pattern.matches("^[a-zA-Z\\s\\-'.,]+$", sanitized)) {
            throw new ValidationException("author", author, "Author name contains invalid characters");
        }

        return sanitized;
    }

    /**
     * Validates ISBN format.
     */
    public static String validateISBN(String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) {
            throw new ValidationException("ISBN", isbn, "ISBN cannot be empty");
        }

        String cleanISBN = isbn.replaceAll("[\\s\\-]", "");

        if (!ISBN_PATTERN.matcher(cleanISBN).matches()) {
            throw new ValidationException("ISBN", isbn, "Invalid ISBN format");
        }

        return cleanISBN;
    }

    /**
     * Validates email format.
     */
    public static String validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new ValidationException("email", email, "Email cannot be empty");
        }

        String cleanEmail = email.trim().toLowerCase();

        if (!EMAIL_PATTERN.matcher(cleanEmail).matches()) {
            throw new ValidationException("email", email, "Invalid email format");
        }

        return cleanEmail;
    }

    /**
     * Validates positive integer.
     */
    public static int validatePositiveInteger(Integer value, String fieldName) {
        if (value == null) {
            throw new ValidationException(fieldName, null, "Value cannot be null");
        }

        if (value <= 0) {
            throw new ValidationException(fieldName, value, "Must be a positive number");
        }

        return value;
    }

    /**
     * Validates non-negative integer.
     */
    public static int validateNonNegativeInteger(Integer value, String fieldName) {
        if (value == null) {
            throw new ValidationException(fieldName, null, "Value cannot be null");
        }

        if (value < 0) {
            throw new ValidationException(fieldName, value, "Cannot be negative");
        }

        return value;
    }
}