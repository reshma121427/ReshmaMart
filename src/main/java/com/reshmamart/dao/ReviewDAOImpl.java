package com.reshmamart.dao;

import com.reshmamart.exception.DatabaseException;
import com.reshmamart.model.Review;
import com.reshmamart.util.DBConnectionPool;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ReviewDAOImpl implements ReviewDAO {

    @Override
    public Review create(Review review) {
        String sql = "INSERT INTO reviews (order_id, product_id, buyer_id, rating, comment) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, review.getOrderId());
            ps.setLong(2, review.getProductId());
            ps.setLong(3, review.getBuyerId());
            ps.setInt(4, review.getRating());
            ps.setString(5, review.getComment());

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new DatabaseException("Creating review failed, no rows affected.");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    review.setId(generatedKeys.getLong(1));
                }
            }
            return review;
        } catch (SQLException e) {
            throw new DatabaseException("Error creating review: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Review> findByProductId(Long productId) {
        String sql = "SELECT r.id, r.order_id, r.product_id, r.buyer_id, r.rating, r.comment, r.created_at, " +
                     "u.name AS buyer_name, p.name AS product_name " +
                     "FROM reviews r " +
                     "JOIN users u ON r.buyer_id = u.id " +
                     "JOIN products p ON r.product_id = p.id " +
                     "WHERE r.product_id = ? " +
                     "ORDER BY r.created_at DESC";
        List<Review> reviews = new ArrayList<>();
        try (Connection conn = DBConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Review r = new Review();
                    r.setId(rs.getLong("id"));
                    r.setOrderId(rs.getLong("order_id"));
                    r.setProductId(rs.getLong("product_id"));
                    r.setBuyerId(rs.getLong("buyer_id"));
                    r.setRating(rs.getInt("rating"));
                    r.setComment(rs.getString("comment"));
                    r.setCreatedAt(rs.getTimestamp("created_at"));
                    r.setBuyerName(rs.getString("buyer_name"));
                    r.setProductName(rs.getString("product_name"));
                    reviews.add(r);
                }
            }
            return reviews;
        } catch (SQLException e) {
            throw new DatabaseException("Error finding reviews by product ID: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean hasBuyerPurchasedProduct(Long buyerId, Long productId) {
        // Buyer can review if they have purchased the product in an order that is either CONFIRMED, SHIPPED, or DELIVERED
        String sql = "SELECT 1 FROM orders o " +
                     "JOIN order_items oi ON o.id = oi.order_id " +
                     "WHERE o.buyer_id = ? AND oi.product_id = ? AND o.status IN ('CONFIRMED', 'SHIPPED', 'DELIVERED') " +
                     "LIMIT 1";
        try (Connection conn = DBConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, buyerId);
            ps.setLong(2, productId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error checking purchase history: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Review> findByOrderAndProductAndBuyer(Long orderId, Long productId, Long buyerId) {
        String sql = "SELECT id, order_id, product_id, buyer_id, rating, comment, created_at " +
                     "FROM reviews WHERE order_id = ? AND product_id = ? AND buyer_id = ?";
        try (Connection conn = DBConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, orderId);
            ps.setLong(2, productId);
            ps.setLong(3, buyerId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Review r = new Review();
                    r.setId(rs.getLong("id"));
                    r.setOrderId(rs.getLong("order_id"));
                    r.setProductId(rs.getLong("product_id"));
                    r.setBuyerId(rs.getLong("buyer_id"));
                    r.setRating(rs.getInt("rating"));
                    r.setComment(rs.getString("comment"));
                    r.setCreatedAt(rs.getTimestamp("created_at"));
                    return Optional.of(r);
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new DatabaseException("Error finding existing review: " + e.getMessage(), e);
        }
    }

    @Override
    public double getAverageRating(Long productId) {
        String sql = "SELECT COALESCE(AVG(rating), 0.0) FROM reviews WHERE product_id = ?";
        try (Connection conn = DBConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }
            return 0.0;
        } catch (SQLException e) {
            throw new DatabaseException("Error calculating average rating: " + e.getMessage(), e);
        }
    }

    @Override
    public int countReviewsForProduct(Long productId) {
        String sql = "SELECT COUNT(*) FROM reviews WHERE product_id = ?";
        try (Connection conn = DBConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            return 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error counting reviews: " + e.getMessage(), e);
        }
    }
}
