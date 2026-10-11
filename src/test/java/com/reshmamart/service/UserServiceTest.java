package com.reshmamart.service;

import com.reshmamart.dao.UserDAO;
import com.reshmamart.dto.LoginRequestDTO;
import com.reshmamart.dto.RegisterRequestDTO;
import com.reshmamart.dto.UserResponseDTO;
import com.reshmamart.exception.AuthenticationException;
import com.reshmamart.exception.ValidationException;
import com.reshmamart.model.Role;
import com.reshmamart.model.User;
import com.reshmamart.util.PasswordUtil;
import java.sql.Timestamp;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserDAO userDAO;

    private UserService userService;

    @BeforeEach
    public void setUp() {
        userService = new UserServiceImpl(userDAO);
    }

    @Test
    public void testRegisterSuccess() {
        RegisterRequestDTO request = new RegisterRequestDTO(
            "Rubini User", "rubini@example.com", "Secret@123", "Secret@123", Role.BUYER
        );

        when(userDAO.existsByEmail("rubini@example.com")).thenReturn(false);

        User mockCreated = new User(1L, "Rubini User", "rubini@example.com", "hashed_pwd", Role.BUYER, new Timestamp(System.currentTimeMillis()));
        when(userDAO.create(any(User.class))).thenReturn(mockCreated);

        UserResponseDTO response = userService.register(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Rubini User", response.getName());
        assertEquals("rubini@example.com", response.getEmail());
        assertEquals(Role.BUYER, response.getRole());

        verify(userDAO).create(any(User.class));
    }

    @Test
    public void testRegisterAdminDisallowed() {
        RegisterRequestDTO request = new RegisterRequestDTO(
            "Fake Admin", "fakeadmin@example.com", "Secret@123", "Secret@123", Role.ADMIN
        );

        ValidationException ex = assertThrows(ValidationException.class, () -> {
            userService.register(request);
        });

        assertTrue(ex.getFieldErrors().containsKey("role"));
        verify(userDAO, never()).create(any(User.class));
    }

    @Test
    public void testRegisterDuplicateEmailThrowsException() {
        RegisterRequestDTO request = new RegisterRequestDTO(
            "Duplicate User", "existing@example.com", "Secret@123", "Secret@123", Role.BUYER
        );

        when(userDAO.existsByEmail("existing@example.com")).thenReturn(true);

        ValidationException ex = assertThrows(ValidationException.class, () -> {
            userService.register(request);
        });

        assertTrue(ex.getFieldErrors().containsKey("email"));
        verify(userDAO, never()).create(any(User.class));
    }

    @Test
    public void testLoginSuccess() {
        String plainPassword = "Password@123";
        String hashedPassword = PasswordUtil.hashPassword(plainPassword);

        User mockUser = new User(1L, "John", "john@example.com", hashedPassword, Role.BUYER, new Timestamp(System.currentTimeMillis()));
        when(userDAO.findByEmail("john@example.com")).thenReturn(Optional.of(mockUser));

        LoginRequestDTO request = new LoginRequestDTO("john@example.com", plainPassword);
        User loggedIn = userService.login(request);

        assertNotNull(loggedIn);
        assertEquals(1L, loggedIn.getId());
        assertEquals("john@example.com", loggedIn.getEmail());
    }

    @Test
    public void testLoginWrongPasswordThrowsException() {
        String hashedPassword = PasswordUtil.hashPassword("CorrectPassword@123");

        User mockUser = new User(1L, "John", "john@example.com", hashedPassword, Role.BUYER, new Timestamp(System.currentTimeMillis()));
        when(userDAO.findByEmail("john@example.com")).thenReturn(Optional.of(mockUser));

        LoginRequestDTO request = new LoginRequestDTO("john@example.com", "WrongPassword");

        assertThrows(AuthenticationException.class, () -> {
            userService.login(request);
        });
    }
}
