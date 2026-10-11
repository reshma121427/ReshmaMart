package com.reshmamart.controller;

import com.reshmamart.dto.CartDTO;
import com.reshmamart.dto.OrderDTO;
import com.reshmamart.exception.ValidationException;
import com.reshmamart.model.User;
import com.reshmamart.service.CartService;
import com.reshmamart.service.OrderService;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "CheckoutServlet", urlPatterns = {"/checkout"})
public class CheckoutServlet extends HttpServlet {

    private OrderService orderService;
    private CartService cartService;

    @Override
    public void init() throws ServletException {
        orderService = (OrderService) getServletContext().getAttribute("orderService");
        cartService = (CartService) getServletContext().getAttribute("cartService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User currentUser = getCurrentUser(req);
        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/auth/login?redirect=" +
                java.net.URLEncoder.encode(req.getRequestURI(), "UTF-8"));
            return;
        }

        CartDTO cart = cartService.getCart(currentUser.getId());
        if (cart.getItems().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/cart?error=" +
                java.net.URLEncoder.encode("Your cart is empty. Add items before checking out.", "UTF-8"));
            return;
        }

        req.setAttribute("cart", cart);
        req.getRequestDispatcher("/WEB-INF/views/buyer/checkout.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User currentUser = getCurrentUser(req);
        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/auth/login");
            return;
        }

        String shippingAddress = req.getParameter("shippingAddress");
        String paymentMethod = req.getParameter("paymentMethod");

        try {
            OrderDTO order = orderService.checkout(currentUser.getId(), shippingAddress, paymentMethod);
            resp.sendRedirect(req.getContextPath() + "/orders/detail?id=" + order.getId() + "&placed=true");
        } catch (ValidationException e) {
            CartDTO cart = cartService.getCart(currentUser.getId());
            req.setAttribute("cart", cart);
            req.setAttribute("errorMessage", e.getMessage());
            req.setAttribute("fieldErrors", e.getFieldErrors());
            req.setAttribute("shippingAddress", shippingAddress);
            req.setAttribute("paymentMethod", paymentMethod);
            req.getRequestDispatcher("/WEB-INF/views/buyer/checkout.jsp").forward(req, resp);
        } catch (Exception e) {
            CartDTO cart = cartService.getCart(currentUser.getId());
            req.setAttribute("cart", cart);
            req.setAttribute("errorMessage", "An error occurred while processing your order: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/buyer/checkout.jsp").forward(req, resp);
        }
    }

    private User getCurrentUser(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return (session != null) ? (User) session.getAttribute("currentUser") : null;
    }
}
