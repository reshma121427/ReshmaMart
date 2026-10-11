package com.reshmamart.dao;

import com.reshmamart.exception.DatabaseException;
import com.reshmamart.model.CartItem;
import com.reshmamart.model.Order;
import com.reshmamart.model.OrderItem;
import com.reshmamart.model.OrderStatus;
import com.reshmamart.util.DBConnectionPool;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OrderDAOImpl implements OrderDAO {

    @Override
    public Order createOrderWithItems(Long buyerId, String shippingAddress, BigDecimal totalAmount, List<CartItem> cartItems) {
        String insertOrderSql = "INSERT INTO orders (buyer_id, total_amount, status, shipping_address, payment_status) " +
                               "VALUES (?, ?, 'PENDING', ?, 'COMPLETED')";
        String checkStockSql = "SELECT stock_qty FROM products WHERE id = ? FOR UPDATE";
        String updateStockSql = "UPDATE products SET stock_qty = stock_qty - ? WHERE id = ? AND stock_qty >= ?";
        String insertItemSql = "INSERT INTO order_items (order_id, product_id, quantity, unit_price) VALUES (?, ?, ?, ?)";
        String clearCartSql = "DELETE FROM cart_items WHERE buyer_id = ?";

        Connection conn = null;
        try {
            conn = DBConnectionPool.getConnection();
            conn.setAutoCommit(false);

            // 1. Verify and update stock for all items
            for (CartItem item : cartItems) {
                try (PreparedStatement psStock = conn.prepareStatement(updateStockSql)) {
                    psStock.setInt(1, item.getQuantity());
                    psStock.setLong(2, item.getProductId());
                    psStock.setInt(3, item.getQuantity());

                    int rowsUpdated = psStock.executeUpdate();
                    if (rowsUpdated == 0) {
                        conn.rollback();
                        throw new DatabaseException("Insufficient stock for product ID: " + item.getProductId() + " (" + item.getProductName() + ")");
                    }
                }
            }

            // 2. Insert order
            Order order = new Order();
            try (PreparedStatement psOrder = conn.prepareStatement(insertOrderSql, Statement.RETURN_GENERATED_KEYS)) {
                psOrder.setLong(1, buyerId);
                psOrder.setBigDecimal(2, totalAmount);
                psOrder.setString(3, shippingAddress);

                int affectedRows = psOrder.executeUpdate();
                if (affectedRows == 0) {
                    conn.rollback();
                    throw new DatabaseException("Failed to insert order.");
                }

                try (ResultSet keys = psOrder.getGeneratedKeys()) {
                    if (keys.next()) {
                        order.setId(keys.getLong(1));
                    } else {
                        conn.rollback();
                        throw new DatabaseException("Failed to obtain order ID.");
                    }
                }
            }

            order.setBuyerId(buyerId);
            order.setTotalAmount(totalAmount);
            order.setStatus(OrderStatus.PENDING);
            order.setShippingAddress(shippingAddress);
            order.setPaymentStatus("COMPLETED");

            // 3. Insert order items
            List<OrderItem> orderItems = new ArrayList<>();
            try (PreparedStatement psItem = conn.prepareStatement(insertItemSql, Statement.RETURN_GENERATED_KEYS)) {
                for (CartItem item : cartItems) {
                    psItem.setLong(1, order.getId());
                    psItem.setLong(2, item.getProductId());
                    psItem.setInt(3, item.getQuantity());
                    psItem.setBigDecimal(4, item.getProductPrice());
                    psItem.addBatch();

                    OrderItem oi = new OrderItem();
                    oi.setOrderId(order.getId());
                    oi.setProductId(item.getProductId());
                    oi.setQuantity(item.getQuantity());
                    oi.setUnitPrice(item.getProductPrice());
                    oi.setProductName(item.getProductName());
                    oi.setProductImageUrl(item.getProductImageUrl());
                    orderItems.add(oi);
                }
                psItem.executeBatch();
            }
            order.setItems(orderItems);

            // 4. Clear buyer's cart
            try (PreparedStatement psClear = conn.prepareStatement(clearCartSql)) {
                psClear.setLong(1, buyerId);
                psClear.executeUpdate();
            }

            // Commit atomic transaction
            conn.commit();
            return order;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    // ignore rollback exception
                }
            }
            throw new DatabaseException("Error creating order: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    // ignore close exception
                }
            }
        }
    }

    @Override
    public Optional<Order> findById(Long orderId) {
        String sql = "SELECT o.id, o.buyer_id, o.total_amount, o.status, o.shipping_address, o.payment_status, o.created_at, " +
                     "u.name AS buyer_name, u.email AS buyer_email " +
                     "FROM orders o " +
                     "JOIN users u ON o.buyer_id = u.id " +
                     "WHERE o.id = ?";
        try (Connection conn = DBConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Order order = mapRowToOrder(rs);
                    order.setItems(findItemsByOrderId(order.getId(), conn));
                    return Optional.of(order);
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new DatabaseException("Error finding order by ID: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Order> findByBuyerId(Long buyerId) {
        String sql = "SELECT o.id, o.buyer_id, o.total_amount, o.status, o.shipping_address, o.payment_status, o.created_at, " +
                     "u.name AS buyer_name, u.email AS buyer_email " +
                     "FROM orders o " +
                     "JOIN users u ON o.buyer_id = u.id " +
                     "WHERE o.buyer_id = ? " +
                     "ORDER BY o.created_at DESC";
        List<Order> orders = new ArrayList<>();
        try (Connection conn = DBConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, buyerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Order order = mapRowToOrder(rs);
                    order.setItems(findItemsByOrderId(order.getId(), conn));
                    orders.add(order);
                }
            }
            return orders;
        } catch (SQLException e) {
            throw new DatabaseException("Error finding orders by buyer: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Order> findBySellerId(Long sellerId) {
        String sql = "SELECT DISTINCT o.id, o.buyer_id, o.total_amount, o.status, o.shipping_address, o.payment_status, o.created_at, " +
                     "u.name AS buyer_name, u.email AS buyer_email " +
                     "FROM orders o " +
                     "JOIN users u ON o.buyer_id = u.id " +
                     "JOIN order_items oi ON o.id = oi.order_id " +
                     "JOIN products p ON oi.product_id = p.id " +
                     "WHERE p.seller_id = ? " +
                     "ORDER BY o.created_at DESC";
        List<Order> orders = new ArrayList<>();
        try (Connection conn = DBConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, sellerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Order order = mapRowToOrder(rs);
                    order.setItems(findItemsByOrderIdAndSeller(order.getId(), sellerId, conn));
                    orders.add(order);
                }
            }
            return orders;
        } catch (SQLException e) {
            throw new DatabaseException("Error finding orders for seller: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Order> findAll() {
        String sql = "SELECT o.id, o.buyer_id, o.total_amount, o.status, o.shipping_address, o.payment_status, o.created_at, " +
                     "u.name AS buyer_name, u.email AS buyer_email " +
                     "FROM orders o " +
                     "JOIN users u ON o.buyer_id = u.id " +
                     "ORDER BY o.created_at DESC";
        List<Order> orders = new ArrayList<>();
        try (Connection conn = DBConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Order order = mapRowToOrder(rs);
                order.setItems(findItemsByOrderId(order.getId(), conn));
                orders.add(order);
            }
            return orders;
        } catch (SQLException e) {
            throw new DatabaseException("Error finding all orders: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean updateStatus(Long orderId, OrderStatus newStatus) {
        String sql = "UPDATE orders SET status = ? WHERE id = ?";
        try (Connection conn = DBConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newStatus.name());
            ps.setLong(2, orderId);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error updating order status: " + e.getMessage(), e);
        }
    }

    @Override
    public int countAll() {
        String sql = "SELECT COUNT(*) FROM orders";
        try (Connection conn = DBConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error counting orders: " + e.getMessage(), e);
        }
    }

    @Override
    public BigDecimal calculateTotalRevenue() {
        String sql = "SELECT COALESCE(SUM(total_amount), 0.00) FROM orders WHERE status != 'CANCELLED'";
        try (Connection conn = DBConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getBigDecimal(1);
            }
            return BigDecimal.ZERO;
        } catch (SQLException e) {
            throw new DatabaseException("Error calculating total revenue: " + e.getMessage(), e);
        }
    }

    private List<OrderItem> findItemsByOrderId(Long orderId, Connection conn) throws SQLException {
        String sql = "SELECT oi.id, oi.order_id, oi.product_id, oi.quantity, oi.unit_price, oi.created_at, " +
                     "p.name AS product_name, p.image_url AS product_image_url, p.seller_id " +
                     "FROM order_items oi " +
                     "JOIN products p ON oi.product_id = p.id " +
                     "WHERE oi.order_id = ?";
        List<OrderItem> items = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    OrderItem item = new OrderItem();
                    item.setId(rs.getLong("id"));
                    item.setOrderId(rs.getLong("order_id"));
                    item.setProductId(rs.getLong("product_id"));
                    item.setQuantity(rs.getInt("quantity"));
                    item.setUnitPrice(rs.getBigDecimal("unit_price"));
                    item.setCreatedAt(rs.getTimestamp("created_at"));
                    item.setProductName(rs.getString("product_name"));
                    item.setProductImageUrl(rs.getString("product_image_url"));
                    item.setSellerId(rs.getLong("seller_id"));
                    items.add(item);
                }
            }
        }
        return items;
    }

    private List<OrderItem> findItemsByOrderIdAndSeller(Long orderId, Long sellerId, Connection conn) throws SQLException {
        String sql = "SELECT oi.id, oi.order_id, oi.product_id, oi.quantity, oi.unit_price, oi.created_at, " +
                     "p.name AS product_name, p.image_url AS product_image_url, p.seller_id " +
                     "FROM order_items oi " +
                     "JOIN products p ON oi.product_id = p.id " +
                     "WHERE oi.order_id = ? AND p.seller_id = ?";
        List<OrderItem> items = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, orderId);
            ps.setLong(2, sellerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    OrderItem item = new OrderItem();
                    item.setId(rs.getLong("id"));
                    item.setOrderId(rs.getLong("order_id"));
                    item.setProductId(rs.getLong("product_id"));
                    item.setQuantity(rs.getInt("quantity"));
                    item.setUnitPrice(rs.getBigDecimal("unit_price"));
                    item.setCreatedAt(rs.getTimestamp("created_at"));
                    item.setProductName(rs.getString("product_name"));
                    item.setProductImageUrl(rs.getString("product_image_url"));
                    item.setSellerId(rs.getLong("seller_id"));
                    items.add(item);
                }
            }
        }
        return items;
    }

    private Order mapRowToOrder(ResultSet rs) throws SQLException {
        Order o = new Order();
        o.setId(rs.getLong("id"));
        o.setBuyerId(rs.getLong("buyer_id"));
        o.setTotalAmount(rs.getBigDecimal("total_amount"));
        o.setStatus(OrderStatus.fromString(rs.getString("status")));
        o.setShippingAddress(rs.getString("shipping_address"));
        o.setPaymentStatus(rs.getString("payment_status"));
        o.setCreatedAt(rs.getTimestamp("created_at"));
        o.setBuyerName(rs.getString("buyer_name"));
        o.setBuyerEmail(rs.getString("buyer_email"));
        return o;
    }
}
