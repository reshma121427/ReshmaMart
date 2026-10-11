package com.reshmamart.dto;

import com.reshmamart.model.Review;
import java.sql.Timestamp;

public class ReviewDTO {
    private Long id;
    private Long orderId;
    private Long productId;
    private Long buyerId;
    private String buyerName;
    private String productName;
    private Integer rating;
    private String comment;
    private Timestamp createdAt;

    public ReviewDTO() {}

    public static ReviewDTO fromEntity(Review r) {
        if (r == null) return null;
        ReviewDTO dto = new ReviewDTO();
        dto.setId(r.getId());
        dto.setOrderId(r.getOrderId());
        dto.setProductId(r.getProductId());
        dto.setBuyerId(r.getBuyerId());
        dto.setBuyerName(r.getBuyerName());
        dto.setProductName(r.getProductName());
        dto.setRating(r.getRating());
        dto.setComment(r.getComment());
        dto.setCreatedAt(r.getCreatedAt());
        return dto;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public Long getBuyerId() { return buyerId; }
    public void setBuyerId(Long buyerId) { this.buyerId = buyerId; }

    public String getBuyerName() { return buyerName; }
    public void setBuyerName(String buyerName) { this.buyerName = buyerName; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
