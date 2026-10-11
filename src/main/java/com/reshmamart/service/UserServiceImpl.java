package com.reshmamart.service;

import com.reshmamart.dao.UserDAO;
import com.reshmamart.dto.LoginRequestDTO;
import com.reshmamart.dto.RegisterRequestDTO;
import com.reshmamart.dto.UserResponseDTO;
import com.reshmamart.exception.AuthenticationException;
import com.reshmamart.exception.ResourceNotFoundException;
import com.reshmamart.exception.ValidationException;
import com.reshmamart.model.Role;
import com.reshmamart.model.User;
import com.reshmamart.util.PasswordUtil;
import com.reshmamart.util.ValidationUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserServiceImpl implements UserService {
    private final UserDAO userDAO;

    public UserServiceImpl(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Override
    public UserResponseDTO register(RegisterRequestDTO request) {
        if (request == null) {
            throw new ValidationException("Registration request cannot be null");
        }

        // Validate input at the top of the service method
        ValidationUtil.validateRegistration(
            request.getName(),
            request.getEmail(),
            request.getPassword(),
            request.getConfirmPassword()
        );

        // Disallow registering as ADMIN (Admin is seeded only)
        if (request.getRole() == Role.ADMIN) {
            Map<String, String> errors = new HashMap<>();
            errors.put("role", "Admin accounts cannot be registered via signup");
            throw new ValidationException("Invalid role", errors);
        }

        Role role = request.getRole() != null ? request.getRole() : Role.BUYER;

        // Check if email already exists
        if (userDAO.existsByEmail(request.getEmail().trim())) {
            Map<String, String> errors = new HashMap<>();
            errors.put("email", "An account with this email address already exists");
            throw new ValidationException("Duplicate email", errors);
        }

        // Hash password with jBCrypt
        String passwordHash = PasswordUtil.hashPassword(request.getPassword());

        User newUser = new User();
        newUser.setName(request.getName().trim());
        newUser.setEmail(request.getEmail().trim().toLowerCase());
        newUser.setPasswordHash(passwordHash);
        newUser.setRole(role);

        User created = userDAO.create(newUser);
        return UserResponseDTO.fromEntity(created);
    }

    @Override
    public User login(LoginRequestDTO request) {
        if (request == null) {
            throw new ValidationException("Login credentials cannot be null");
        }

        ValidationUtil.validateLogin(request.getEmail(), request.getPassword());

        User user = userDAO.findByEmail(request.getEmail().trim().toLowerCase())
            .orElseThrow(() -> new AuthenticationException("Invalid email or password"));

        if (!PasswordUtil.verifyPassword(request.getPassword(), user.getPasswordHash())) {
            throw new AuthenticationException("Invalid email or password");
        }

        return user;
    }

    @Override
    public UserResponseDTO getUserById(Long id) {
        if (id == null || id <= 0) {
            throw new ValidationException("Invalid user ID");
        }
        User user = userDAO.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
        return UserResponseDTO.fromEntity(user);
    }

    @Override
    public List<UserResponseDTO> getAllUsers() {
        List<User> users = userDAO.findAll();
        List<UserResponseDTO> dtos = new ArrayList<>();
        for (User u : users) {
            dtos.add(UserResponseDTO.fromEntity(u));
        }
        return dtos;
    }

    @Override
    public int getTotalUserCount() {
        return userDAO.countAll();
    }
}
