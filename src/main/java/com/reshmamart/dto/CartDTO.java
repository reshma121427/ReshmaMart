package com.reshmamart.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CartDTO {
    private List<CartItemDTO> items = new ArrayList<>();
    private Integer totalItems = 0;
    private BigDecimal totalAmount = BigDecimal.ZERO;

    public CartDTO() {}

    public CartDTO(List<CartItemDTO> items) {
        this.items = items != null ? items : new ArrayList<>();
        calculateTotals();
    }

    public void calculateTotals() {
        int itemsCount = 0;
        BigDecimal sum = BigDecimal.ZERO;
        for (CartItemDTO item : items) {
            if (item.getQuantity() != null) {
                itemsCount += item.getQuantity();
            }
            if (item.getSubtotal() != null) {
                sum = sum.add(item.getSubtotal());
            }
        }
        this.totalItems = itemsCount;
        this.totalAmount = sum;
    }

    public List<CartItemDTO> getItems() { return items; }
    public void setItems(List<CartItemDTO> items) {
        this.items = items != null ? items : new ArrayList<>();
        calculateTotals();
    }

    public Integer getTotalItems() { return totalItems; }
    public void setTotalItems(Integer totalItems) { this.totalItems = totalItems; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
}
