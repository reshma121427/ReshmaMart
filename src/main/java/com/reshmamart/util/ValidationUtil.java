package com.reshmamart.util;

import com.reshmamart.exception.ValidationException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public final class ValidationUtil {
    private static final Pattern EMAIL_PATTERN =
        Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private ValidationUtil() {}

    public static boolean isValidEmail(String email) {
        if (email == null) return false;
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    public static void validateRegistration(String name, String email, String password, String confirmPassword) {
        Map<String, String> errors = new HashMap<>();

        if (name == null || name.trim().length() < 2) {
            errors.put("name", "Name must be at least 2 characters long");
        } else if (name.trim().length() > 100) {
            errors.put("name", "Name cannot exceed 100 characters");
        }

        if (email == null || !isValidEmail(email)) {
            errors.put("email", "Please provide a valid email address");
        }

        if (password == null || password.length() < 6) {
            errors.put("password", "Password must be at least 6 characters long");
        }

        if (confirmPassword == null || !confirmPassword.equals(password)) {
            errors.put("confirmPassword", "Passwords do not match");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException("Registration validation failed", errors);
        }
    }

    public static void validateLogin(String email, String password) {
        Map<String, String> errors = new HashMap<>();

        if (email == null || !isValidEmail(email)) {
            errors.put("email", "Valid email is required");
        }

        if (password == null || password.trim().isEmpty()) {
            errors.put("password", "Password is required");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException("Login validation failed", errors);
        }
    }

    public static void validateProduct(String name, String description, BigDecimal price, Integer stockQty, String category) {
        Map<String, String> errors = new HashMap<>();

        if (name == null || name.trim().isEmpty()) {
            errors.put("name", "Product name is required");
        } else if (name.trim().length() > 200) {
            errors.put("name", "Product name cannot exceed 200 characters");
        }

        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            errors.put("price", "Price must be greater than 0");
        }

        if (stockQty == null || stockQty < 0) {
            errors.put("stockQty", "Stock quantity cannot be negative");
        }

        if (category == null || category.trim().isEmpty()) {
            errors.put("category", "Category is required");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException("Product validation failed", errors);
        }
    }

    public static void validateReview(Integer rating, String comment) {
        Map<String, String> errors = new HashMap<>();

        if (rating == null || rating < 1 || rating > 5) {
            errors.put("rating", "Rating must be an integer between 1 and 5");
        }

        if (comment != null && comment.trim().length() > 1000) {
            errors.put("comment", "Review comment cannot exceed 1000 characters");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException("Review validation failed", errors);
        }
    }

    public static void validateShippingAddress(String address) {
        Map<String, String> errors = new HashMap<>();

        if (address == null || address.trim().length() < 10) {
            errors.put("shippingAddress", "Shipping address must be at least 10 characters long");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException("Checkout validation failed", errors);
        }
    }
}
