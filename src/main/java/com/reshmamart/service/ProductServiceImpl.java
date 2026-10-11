package com.reshmamart.service;

import com.reshmamart.dao.ProductDAO;
import com.reshmamart.dto.ProductDTO;
import com.reshmamart.exception.AuthenticationException;
import com.reshmamart.exception.ResourceNotFoundException;
import com.reshmamart.exception.ValidationException;
import com.reshmamart.model.Product;
import com.reshmamart.util.ValidationUtil;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ProductServiceImpl implements ProductService {
    private final ProductDAO productDAO;

    public ProductServiceImpl(ProductDAO productDAO) {
        this.productDAO = productDAO;
    }

    @Override
    public ProductDTO createProduct(Long sellerId, String name, String description, BigDecimal price,
                                    Integer stockQty, String category, String imageUrl) {
        if (sellerId == null || sellerId <= 0) {
            throw new AuthenticationException("Seller must be logged in to create a product");
        }

        ValidationUtil.validateProduct(name, description, price, stockQty, category);

        Product product = new Product();
        product.setSellerId(sellerId);
        product.setName(name.trim());
        product.setDescription(description != null ? description.trim() : "");
        product.setPrice(price);
        product.setStockQty(stockQty);
        product.setCategory(category.trim());
        product.setImageUrl(imageUrl != null && !imageUrl.trim().isEmpty() ? imageUrl.trim() : "static/images/placeholder.png");
        product.setIsActive(true);

        Product created = productDAO.create(product);
        return ProductDTO.fromEntity(created);
    }

    @Override
    public ProductDTO updateProduct(Long sellerId, Long productId, String name, String description,
                                    BigDecimal price, Integer stockQty, String category, String imageUrl) {
        if (sellerId == null || sellerId <= 0) {
            throw new AuthenticationException("Seller authentication required");
        }
        if (productId == null || productId <= 0) {
            throw new ValidationException("Invalid product ID");
        }

        ValidationUtil.validateProduct(name, description, price, stockQty, category);

        Product existing = productDAO.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productId));

        if (!existing.getSellerId().equals(sellerId)) {
            throw new AuthenticationException("You are not authorized to edit this product listing");
        }

        existing.setName(name.trim());
        existing.setDescription(description != null ? description.trim() : "");
        existing.setPrice(price);
        existing.setStockQty(stockQty);
        existing.setCategory(category.trim());
        if (imageUrl != null && !imageUrl.trim().isEmpty()) {
            existing.setImageUrl(imageUrl.trim());
        }

        boolean updated = productDAO.update(existing);
        if (!updated) {
            throw new ValidationException("Failed to update product");
        }

        return ProductDTO.fromEntity(existing);
    }

    @Override
    public boolean deleteProduct(Long sellerId, Long productId) {
        if (sellerId == null || sellerId <= 0) {
            throw new AuthenticationException("Seller authentication required");
        }
        Product existing = productDAO.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productId));

        if (!existing.getSellerId().equals(sellerId)) {
            throw new AuthenticationException("You are not authorized to delete this product listing");
        }

        return productDAO.delete(productId);
    }

    @Override
    public boolean toggleProductStatus(Long productId, boolean isActive) {
        if (productId == null || productId <= 0) {
            throw new ValidationException("Invalid product ID");
        }
        return productDAO.setActiveStatus(productId, isActive);
    }

    @Override
    public ProductDTO getProductById(Long productId) {
        if (productId == null || productId <= 0) {
            throw new ValidationException("Invalid product ID");
        }
        Product p = productDAO.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productId));
        return ProductDTO.fromEntity(p);
    }

    @Override
    public List<ProductDTO> getProductsBySeller(Long sellerId) {
        if (sellerId == null || sellerId <= 0) {
            throw new ValidationException("Invalid seller ID");
        }
        List<Product> products = productDAO.findBySellerId(sellerId);
        List<ProductDTO> dtos = new ArrayList<>();
        for (Product p : products) {
            dtos.add(ProductDTO.fromEntity(p));
        }
        return dtos;
    }

    @Override
    public List<ProductDTO> searchProducts(String category, String keyword, String sortBy, int page, int pageSize) {
        int limit = pageSize > 0 ? pageSize : 12;
        int offset = page > 1 ? (page - 1) * limit : 0;

        List<Product> products = productDAO.searchProducts(category, keyword, sortBy, limit, offset);
        List<ProductDTO> dtos = new ArrayList<>();
        for (Product p : products) {
            dtos.add(ProductDTO.fromEntity(p));
        }
        return dtos;
    }

    @Override
    public int countSearchProducts(String category, String keyword) {
        return productDAO.countSearchProducts(category, keyword);
    }

    @Override
    public List<String> getCategories() {
        return productDAO.findAllCategories();
    }

    @Override
    public List<ProductDTO> getAllProductsForAdmin() {
        List<Product> products = productDAO.findAllForAdmin();
        List<ProductDTO> dtos = new ArrayList<>();
        for (Product p : products) {
            dtos.add(ProductDTO.fromEntity(p));
        }
        return dtos;
    }

    @Override
    public int getTotalProductCount() {
        return productDAO.countAll();
    }
}
