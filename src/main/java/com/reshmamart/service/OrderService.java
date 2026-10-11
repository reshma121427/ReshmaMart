package com.reshmamart.service;

import com.reshmamart.dto.OrderDTO;
import com.reshmamart.model.OrderStatus;
import java.math.BigDecimal;
import java.util.List;

public interface OrderService {
    OrderDTO checkout(Long buyerId, String shippingAddress, String paymentMethod);
    OrderDTO getOrderById(Long orderId, Long userId, boolean isAdmin);
    List<OrderDTO> getOrdersByBuyer(Long buyerId);
    List<OrderDTO> getOrdersBySeller(Long sellerId);
    List<OrderDTO> getAllOrdersForAdmin();
    boolean updateOrderStatus(Long orderId, OrderStatus newStatus, Long requesterId, boolean isAdmin);
    int getTotalOrderCount();
    BigDecimal getTotalRevenue();
}
