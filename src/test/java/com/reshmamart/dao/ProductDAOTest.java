package com.reshmamart.dao;

import com.reshmamart.model.Product;
import com.reshmamart.model.Role;
import com.reshmamart.model.User;
import com.reshmamart.util.DBConnectionPool;
import com.reshmamart.util.PasswordUtil;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ProductDAOTest {

    private static ProductDAO productDAO;
    private static UserDAO userDAO;
    private static Long sellerId;

    @BeforeAll
    public static void setUpDatabase() throws Exception {
        Properties props = new Properties();
        props.setProperty("db.url", "jdbc:h2:mem:producttest;DB_CLOSE_DELAY=-1");
        props.setProperty("db.driver", "org.h2.Driver");
        props.setProperty("db.user", "sa");
        props.setProperty("db.password", "");
        props.setProperty("db.pool.maximum", "5");
        props.setProperty("db.pool.minimumIdle", "1");

        DBConnectionPool.init(props);
        productDAO = new ProductDAOImpl();
        userDAO = new UserDAOImpl();

        try (Connection conn = DBConnectionPool.getConnection();
             Statement stmt = conn.createStatement();
             InputStream in = ProductDAOTest.class.getClassLoader().getResourceAsStream("schema.sql")) {
            assertNotNull(in, "schema.sql should be present");
            String schemaSql = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))
                .lines().collect(Collectors.joining("\n"));
            for (String sql : schemaSql.split(";")) {
                if (!sql.trim().isEmpty()) {
                    stmt.execute(sql.trim());
                }
            }
        }

        // Create a test seller
        User seller = new User();
        seller.setName("Product Test Seller");
        seller.setEmail("prod.seller@example.com");
        seller.setPasswordHash(PasswordUtil.hashPassword("Password123"));
        seller.setRole(Role.SELLER);
        User created = userDAO.create(seller);
        sellerId = created.getId();
    }

    @BeforeEach
    public void cleanProducts() throws Exception {
        try (Connection conn = DBConnectionPool.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM reviews");
            stmt.execute("DELETE FROM order_items");
            stmt.execute("DELETE FROM cart_items");
            stmt.execute("DELETE FROM products");
        }
    }

    @AfterAll
    public static void tearDown() {
        DBConnectionPool.shutdown();
    }

    @Test
    public void testCreateAndFindProduct() {
        Product p = new Product();
        p.setSellerId(sellerId);
        p.setName("Wireless Mouse");
        p.setDescription("Ergonomic 2.4GHz wireless mouse");
        p.setPrice(new BigDecimal("29.99"));
        p.setStockQty(50);
        p.setCategory("Electronics");
        p.setImageUrl("https://example.com/mouse.jpg");
        p.setIsActive(true);

        Product created = productDAO.create(p);
        assertNotNull(created.getId());

        Optional<Product> found = productDAO.findById(created.getId());
        assertTrue(found.isPresent());
        assertEquals("Wireless Mouse", found.get().getName());
        assertEquals(new BigDecimal("29.99"), found.get().getPrice());
        assertEquals(50, found.get().getStockQty());
        assertEquals("Electronics", found.get().getCategory());
    }

    @Test
    public void testUpdateProductAndStock() {
        Product p = new Product();
        p.setSellerId(sellerId);
        p.setName("Keyboard");
        p.setDescription("Mechanical Keyboard");
        p.setPrice(new BigDecimal("89.99"));
        p.setStockQty(20);
        p.setCategory("Electronics");

        Product created = productDAO.create(p);

        // Update details
        created.setPrice(new BigDecimal("79.99"));
        created.setStockQty(15);
        boolean updated = productDAO.update(created);
        assertTrue(updated);

        Optional<Product> found = productDAO.findById(created.getId());
        assertTrue(found.isPresent());
        assertEquals(new BigDecimal("79.99"), found.get().getPrice());
        assertEquals(15, found.get().getStockQty());

        // Decrement stock by 5
        boolean stockUpdated = productDAO.updateStock(created.getId(), -5);
        assertTrue(stockUpdated);

        Optional<Product> afterStock = productDAO.findById(created.getId());
        assertTrue(afterStock.isPresent());
        assertEquals(10, afterStock.get().getStockQty());
    }

    @Test
    public void testSearchAndFilter() {
        Product p1 = new Product(null, sellerId, "Apple iPhone 15", "Latest smartphone", new BigDecimal("799.00"), 10, "Electronics", null, true, null);
        Product p2 = new Product(null, sellerId, "Running Shoes", "Comfortable sneakers", new BigDecimal("89.00"), 25, "Fashion", null, true, null);
        Product p3 = new Product(null, sellerId, "Cotton T-Shirt", "Plain crew neck", new BigDecimal("19.99"), 100, "Fashion", null, true, null);

        productDAO.create(p1);
        productDAO.create(p2);
        productDAO.create(p3);

        // Filter by category
        List<Product> fashionList = productDAO.searchProducts("Fashion", null, "newest", 10, 0);
        assertEquals(2, fashionList.size());

        // Search by keyword
        List<Product> phoneList = productDAO.searchProducts(null, "iPhone", "newest", 10, 0);
        assertEquals(1, phoneList.size());
        assertEquals("Apple iPhone 15", phoneList.get(0).getName());
    }
}
