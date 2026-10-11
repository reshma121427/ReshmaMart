package com.reshmamart.dao;

import com.reshmamart.model.Review;
import java.util.List;
import java.util.Optional;

public interface ReviewDAO {
    Review create(Review review);
    List<Review> findByProductId(Long productId);
    boolean hasBuyerPurchasedProduct(Long buyerId, Long productId);
    Optional<Review> findByOrderAndProductAndBuyer(Long orderId, Long productId, Long buyerId);
    double getAverageRating(Long productId);
    int countReviewsForProduct(Long productId);
}
