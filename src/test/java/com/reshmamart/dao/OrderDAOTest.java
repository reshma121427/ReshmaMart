package com.reshmamart.dao;

import com.reshmamart.exception.DatabaseException;
import com.reshmamart.model.*;
import com.reshmamart.util.DBConnectionPool;
import com.reshmamart.util.PasswordUtil;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class OrderDAOTest {

    private static OrderDAO orderDAO;
    private static ProductDAO productDAO;
    private static UserDAO userDAO;
    private static CartDAO cartDAO;

    private static Long buyerId;
    private static Long sellerId;
    private static Long productId;

    @BeforeAll
    public static void setUpDatabase() throws Exception {
        Properties props = new Properties();
        props.setProperty("db.url", "jdbc:h2:mem:ordertest;DB_CLOSE_DELAY=-1");
        props.setProperty("db.driver", "org.h2.Driver");
        props.setProperty("db.user", "sa");
        props.setProperty("db.password", "");
        props.setProperty("db.pool.maximum", "5");
        props.setProperty("db.pool.minimumIdle", "1");

        DBConnectionPool.init(props);
        orderDAO = new OrderDAOImpl();
        productDAO = new ProductDAOImpl();
        userDAO = new UserDAOImpl();
        cartDAO = new CartDAOImpl();

        try (Connection conn = DBConnectionPool.getConnection();
             Statement stmt = conn.createStatement();
             InputStream in = OrderDAOTest.class.getClassLoader().getResourceAsStream("schema.sql")) {
            assertNotNull(in, "schema.sql should be present");
            String schemaSql = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))
                .lines().collect(Collectors.joining("\n"));
            for (String sql : schemaSql.split(";")) {
                if (!sql.trim().isEmpty()) {
                    stmt.execute(sql.trim());
                }
            }
        }

        User buyer = new User(null, "Order Buyer", "order.buyer@example.com", PasswordUtil.hashPassword("Pass123"), Role.BUYER, null);
        buyerId = userDAO.create(buyer).getId();

        User seller = new User(null, "Order Seller", "order.seller@example.com", PasswordUtil.hashPassword("Pass123"), Role.SELLER, null);
        sellerId = userDAO.create(seller).getId();
    }

    @BeforeEach
    public void resetData() throws Exception {
        try (Connection conn = DBConnectionPool.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM reviews");
            stmt.execute("DELETE FROM order_items");
            stmt.execute("DELETE FROM orders");
            stmt.execute("DELETE FROM cart_items");
            stmt.execute("DELETE FROM products");
        }

        Product prod = new Product(null, sellerId, "Test Laptop", "Fast laptop", new BigDecimal("999.00"), 10, "Electronics", null, true, null);
        productId = productDAO.create(prod).getId();
    }

    @AfterAll
    public static void tearDown() {
        DBConnectionPool.shutdown();
    }

    @Test
    public void testCreateOrderWithItemsAndStockDecrement() {
        CartItem cartItem = new CartItem();
        cartItem.setBuyerId(buyerId);
        cartItem.setProductId(productId);
        cartItem.setQuantity(2);
        cartItem.setProductName("Test Laptop");
        cartItem.setProductPrice(new BigDecimal("999.00"));

        List<CartItem> cartItems = new ArrayList<>();
        cartItems.add(cartItem);

        Order order = orderDAO.createOrderWithItems(buyerId, "123 Test Street, City", new BigDecimal("1998.00"), cartItems);

        assertNotNull(order.getId());
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(new BigDecimal("1998.00"), order.getTotalAmount());
        assertEquals(1, order.getItems().size());

        // Verify product stock decremented from 10 to 8
        Product updatedProduct = productDAO.findById(productId).orElseThrow();
        assertEquals(8, updatedProduct.getStockQty());
    }

    @Test
    public void testInsufficientStockRollback() {
        CartItem cartItem = new CartItem();
        cartItem.setBuyerId(buyerId);
        cartItem.setProductId(productId);
        cartItem.setQuantity(15); // Exceeds available stock (10)
        cartItem.setProductName("Test Laptop");
        cartItem.setProductPrice(new BigDecimal("999.00"));

        List<CartItem> cartItems = new ArrayList<>();
        cartItems.add(cartItem);

        assertThrows(DatabaseException.class, () -> {
            orderDAO.createOrderWithItems(buyerId, "123 Test St", new BigDecimal("14985.00"), cartItems);
        });

        // Stock should remain unchanged (10)
        Product product = productDAO.findById(productId).orElseThrow();
        assertEquals(10, product.getStockQty());
    }

    @Test
    public void testUpdateOrderStatus() {
        CartItem cartItem = new CartItem();
        cartItem.setBuyerId(buyerId);
        cartItem.setProductId(productId);
        cartItem.setQuantity(1);
        cartItem.setProductName("Test Laptop");
        cartItem.setProductPrice(new BigDecimal("999.00"));

        List<CartItem> cartItems = List.of(cartItem);
        Order order = orderDAO.createOrderWithItems(buyerId, "123 Test St", new BigDecimal("999.00"), cartItems);

        boolean updated = orderDAO.updateStatus(order.getId(), OrderStatus.CONFIRMED);
        assertTrue(updated);

        Optional<Order> fetched = orderDAO.findById(order.getId());
        assertTrue(fetched.isPresent());
        assertEquals(OrderStatus.CONFIRMED, fetched.get().getStatus());
    }
}
