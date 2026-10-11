package com.reshmamart.dao;

import com.reshmamart.model.CartItem;
import java.util.List;
import java.util.Optional;

public interface CartDAO {
    CartItem addItem(Long buyerId, Long productId, int quantity);
    boolean updateQuantity(Long buyerId, Long productId, int quantity);
    boolean removeItem(Long buyerId, Long productId);
    boolean removeById(Long cartItemId, Long buyerId);
    List<CartItem> getCartByBuyerId(Long buyerId);
    Optional<CartItem> findByBuyerAndProduct(Long buyerId, Long productId);
    boolean clearCart(Long buyerId);
    int countItemsInCart(Long buyerId);
}
