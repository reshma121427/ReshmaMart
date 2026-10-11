package com.reshmamart.controller;

import com.reshmamart.dto.OrderDTO;
import com.reshmamart.model.Role;
import com.reshmamart.model.User;
import com.reshmamart.service.OrderService;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "OrderServlet", urlPatterns = {"/orders", "/orders/*"})
public class OrderServlet extends HttpServlet {

    private OrderService orderService;

    @Override
    public void init() throws ServletException {
        orderService = (OrderService) getServletContext().getAttribute("orderService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User currentUser = getCurrentUser(req);
        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/auth/login?redirect=" +
                java.net.URLEncoder.encode(req.getRequestURI(), "UTF-8"));
            return;
        }

        String pathInfo = req.getPathInfo();
        if ("/detail".equals(pathInfo)) {
            handleOrderDetail(req, resp, currentUser);
        } else {
            handleOrderList(req, resp, currentUser);
        }
    }

    private void handleOrderList(HttpServletRequest req, HttpServletResponse resp, User currentUser)
            throws ServletException, IOException {
        List<OrderDTO> orders = orderService.getOrdersByBuyer(currentUser.getId());
        req.setAttribute("orders", orders);
        req.getRequestDispatcher("/WEB-INF/views/buyer/orders.jsp").forward(req, resp);
    }

    private void handleOrderDetail(HttpServletRequest req, HttpServletResponse resp, User currentUser)
            throws ServletException, IOException {
        try {
            Long orderId = Long.parseLong(req.getParameter("id"));
            boolean isAdmin = currentUser.getRole() == Role.ADMIN;
            OrderDTO order = orderService.getOrderById(orderId, currentUser.getId(), isAdmin);

            req.setAttribute("order", order);
            req.setAttribute("isNewlyPlaced", "true".equals(req.getParameter("placed")));
            req.getRequestDispatcher("/WEB-INF/views/buyer/order-detail.jsp").forward(req, resp);
        } catch (Exception e) {
            resp.sendRedirect(req.getContextPath() + "/orders?error=" +
                java.net.URLEncoder.encode("Order not found or access denied", "UTF-8"));
        }
    }

    private User getCurrentUser(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return (session != null) ? (User) session.getAttribute("currentUser") : null;
    }
}
