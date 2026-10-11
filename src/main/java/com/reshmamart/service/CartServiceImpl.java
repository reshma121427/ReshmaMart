package com.reshmamart.service;

import com.reshmamart.dao.CartDAO;
import com.reshmamart.dao.ProductDAO;
import com.reshmamart.dto.CartDTO;
import com.reshmamart.dto.CartItemDTO;
import com.reshmamart.exception.AuthenticationException;
import com.reshmamart.exception.ResourceNotFoundException;
import com.reshmamart.exception.ValidationException;
import com.reshmamart.model.CartItem;
import com.reshmamart.model.Product;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CartServiceImpl implements CartService {
    private final CartDAO cartDAO;
    private final ProductDAO productDAO;

    public CartServiceImpl(CartDAO cartDAO, ProductDAO productDAO) {
        this.cartDAO = cartDAO;
        this.productDAO = productDAO;
    }

    @Override
    public CartDTO getCart(Long buyerId) {
        if (buyerId == null || buyerId <= 0) {
            throw new AuthenticationException("Buyer must be logged in to view cart");
        }
        List<CartItem> items = cartDAO.getCartByBuyerId(buyerId);
        List<CartItemDTO> dtos = new ArrayList<>();
        for (CartItem item : items) {
            dtos.add(CartItemDTO.fromEntity(item));
        }
        return new CartDTO(dtos);
    }

    @Override
    public CartDTO addToCart(Long buyerId, Long productId, int quantity) {
        if (buyerId == null || buyerId <= 0) {
            throw new AuthenticationException("Buyer must be logged in to add items to cart");
        }
        if (productId == null || productId <= 0) {
            throw new ValidationException("Invalid product ID");
        }
        if (quantity <= 0) {
            throw new ValidationException("Quantity must be at least 1");
        }

        Product product = productDAO.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productId));

        if (!Boolean.TRUE.equals(product.getIsActive())) {
            throw new ValidationException("This product is currently unavailable");
        }

        if (product.getStockQty() < quantity) {
            throw new ValidationException("Cannot add " + quantity + " items. Only " + product.getStockQty() + " left in stock.");
        }

        // Check if existing quantity in cart + new quantity exceeds stock
        Optional<CartItem> existing = cartDAO.findByBuyerAndProduct(buyerId, productId);
        if (existing.isPresent()) {
            int totalDesired = existing.get().getQuantity() + quantity;
            if (totalDesired > product.getStockQty()) {
                throw new ValidationException("Cannot add more. You already have " + existing.get().getQuantity() +
                    " in cart and only " + product.getStockQty() + " are available.");
            }
        }

        cartDAO.addItem(buyerId, productId, quantity);
        return getCart(buyerId);
    }

    @Override
    public CartDTO updateCartItemQuantity(Long buyerId, Long productId, int quantity) {
        if (buyerId == null || buyerId <= 0) {
            throw new AuthenticationException("Buyer authentication required");
        }
        if (productId == null || productId <= 0) {
            throw new ValidationException("Invalid product ID");
        }

        if (quantity <= 0) {
            cartDAO.removeItem(buyerId, productId);
            return getCart(buyerId);
        }

        Product product = productDAO.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productId));

        if (quantity > product.getStockQty()) {
            throw new ValidationException("Requested quantity (" + quantity + ") exceeds available stock (" + product.getStockQty() + ")");
        }

        cartDAO.updateQuantity(buyerId, productId, quantity);
        return getCart(buyerId);
    }

    @Override
    public CartDTO removeFromCart(Long buyerId, Long productId) {
        if (buyerId == null || buyerId <= 0) {
            throw new AuthenticationException("Buyer authentication required");
        }
        cartDAO.removeItem(buyerId, productId);
        return getCart(buyerId);
    }

    @Override
    public void clearCart(Long buyerId) {
        if (buyerId == null || buyerId <= 0) {
            throw new AuthenticationException("Buyer authentication required");
        }
        cartDAO.clearCart(buyerId);
    }

    @Override
    public int getCartItemCount(Long buyerId) {
        if (buyerId == null || buyerId <= 0) {
            return 0;
        }
        return cartDAO.countItemsInCart(buyerId);
    }
}
