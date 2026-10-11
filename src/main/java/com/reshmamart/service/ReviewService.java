package com.reshmamart.service;

import com.reshmamart.dto.ReviewDTO;
import java.util.List;

public interface ReviewService {
    ReviewDTO addReview(Long buyerId, Long orderId, Long productId, Integer rating, String comment);
    List<ReviewDTO> getReviewsForProduct(Long productId);
    boolean canBuyerReviewProduct(Long buyerId, Long productId);
    double getAverageRating(Long productId);
}
