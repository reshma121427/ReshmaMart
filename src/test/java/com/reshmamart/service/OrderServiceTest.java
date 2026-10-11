package com.reshmamart.service;

import com.reshmamart.dao.CartDAO;
import com.reshmamart.dao.OrderDAO;
import com.reshmamart.dao.ProductDAO;
import com.reshmamart.dto.OrderDTO;
import com.reshmamart.exception.ValidationException;
import com.reshmamart.model.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderDAO orderDAO;
    @Mock
    private CartDAO cartDAO;
    @Mock
    private ProductDAO productDAO;

    private OrderService orderService;

    @BeforeEach
    public void setUp() {
        orderService = new OrderServiceImpl(orderDAO, cartDAO, productDAO);
    }

    @Test
    public void testCheckoutEmptyCartThrowsException() {
        when(cartDAO.getCartByBuyerId(1L)).thenReturn(Collections.emptyList());

        ValidationException ex = assertThrows(ValidationException.class, () -> {
            orderService.checkout(1L, "123 Main Street, City", "MOCK_CARD");
        });

        assertTrue(ex.getMessage().contains("empty"));
        verify(orderDAO, never()).createOrderWithItems(any(), any(), any(), any());
    }

    @Test
    public void testCheckoutSuccess() {
        CartItem item = new CartItem(1L, 1L, 10L, 2, new Timestamp(System.currentTimeMillis()));
        item.setProductName("Coffee Mug");
        item.setProductPrice(new BigDecimal("15.00"));
        List<CartItem> cartItems = List.of(item);

        Product product = new Product(10L, 2L, "Coffee Mug", "Ceramic mug", new BigDecimal("15.00"), 10, "Home", null, true, null);

        when(cartDAO.getCartByBuyerId(1L)).thenReturn(cartItems);
        when(productDAO.findById(10L)).thenReturn(Optional.of(product));

        Order mockOrder = new Order(100L, 1L, new BigDecimal("30.00"), OrderStatus.PENDING, "123 Main St, City", "COMPLETED", new Timestamp(System.currentTimeMillis()));
        mockOrder.setItems(List.of(new OrderItem(1L, 100L, 10L, 2, new BigDecimal("15.00"), null)));
        when(orderDAO.createOrderWithItems(eq(1L), eq("123 Main St, City"), eq(new BigDecimal("30.00")), eq(cartItems)))
            .thenReturn(mockOrder);

        OrderDTO orderDTO = orderService.checkout(1L, "123 Main St, City", "MOCK_CARD");

        assertNotNull(orderDTO);
        assertEquals(100L, orderDTO.getId());
        assertEquals(OrderStatus.PENDING, orderDTO.getStatus());
        assertEquals(new BigDecimal("30.00"), orderDTO.getTotalAmount());
    }

    @Test
    public void testUpdateStatusValid() {
        Order existing = new Order(100L, 1L, new BigDecimal("30.00"), OrderStatus.PENDING, "123 Main St", "COMPLETED", null);
        OrderItem item = new OrderItem(1L, 100L, 10L, 1, new BigDecimal("30.00"), null);
        item.setSellerId(5L);
        existing.setItems(List.of(item));

        when(orderDAO.findById(100L)).thenReturn(Optional.of(existing));
        when(orderDAO.updateStatus(100L, OrderStatus.CONFIRMED)).thenReturn(true);

        boolean updated = orderService.updateOrderStatus(100L, OrderStatus.CONFIRMED, 5L, false);
        assertTrue(updated);
        verify(orderDAO).updateStatus(100L, OrderStatus.CONFIRMED);
    }
}
