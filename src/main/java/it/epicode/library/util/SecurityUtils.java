package it.epicode.library.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class SecurityUtils {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final String ALGORITHM = "SHA-256";

    /**
     * Generates a secure random string for IDs or tokens.
     */
    public static String generateSecureToken(int length) {
        byte[] bytes = new byte[length];
        SECURE_RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    /**
     * Hashes a password with salt.
     */
    public static String hashPassword(String password, String salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance(ALGORITHM);
            digest.update(salt.getBytes());
            byte[] hashedBytes = digest.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hashedBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Hashing algorithm not available", e);
        }
    }

    /**
     * Generates a random salt.
     */
    public static String generateSalt() {
        byte[] salt = new byte[16];
        SECURE_RANDOM.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    /**
     * Sanitizes user input to prevent XSS attacks.
     */
    public static String sanitizeForXSS(String input) {
        if (input == null) return null;

        return input.replaceAll("<", "&lt;")
                .replaceAll(">", "&gt;")
                .replaceAll("\"", "&quot;")
                .replaceAll("'", "&#x27;")
                .replaceAll("/", "&#x2F;");
    }

    /**
     * Validates if a string is safe for SQL operations.
     */
    public static boolean isSafeSqlString(String input) {
        if (input == null) return true;

        String[] dangerousPatterns = {
                "DROP", "DELETE", "INSERT", "UPDATE", "SELECT",
                "--", "/*", "*/", "xp_", "sp_", "EXEC", "EXECUTE"
        };

        String upperInput = input.toUpperCase();
        for (String pattern : dangerousPatterns) {
            if (upperInput.contains(pattern)) {
                return false;
            }
        }
        return true;
    }
}