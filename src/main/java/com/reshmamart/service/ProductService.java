package com.reshmamart.service;

import com.reshmamart.dto.ProductDTO;
import com.reshmamart.model.Product;
import java.math.BigDecimal;
import java.util.List;

public interface ProductService {
    ProductDTO createProduct(Long sellerId, String name, String description, BigDecimal price,
                             Integer stockQty, String category, String imageUrl);
    ProductDTO updateProduct(Long sellerId, Long productId, String name, String description,
                             BigDecimal price, Integer stockQty, String category, String imageUrl);
    boolean deleteProduct(Long sellerId, Long productId);
    boolean toggleProductStatus(Long productId, boolean isActive);
    ProductDTO getProductById(Long productId);
    List<ProductDTO> getProductsBySeller(Long sellerId);
    List<ProductDTO> searchProducts(String category, String keyword, String sortBy, int page, int pageSize);
    int countSearchProducts(String category, String keyword);
    List<String> getCategories();
    List<ProductDTO> getAllProductsForAdmin();
    int getTotalProductCount();
}
