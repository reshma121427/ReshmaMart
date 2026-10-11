package com.reshmamart.service;

import com.reshmamart.dto.CartDTO;

public interface CartService {
    CartDTO getCart(Long buyerId);
    CartDTO addToCart(Long buyerId, Long productId, int quantity);
    CartDTO updateCartItemQuantity(Long buyerId, Long productId, int quantity);
    CartDTO removeFromCart(Long buyerId, Long productId);
    void clearCart(Long buyerId);
    int getCartItemCount(Long buyerId);
}
