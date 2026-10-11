package com.reshmamart.dao;

import com.reshmamart.exception.DatabaseException;
import com.reshmamart.model.CartItem;
import com.reshmamart.util.DBConnectionPool;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CartDAOImpl implements CartDAO {

    @Override
    public CartItem addItem(Long buyerId, Long productId, int quantity) {
        // Check if item already in cart
        Optional<CartItem> existing = findByBuyerAndProduct(buyerId, productId);
        if (existing.isPresent()) {
            int newQty = existing.get().getQuantity() + quantity;
            updateQuantity(buyerId, productId, newQty);
            existing.get().setQuantity(newQty);
            return existing.get();
        }

        String sql = "INSERT INTO cart_items (buyer_id, product_id, quantity) VALUES (?, ?, ?)";
        try (Connection conn = DBConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, buyerId);
            ps.setLong(2, productId);
            ps.setInt(3, quantity);

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new DatabaseException("Adding item to cart failed, no rows affected.");
            }

            CartItem item = new CartItem();
            item.setBuyerId(buyerId);
            item.setProductId(productId);
            item.setQuantity(quantity);

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    item.setId(generatedKeys.getLong(1));
                }
            }
            return item;
        } catch (SQLException e) {
            throw new DatabaseException("Error adding item to cart: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean updateQuantity(Long buyerId, Long productId, int quantity) {
        if (quantity <= 0) {
            return removeItem(buyerId, productId);
        }
        String sql = "UPDATE cart_items SET quantity = ? WHERE buyer_id = ? AND product_id = ?";
        try (Connection conn = DBConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, quantity);
            ps.setLong(2, buyerId);
            ps.setLong(3, productId);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error updating cart quantity: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean removeItem(Long buyerId, Long productId) {
        String sql = "DELETE FROM cart_items WHERE buyer_id = ? AND product_id = ?";
        try (Connection conn = DBConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, buyerId);
            ps.setLong(2, productId);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error removing item from cart: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean removeById(Long cartItemId, Long buyerId) {
        String sql = "DELETE FROM cart_items WHERE id = ? AND buyer_id = ?";
        try (Connection conn = DBConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, cartItemId);
            ps.setLong(2, buyerId);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error removing item by ID: " + e.getMessage(), e);
        }
    }

    @Override
    public List<CartItem> getCartByBuyerId(Long buyerId) {
        String sql = "SELECT c.id, c.buyer_id, c.product_id, c.quantity, c.created_at, " +
                     "p.name AS product_name, p.image_url AS product_image_url, p.price AS product_price, " +
                     "p.stock_qty AS product_stock, p.category AS product_category " +
                     "FROM cart_items c " +
                     "JOIN products p ON c.product_id = p.id " +
                     "WHERE c.buyer_id = ? " +
                     "ORDER BY c.created_at ASC";
        List<CartItem> items = new ArrayList<>();
        try (Connection conn = DBConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, buyerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CartItem item = new CartItem();
                    item.setId(rs.getLong("id"));
                    item.setBuyerId(rs.getLong("buyer_id"));
                    item.setProductId(rs.getLong("product_id"));
                    item.setQuantity(rs.getInt("quantity"));
                    item.setCreatedAt(rs.getTimestamp("created_at"));
                    item.setProductName(rs.getString("product_name"));
                    item.setProductImageUrl(rs.getString("product_image_url"));
                    item.setProductPrice(rs.getBigDecimal("product_price"));
                    item.setProductStock(rs.getInt("product_stock"));
                    item.setProductCategory(rs.getString("product_category"));
                    items.add(item);
                }
            }
            return items;
        } catch (SQLException e) {
            throw new DatabaseException("Error getting cart items: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<CartItem> findByBuyerAndProduct(Long buyerId, Long productId) {
        String sql = "SELECT c.id, c.buyer_id, c.product_id, c.quantity, c.created_at, " +
                     "p.name AS product_name, p.image_url AS product_image_url, p.price AS product_price, " +
                     "p.stock_qty AS product_stock, p.category AS product_category " +
                     "FROM cart_items c " +
                     "JOIN products p ON c.product_id = p.id " +
                     "WHERE c.buyer_id = ? AND c.product_id = ?";
        try (Connection conn = DBConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, buyerId);
            ps.setLong(2, productId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    CartItem item = new CartItem();
                    item.setId(rs.getLong("id"));
                    item.setBuyerId(rs.getLong("buyer_id"));
                    item.setProductId(rs.getLong("product_id"));
                    item.setQuantity(rs.getInt("quantity"));
                    item.setCreatedAt(rs.getTimestamp("created_at"));
                    item.setProductName(rs.getString("product_name"));
                    item.setProductImageUrl(rs.getString("product_image_url"));
                    item.setProductPrice(rs.getBigDecimal("product_price"));
                    item.setProductStock(rs.getInt("product_stock"));
                    item.setProductCategory(rs.getString("product_category"));
                    return Optional.of(item);
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new DatabaseException("Error finding cart item: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean clearCart(Long buyerId) {
        String sql = "DELETE FROM cart_items WHERE buyer_id = ?";
        try (Connection conn = DBConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, buyerId);
            return ps.executeUpdate() >= 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error clearing cart: " + e.getMessage(), e);
        }
    }

    @Override
    public int countItemsInCart(Long buyerId) {
        String sql = "SELECT COALESCE(SUM(quantity), 0) FROM cart_items WHERE buyer_id = ?";
        try (Connection conn = DBConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, buyerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            return 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error counting cart items: " + e.getMessage(), e);
        }
    }
}
