package com.reshmamart.controller;

import com.reshmamart.dto.OrderDTO;
import com.reshmamart.dto.ProductDTO;
import com.reshmamart.exception.ValidationException;
import com.reshmamart.model.OrderStatus;
import com.reshmamart.model.Role;
import com.reshmamart.model.User;
import com.reshmamart.service.OrderService;
import com.reshmamart.service.ProductService;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "SellerServlet", urlPatterns = {"/seller/*"})
public class SellerServlet extends HttpServlet {

    private ProductService productService;
    private OrderService orderService;

    @Override
    public void init() throws ServletException {
        productService = (ProductService) getServletContext().getAttribute("productService");
        orderService = (OrderService) getServletContext().getAttribute("orderService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User seller = getCurrentSeller(req);
        if (seller == null) {
            resp.sendRedirect(req.getContextPath() + "/auth/login");
            return;
        }

        String pathInfo = req.getPathInfo();
        if (pathInfo == null || "/dashboard".equals(pathInfo) || "/".equals(pathInfo)) {
            handleDashboard(req, resp, seller);
        } else if ("/product/new".equals(pathInfo)) {
            handleProductNewForm(req, resp);
        } else if ("/product/edit".equals(pathInfo)) {
            handleProductEditForm(req, resp, seller);
        } else if ("/orders".equals(pathInfo)) {
            handleSellerOrders(req, resp, seller);
        } else {
            resp.sendRedirect(req.getContextPath() + "/seller/dashboard");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User seller = getCurrentSeller(req);
        if (seller == null) {
            resp.sendRedirect(req.getContextPath() + "/auth/login");
            return;
        }

        String pathInfo = req.getPathInfo();
        if ("/product/new".equals(pathInfo)) {
            handleCreateProduct(req, resp, seller);
        } else if ("/product/edit".equals(pathInfo)) {
            handleUpdateProduct(req, resp, seller);
        } else if ("/product/delete".equals(pathInfo)) {
            handleDeleteProduct(req, resp, seller);
        } else if ("/orders/status".equals(pathInfo)) {
            handleUpdateOrderStatus(req, resp, seller);
        } else {
            resp.sendRedirect(req.getContextPath() + "/seller/dashboard");
        }
    }

    private void handleDashboard(HttpServletRequest req, HttpServletResponse resp, User seller)
            throws ServletException, IOException {
        List<ProductDTO> products = productService.getProductsBySeller(seller.getId());
        req.setAttribute("products", products);
        req.getRequestDispatcher("/WEB-INF/views/seller/dashboard.jsp").forward(req, resp);
    }

    private void handleProductNewForm(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setAttribute("categories", productService.getCategories());
        req.getRequestDispatcher("/WEB-INF/views/seller/product-form.jsp").forward(req, resp);
    }

    private void handleProductEditForm(HttpServletRequest req, HttpServletResponse resp, User seller)
            throws ServletException, IOException {
        try {
            Long productId = Long.parseLong(req.getParameter("id"));
            ProductDTO product = productService.getProductById(productId);
            if (!product.getSellerId().equals(seller.getId()) && seller.getRole() != Role.ADMIN) {
                resp.sendRedirect(req.getContextPath() + "/seller/dashboard?error=Unauthorized");
                return;
            }
            req.setAttribute("product", product);
            req.setAttribute("categories", productService.getCategories());
            req.setAttribute("isEdit", true);
            req.getRequestDispatcher("/WEB-INF/views/seller/product-form.jsp").forward(req, resp);
        } catch (Exception e) {
            resp.sendRedirect(req.getContextPath() + "/seller/dashboard?error=Invalid+Product");
        }
    }

    private void handleCreateProduct(HttpServletRequest req, HttpServletResponse resp, User seller)
            throws ServletException, IOException {
        String name = req.getParameter("name");
        String description = req.getParameter("description");
        String priceStr = req.getParameter("price");
        String stockStr = req.getParameter("stockQty");
        String category = req.getParameter("category");
        String imageUrl = req.getParameter("imageUrl");

        try {
            BigDecimal price = new BigDecimal(priceStr != null ? priceStr.trim() : "0");
            int stockQty = Integer.parseInt(stockStr != null ? stockStr.trim() : "0");

            productService.createProduct(seller.getId(), name, description, price, stockQty, category, imageUrl);
            resp.sendRedirect(req.getContextPath() + "/seller/dashboard?success=" +
                java.net.URLEncoder.encode("Product created successfully", "UTF-8"));
        } catch (ValidationException e) {
            req.setAttribute("errorMessage", e.getMessage());
            req.setAttribute("fieldErrors", e.getFieldErrors());
            req.setAttribute("name", name);
            req.setAttribute("description", description);
            req.setAttribute("price", priceStr);
            req.setAttribute("stockQty", stockStr);
            req.setAttribute("category", category);
            req.setAttribute("imageUrl", imageUrl);
            req.setAttribute("categories", productService.getCategories());
            req.getRequestDispatcher("/WEB-INF/views/seller/product-form.jsp").forward(req, resp);
        } catch (Exception e) {
            req.setAttribute("errorMessage", "Error creating product: " + e.getMessage());
            req.setAttribute("categories", productService.getCategories());
            req.getRequestDispatcher("/WEB-INF/views/seller/product-form.jsp").forward(req, resp);
        }
    }

    private void handleUpdateProduct(HttpServletRequest req, HttpServletResponse resp, User seller)
            throws ServletException, IOException {
        String idStr = req.getParameter("id");
        String name = req.getParameter("name");
        String description = req.getParameter("description");
        String priceStr = req.getParameter("price");
        String stockStr = req.getParameter("stockQty");
        String category = req.getParameter("category");
        String imageUrl = req.getParameter("imageUrl");

        try {
            Long productId = Long.parseLong(idStr);
            BigDecimal price = new BigDecimal(priceStr != null ? priceStr.trim() : "0");
            int stockQty = Integer.parseInt(stockStr != null ? stockStr.trim() : "0");

            productService.updateProduct(seller.getId(), productId, name, description, price, stockQty, category, imageUrl);
            resp.sendRedirect(req.getContextPath() + "/seller/dashboard?success=" +
                java.net.URLEncoder.encode("Product updated successfully", "UTF-8"));
        } catch (ValidationException e) {
            req.setAttribute("errorMessage", e.getMessage());
            req.setAttribute("fieldErrors", e.getFieldErrors());
            req.setAttribute("isEdit", true);
            req.setAttribute("categories", productService.getCategories());
            req.getRequestDispatcher("/WEB-INF/views/seller/product-form.jsp").forward(req, resp);
        } catch (Exception e) {
            resp.sendRedirect(req.getContextPath() + "/seller/dashboard?error=" +
                java.net.URLEncoder.encode("Failed to update product", "UTF-8"));
        }
    }

    private void handleDeleteProduct(HttpServletRequest req, HttpServletResponse resp, User seller) throws IOException {
        try {
            Long productId = Long.parseLong(req.getParameter("id"));
            productService.deleteProduct(seller.getId(), productId);
            resp.sendRedirect(req.getContextPath() + "/seller/dashboard?success=" +
                java.net.URLEncoder.encode("Product deleted successfully", "UTF-8"));
        } catch (Exception e) {
            resp.sendRedirect(req.getContextPath() + "/seller/dashboard?error=" +
                java.net.URLEncoder.encode(e.getMessage(), "UTF-8"));
        }
    }

    private void handleSellerOrders(HttpServletRequest req, HttpServletResponse resp, User seller)
            throws ServletException, IOException {
        List<OrderDTO> orders = orderService.getOrdersBySeller(seller.getId());
        req.setAttribute("orders", orders);
        req.getRequestDispatcher("/WEB-INF/views/seller/orders.jsp").forward(req, resp);
    }

    private void handleUpdateOrderStatus(HttpServletRequest req, HttpServletResponse resp, User seller) throws IOException {
        try {
            Long orderId = Long.parseLong(req.getParameter("orderId"));
            OrderStatus newStatus = OrderStatus.fromString(req.getParameter("status"));
            boolean isAdmin = seller.getRole() == Role.ADMIN;

            orderService.updateOrderStatus(orderId, newStatus, seller.getId(), isAdmin);
            resp.sendRedirect(req.getContextPath() + "/seller/orders?success=" +
                java.net.URLEncoder.encode("Order #" + orderId + " status updated to " + newStatus, "UTF-8"));
        } catch (Exception e) {
            resp.sendRedirect(req.getContextPath() + "/seller/orders?error=" +
                java.net.URLEncoder.encode(e.getMessage(), "UTF-8"));
        }
    }

    private User getCurrentSeller(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null) return null;
        User user = (User) session.getAttribute("currentUser");
        if (user != null && (user.getRole() == Role.SELLER || user.getRole() == Role.ADMIN)) {
            return user;
        }
        return null;
    }
}
