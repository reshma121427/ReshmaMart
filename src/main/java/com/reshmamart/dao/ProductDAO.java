package com.reshmamart.dao;

import com.reshmamart.model.Product;
import java.util.List;
import java.util.Optional;

public interface ProductDAO {
    Product create(Product product);
    boolean update(Product product);
    boolean delete(Long id);
    boolean setActiveStatus(Long id, boolean isActive);
    Optional<Product> findById(Long id);
    List<Product> findBySellerId(Long sellerId);
    List<Product> searchProducts(String category, String keyword, String sortBy, int limit, int offset);
    int countSearchProducts(String category, String keyword);
    List<String> findAllCategories();
    boolean updateStock(Long productId, int quantityDelta);
    int countAll();
    List<Product> findAllForAdmin();
}
