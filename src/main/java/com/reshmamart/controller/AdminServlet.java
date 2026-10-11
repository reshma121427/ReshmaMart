package com.reshmamart.controller;

import com.reshmamart.dto.OrderDTO;
import com.reshmamart.dto.ProductDTO;
import com.reshmamart.dto.UserResponseDTO;
import com.reshmamart.model.Role;
import com.reshmamart.model.User;
import com.reshmamart.service.OrderService;
import com.reshmamart.service.ProductService;
import com.reshmamart.service.UserService;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "AdminServlet", urlPatterns = {"/admin/*"})
public class AdminServlet extends HttpServlet {

    private UserService userService;
    private ProductService productService;
    private OrderService orderService;

    @Override
    public void init() throws ServletException {
        userService = (UserService) getServletContext().getAttribute("userService");
        productService = (ProductService) getServletContext().getAttribute("productService");
        orderService = (OrderService) getServletContext().getAttribute("orderService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User admin = getCurrentAdmin(req);
        if (admin == null) {
            resp.sendRedirect(req.getContextPath() + "/auth/login");
            return;
        }

        String pathInfo = req.getPathInfo();
        if (pathInfo == null || "/dashboard".equals(pathInfo) || "/".equals(pathInfo)) {
            handleDashboard(req, resp);
        } else if ("/users".equals(pathInfo)) {
            handleUsers(req, resp);
        } else if ("/orders".equals(pathInfo)) {
            handleOrders(req, resp);
        } else if ("/listings".equals(pathInfo)) {
            handleListings(req, resp);
        } else {
            resp.sendRedirect(req.getContextPath() + "/admin/dashboard");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User admin = getCurrentAdmin(req);
        if (admin == null) {
            resp.sendRedirect(req.getContextPath() + "/auth/login");
            return;
        }

        String pathInfo = req.getPathInfo();
        if ("/listings/toggle".equals(pathInfo)) {
            handleToggleListing(req, resp);
        } else {
            resp.sendRedirect(req.getContextPath() + "/admin/dashboard");
        }
    }

    private void handleDashboard(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int totalUsers = userService.getTotalUserCount();
        int totalProducts = productService.getTotalProductCount();
        int totalOrders = orderService.getTotalOrderCount();
        BigDecimal totalRevenue = orderService.getTotalRevenue();

        req.setAttribute("totalUsers", totalUsers);
        req.setAttribute("totalProducts", totalProducts);
        req.setAttribute("totalOrders", totalOrders);
        req.setAttribute("totalRevenue", totalRevenue);

        req.getRequestDispatcher("/WEB-INF/views/admin/dashboard.jsp").forward(req, resp);
    }

    private void handleUsers(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<UserResponseDTO> users = userService.getAllUsers();
        req.setAttribute("users", users);
        req.getRequestDispatcher("/WEB-INF/views/admin/users.jsp").forward(req, resp);
    }

    private void handleOrders(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<OrderDTO> orders = orderService.getAllOrdersForAdmin();
        req.setAttribute("orders", orders);
        req.getRequestDispatcher("/WEB-INF/views/admin/orders.jsp").forward(req, resp);
    }

    private void handleListings(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<ProductDTO> products = productService.getAllProductsForAdmin();
        req.setAttribute("products", products);
        req.getRequestDispatcher("/WEB-INF/views/admin/listings.jsp").forward(req, resp);
    }

    private void handleToggleListing(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            Long productId = Long.parseLong(req.getParameter("id"));
            boolean active = Boolean.parseBoolean(req.getParameter("active"));
            productService.toggleProductStatus(productId, active);
            resp.sendRedirect(req.getContextPath() + "/admin/listings?success=" +
                java.net.URLEncoder.encode("Product status updated", "UTF-8"));
        } catch (Exception e) {
            resp.sendRedirect(req.getContextPath() + "/admin/listings?error=" +
                java.net.URLEncoder.encode("Failed to update status", "UTF-8"));
        }
    }

    private User getCurrentAdmin(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null) return null;
        User user = (User) session.getAttribute("currentUser");
        if (user != null && user.getRole() == Role.ADMIN) {
            return user;
        }
        return null;
    }
}
