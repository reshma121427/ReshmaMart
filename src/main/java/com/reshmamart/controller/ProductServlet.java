package com.reshmamart.controller;

import com.reshmamart.dto.ProductDTO;
import com.reshmamart.dto.ReviewDTO;
import com.reshmamart.model.User;
import com.reshmamart.service.ProductService;
import com.reshmamart.service.ReviewService;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "ProductServlet", urlPatterns = {"/products", "/products/*", ""})
public class ProductServlet extends HttpServlet {

    private ProductService productService;
    private ReviewService reviewService;

    @Override
    public void init() throws ServletException {
        productService = (ProductService) getServletContext().getAttribute("productService");
        reviewService = (ReviewService) getServletContext().getAttribute("reviewService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo != null && pathInfo.equals("/detail")) {
            handleProductDetail(req, resp);
        } else {
            handleProductCatalog(req, resp);
        }
    }

    private void handleProductCatalog(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String category = req.getParameter("category");
        String keyword = req.getParameter("keyword");
        String sortBy = req.getParameter("sortBy");

        int page = 1;
        int pageSize = 12;
        try {
            String pageParam = req.getParameter("page");
            if (pageParam != null) {
                page = Integer.parseInt(pageParam);
                if (page < 1) page = 1;
            }
        } catch (NumberFormatException e) {
            page = 1;
        }

        List<ProductDTO> products = productService.searchProducts(category, keyword, sortBy, page, pageSize);
        int totalProducts = productService.countSearchProducts(category, keyword);
        int totalPages = (int) Math.ceil((double) totalProducts / pageSize);
        List<String> categories = productService.getCategories();

        req.setAttribute("products", products);
        req.setAttribute("categories", categories);
        req.setAttribute("selectedCategory", category != null ? category : "ALL");
        req.setAttribute("keyword", keyword != null ? keyword : "");
        req.setAttribute("sortBy", sortBy != null ? sortBy : "newest");
        req.setAttribute("currentPage", page);
        req.setAttribute("totalPages", totalPages);
        req.setAttribute("totalProducts", totalProducts);

        req.getRequestDispatcher("/WEB-INF/views/buyer/products.jsp").forward(req, resp);
    }

    private void handleProductDetail(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Long productId = Long.parseLong(req.getParameter("id"));
            ProductDTO product = productService.getProductById(productId);
            List<ReviewDTO> reviews = reviewService.getReviewsForProduct(productId);
            double avgRating = reviewService.getAverageRating(productId);

            boolean canReview = false;
            HttpSession session = req.getSession(false);
            if (session != null) {
                User user = (User) session.getAttribute("currentUser");
                if (user != null) {
                    canReview = reviewService.canBuyerReviewProduct(user.getId(), productId);
                }
            }

            req.setAttribute("product", product);
            req.setAttribute("reviews", reviews);
            req.setAttribute("avgRating", avgRating);
            req.setAttribute("canReview", canReview);

            req.getRequestDispatcher("/WEB-INF/views/buyer/product-detail.jsp").forward(req, resp);
        } catch (Exception e) {
            resp.sendRedirect(req.getContextPath() + "/products?error=" + java.net.URLEncoder.encode("Product not found", "UTF-8"));
        }
    }
}
