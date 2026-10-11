package com.reshmamart.service;

import com.reshmamart.dao.OrderDAO;
import com.reshmamart.dao.ProductDAO;
import com.reshmamart.dao.ReviewDAO;
import com.reshmamart.dto.ReviewDTO;
import com.reshmamart.exception.AuthenticationException;
import com.reshmamart.exception.ResourceNotFoundException;
import com.reshmamart.exception.ValidationException;
import com.reshmamart.model.Order;
import com.reshmamart.model.Product;
import com.reshmamart.model.Review;
import com.reshmamart.util.ValidationUtil;
import java.util.ArrayList;
import java.util.List;

public class ReviewServiceImpl implements ReviewService {
    private final ReviewDAO reviewDAO;
    private final OrderDAO orderDAO;
    private final ProductDAO productDAO;

    public ReviewServiceImpl(ReviewDAO reviewDAO, OrderDAO orderDAO, ProductDAO productDAO) {
        this.reviewDAO = reviewDAO;
        this.orderDAO = orderDAO;
        this.productDAO = productDAO;
    }

    @Override
    public ReviewDTO addReview(Long buyerId, Long orderId, Long productId, Integer rating, String comment) {
        if (buyerId == null || buyerId <= 0) {
            throw new AuthenticationException("Buyer must be logged in to leave a review");
        }

        ValidationUtil.validateReview(rating, comment);

        // Verify product exists
        Product product = productDAO.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productId));

        // Verify order exists and belongs to buyer
        Order order = orderDAO.findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));

        if (!order.getBuyerId().equals(buyerId)) {
            throw new AuthenticationException("You can only review products from your own orders");
        }

        // Verify product is in the order
        boolean productInOrder = order.getItems().stream()
            .anyMatch(item -> item.getProductId().equals(productId));
        if (!productInOrder) {
            throw new ValidationException("Product was not part of order #" + orderId);
        }

        // Check if review already exists for this order + product + buyer
        if (reviewDAO.findByOrderAndProductAndBuyer(orderId, productId, buyerId).isPresent()) {
            throw new ValidationException("You have already reviewed this product for order #" + orderId);
        }

        Review review = new Review();
        review.setOrderId(orderId);
        review.setProductId(productId);
        review.setBuyerId(buyerId);
        review.setRating(rating);
        review.setComment(comment != null ? comment.trim() : "");

        Review created = reviewDAO.create(review);
        ReviewDTO dto = ReviewDTO.fromEntity(created);
        dto.setProductName(product.getName());
        return dto;
    }

    @Override
    public List<ReviewDTO> getReviewsForProduct(Long productId) {
        if (productId == null || productId <= 0) {
            throw new ValidationException("Invalid product ID");
        }
        List<Review> reviews = reviewDAO.findByProductId(productId);
        List<ReviewDTO> dtos = new ArrayList<>();
        for (Review r : reviews) {
            dtos.add(ReviewDTO.fromEntity(r));
        }
        return dtos;
    }

    @Override
    public boolean canBuyerReviewProduct(Long buyerId, Long productId) {
        if (buyerId == null || productId == null) return false;
        return reviewDAO.hasBuyerPurchasedProduct(buyerId, productId);
    }

    @Override
    public double getAverageRating(Long productId) {
        if (productId == null) return 0.0;
        return reviewDAO.getAverageRating(productId);
    }
}
