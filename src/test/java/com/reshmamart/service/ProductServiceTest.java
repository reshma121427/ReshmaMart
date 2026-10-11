package com.reshmamart.service;

import com.reshmamart.dao.ProductDAO;
import com.reshmamart.dto.ProductDTO;
import com.reshmamart.exception.AuthenticationException;
import com.reshmamart.exception.ValidationException;
import com.reshmamart.model.Product;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductDAO productDAO;

    private ProductService productService;

    @BeforeEach
    public void setUp() {
        productService = new ProductServiceImpl(productDAO);
    }

    @Test
    public void testCreateProductSuccess() {
        Product mockProduct = new Product(10L, 5L, "Gaming Headset", "Surround sound", new BigDecimal("59.99"), 15, "Electronics", "headset.jpg", true, new Timestamp(System.currentTimeMillis()));
        when(productDAO.create(any(Product.class))).thenReturn(mockProduct);

        ProductDTO result = productService.createProduct(5L, "Gaming Headset", "Surround sound", new BigDecimal("59.99"), 15, "Electronics", "headset.jpg");

        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals("Gaming Headset", result.getName());
        assertEquals(new BigDecimal("59.99"), result.getPrice());
        verify(productDAO).create(any(Product.class));
    }

    @Test
    public void testCreateProductInvalidPriceThrowsException() {
        assertThrows(ValidationException.class, () -> {
            productService.createProduct(5L, "Invalid Item", "Desc", new BigDecimal("-10.00"), 5, "Electronics", null);
        });
        verify(productDAO, never()).create(any(Product.class));
    }

    @Test
    public void testUpdateProductUnauthorizedSellerThrowsException() {
        Product existing = new Product(10L, 5L, "Item", "Desc", new BigDecimal("20.00"), 5, "Electronics", null, true, null);
        when(productDAO.findById(10L)).thenReturn(Optional.of(existing));

        // Different seller (99L) attempts to update product owned by 5L
        assertThrows(AuthenticationException.class, () -> {
            productService.updateProduct(99L, 10L, "New Name", "Desc", new BigDecimal("25.00"), 10, "Electronics", null);
        });

        verify(productDAO, never()).update(any(Product.class));
    }
}
