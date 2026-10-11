package com.reshmamart.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PasswordUtilTest {

    @Test
    public void testPasswordHashingAndVerification() {
        String password = "TestPassword@123";
        String hash = PasswordUtil.hashPassword(password);

        assertNotNull(hash);
        assertTrue(hash.startsWith("$2a$"));
        assertTrue(PasswordUtil.verifyPassword(password, hash));
        assertFalse(PasswordUtil.verifyPassword("WrongPassword", hash));
    }

    @Test
    public void testHashUniquenessWithSalt() {
        String password = "SamePassword123";
        String hash1 = PasswordUtil.hashPassword(password);
        String hash2 = PasswordUtil.hashPassword(password);

        assertNotEquals(hash1, hash2, "BCrypt hashes should differ due to unique salts");
        assertTrue(PasswordUtil.verifyPassword(password, hash1));
        assertTrue(PasswordUtil.verifyPassword(password, hash2));
    }

    @Test
    public void testGenerateSeedHashes() {
        System.out.println("Admin@123 hash: " + PasswordUtil.hashPassword("Admin@123"));
        System.out.println("Seller@123 hash: " + PasswordUtil.hashPassword("Seller@123"));
        System.out.println("Buyer@123 hash: " + PasswordUtil.hashPassword("Buyer@123"));
    }
}
