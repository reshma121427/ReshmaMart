package com.reshmamart.service;

import com.reshmamart.dao.CartDAO;
import com.reshmamart.dao.OrderDAO;
import com.reshmamart.dao.ProductDAO;
import com.reshmamart.dto.OrderDTO;
import com.reshmamart.exception.AuthenticationException;
import com.reshmamart.exception.ResourceNotFoundException;
import com.reshmamart.exception.ValidationException;
import com.reshmamart.model.CartItem;
import com.reshmamart.model.Order;
import com.reshmamart.model.OrderStatus;
import com.reshmamart.model.Product;
import com.reshmamart.util.ValidationUtil;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class OrderServiceImpl implements OrderService {
    private final OrderDAO orderDAO;
    private final CartDAO cartDAO;
    private final ProductDAO productDAO;

    public OrderServiceImpl(OrderDAO orderDAO, CartDAO cartDAO, ProductDAO productDAO) {
        this.orderDAO = orderDAO;
        this.cartDAO = cartDAO;
        this.productDAO = productDAO;
    }

    @Override
    public OrderDTO checkout(Long buyerId, String shippingAddress, String paymentMethod) {
        if (buyerId == null || buyerId <= 0) {
            throw new AuthenticationException("Buyer must be logged in to checkout");
        }

        ValidationUtil.validateShippingAddress(shippingAddress);

        // Fetch buyer's cart items
        List<CartItem> cartItems = cartDAO.getCartByBuyerId(buyerId);
        if (cartItems.isEmpty()) {
            throw new ValidationException("Cannot checkout with an empty cart");
        }

        // Verify stock for each item in the cart before placing order
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (CartItem item : cartItems) {
            Product product = productDAO.findById(item.getProductId())
                .orElseThrow(() -> new ValidationException("Product not found: " + item.getProductName()));

            if (!Boolean.TRUE.equals(product.getIsActive())) {
                throw new ValidationException("Product is no longer available: " + product.getName());
            }

            if (product.getStockQty() < item.getQuantity()) {
                throw new ValidationException("Insufficient stock for '" + product.getName() + "'. Available: " +
                    product.getStockQty() + ", in cart: " + item.getQuantity());
            }

            totalAmount = totalAmount.add(item.getSubtotal());
        }

        // Atomic order creation via DAO
        Order createdOrder = orderDAO.createOrderWithItems(buyerId, shippingAddress.trim(), totalAmount, cartItems);

        return OrderDTO.fromEntity(createdOrder);
    }

    @Override
    public OrderDTO getOrderById(Long orderId, Long userId, boolean isAdmin) {
        if (orderId == null || orderId <= 0) {
            throw new ValidationException("Invalid order ID");
        }
        Order order = orderDAO.findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));

        // Security check: allow if admin or buyer who placed the order or seller of an item
        if (!isAdmin && !order.getBuyerId().equals(userId)) {
            boolean isSellerOfItem = order.getItems().stream()
                .anyMatch(item -> item.getSellerId() != null && item.getSellerId().equals(userId));
            if (!isSellerOfItem) {
                throw new AuthenticationException("You are not authorized to view this order");
            }
        }

        return OrderDTO.fromEntity(order);
    }

    @Override
    public List<OrderDTO> getOrdersByBuyer(Long buyerId) {
        if (buyerId == null || buyerId <= 0) {
            throw new AuthenticationException("Buyer authentication required");
        }
        List<Order> orders = orderDAO.findByBuyerId(buyerId);
        List<OrderDTO> dtos = new ArrayList<>();
        for (Order o : orders) {
            dtos.add(OrderDTO.fromEntity(o));
        }
        return dtos;
    }

    @Override
    public List<OrderDTO> getOrdersBySeller(Long sellerId) {
        if (sellerId == null || sellerId <= 0) {
            throw new AuthenticationException("Seller authentication required");
        }
        List<Order> orders = orderDAO.findBySellerId(sellerId);
        List<OrderDTO> dtos = new ArrayList<>();
        for (Order o : orders) {
            dtos.add(OrderDTO.fromEntity(o));
        }
        return dtos;
    }

    @Override
    public List<OrderDTO> getAllOrdersForAdmin() {
        List<Order> orders = orderDAO.findAll();
        List<OrderDTO> dtos = new ArrayList<>();
        for (Order o : orders) {
            dtos.add(OrderDTO.fromEntity(o));
        }
        return dtos;
    }

    @Override
    public boolean updateOrderStatus(Long orderId, OrderStatus newStatus, Long requesterId, boolean isAdmin) {
        if (orderId == null || orderId <= 0) {
            throw new ValidationException("Invalid order ID");
        }
        if (newStatus == null) {
            throw new ValidationException("New order status is required");
        }

        Order existing = orderDAO.findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));

        if (!isAdmin) {
            boolean isSellerOfItem = existing.getItems().stream()
                .anyMatch(item -> item.getSellerId() != null && item.getSellerId().equals(requesterId));
            if (!isSellerOfItem) {
                throw new AuthenticationException("You are not authorized to update this order's status");
            }
        }

        // Validate lifecycle transition (O2 workflow: PENDING -> CONFIRMED -> SHIPPED -> DELIVERED)
        if (existing.getStatus() == OrderStatus.DELIVERED && newStatus != OrderStatus.DELIVERED) {
            throw new ValidationException("Cannot modify status of an already delivered order");
        }
        if (existing.getStatus() == OrderStatus.CANCELLED) {
            throw new ValidationException("Cannot modify status of a cancelled order");
        }

        return orderDAO.updateStatus(orderId, newStatus);
    }

    @Override
    public int getTotalOrderCount() {
        return orderDAO.countAll();
    }

    @Override
    public BigDecimal getTotalRevenue() {
        return orderDAO.calculateTotalRevenue();
    }
}
