package com.reshmamart.util;

import org.mindrot.jbcrypt.BCrypt;

public final class PasswordUtil {
    private static final int LOG_ROUNDS = 12;

    private PasswordUtil() {}

    /**
     * Hashes a plaintext password using BCrypt with salt.
     * Note: Never log the plaintext password or the resulting hash in business logs.
     */
    public static String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(LOG_ROUNDS));
    }

    /**
     * Verifies a plaintext password against a stored BCrypt hash.
     */
    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null) {
            return false;
        }
        try {
            return BCrypt.checkpw(plainPassword, hashedPassword);
        } catch (Exception e) {
            return false;
        }
    }
}
