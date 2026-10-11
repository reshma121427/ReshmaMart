package com.reshmamart.dao;

import com.reshmamart.model.Role;
import com.reshmamart.model.User;
import com.reshmamart.util.DBConnectionPool;
import com.reshmamart.util.PasswordUtil;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UserDAOTest {

    private static UserDAO userDAO;

    @BeforeAll
    public static void setUpDatabase() throws Exception {
        Properties props = new Properties();
        props.setProperty("db.url", "jdbc:h2:mem:usertest;DB_CLOSE_DELAY=-1");
        props.setProperty("db.driver", "org.h2.Driver");
        props.setProperty("db.user", "sa");
        props.setProperty("db.password", "");
        props.setProperty("db.pool.maximum", "5");
        props.setProperty("db.pool.minimumIdle", "1");

        DBConnectionPool.init(props);
        userDAO = new UserDAOImpl();

        // Load schema
        try (Connection conn = DBConnectionPool.getConnection();
             Statement stmt = conn.createStatement();
             InputStream in = UserDAOTest.class.getClassLoader().getResourceAsStream("schema.sql")) {
            assertNotNull(in, "schema.sql should be present");
            String schemaSql = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))
                .lines().collect(Collectors.joining("\n"));
            for (String sql : schemaSql.split(";")) {
                if (!sql.trim().isEmpty()) {
                    stmt.execute(sql.trim());
                }
            }
        }
    }

    @BeforeEach
    public void cleanUsers() throws Exception {
        try (Connection conn = DBConnectionPool.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM users");
        }
    }

    @AfterAll
    public static void tearDown() {
        DBConnectionPool.shutdown();
    }

    @Test
    public void testCreateAndFindById() {
        User user = new User();
        user.setName("Test Buyer");
        user.setEmail("testbuyer@example.com");
        user.setPasswordHash(PasswordUtil.hashPassword("Password123"));
        user.setRole(Role.BUYER);

        User created = userDAO.create(user);
        assertNotNull(created.getId());

        Optional<User> found = userDAO.findById(created.getId());
        assertTrue(found.isPresent());
        assertEquals("Test Buyer", found.get().getName());
        assertEquals("testbuyer@example.com", found.get().getEmail());
        assertEquals(Role.BUYER, found.get().getRole());
    }

    @Test
    public void testFindByEmail() {
        User user = new User();
        user.setName("Test Seller");
        user.setEmail("seller@example.com");
        user.setPasswordHash(PasswordUtil.hashPassword("Password123"));
        user.setRole(Role.SELLER);

        userDAO.create(user);

        Optional<User> found = userDAO.findByEmail("seller@example.com");
        assertTrue(found.isPresent());
        assertEquals("Test Seller", found.get().getName());

        // Case insensitivity test
        Optional<User> foundUpper = userDAO.findByEmail("SELLER@EXAMPLE.COM");
        assertTrue(foundUpper.isPresent());
    }

    @Test
    public void testExistsByEmail() {
        User user = new User();
        user.setName("Admin User");
        user.setEmail("admin@example.com");
        user.setPasswordHash(PasswordUtil.hashPassword("AdminPass"));
        user.setRole(Role.ADMIN);

        userDAO.create(user);

        assertTrue(userDAO.existsByEmail("admin@example.com"));
        assertFalse(userDAO.existsByEmail("nonexistent@example.com"));
    }
}
