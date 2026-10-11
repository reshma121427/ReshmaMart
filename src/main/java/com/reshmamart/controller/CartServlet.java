package com.reshmamart.controller;

import com.reshmamart.dto.CartDTO;
import com.reshmamart.exception.ValidationException;
import com.reshmamart.model.User;
import com.reshmamart.service.CartService;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "CartServlet", urlPatterns = {"/cart", "/cart/*"})
public class CartServlet extends HttpServlet {

    private CartService cartService;

    @Override
    public void init() throws ServletException {
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
        req.setAttribute("cart", cart);
        req.getRequestDispatcher("/WEB-INF/views/buyer/cart.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User currentUser = getCurrentUser(req);
        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/auth/login");
            return;
        }

        String pathInfo = req.getPathInfo();
        try {
            if ("/add".equals(pathInfo)) {
                Long productId = Long.parseLong(req.getParameter("productId"));
                int quantity = Integer.parseInt(req.getParameter("quantity"));
                cartService.addToCart(currentUser.getId(), productId, quantity);
                resp.sendRedirect(req.getContextPath() + "/cart?success=" +
                    java.net.URLEncoder.encode("Item added to cart", "UTF-8"));

            } else if ("/update".equals(pathInfo)) {
                Long productId = Long.parseLong(req.getParameter("productId"));
                int quantity = Integer.parseInt(req.getParameter("quantity"));
                cartService.updateCartItemQuantity(currentUser.getId(), productId, quantity);
                resp.sendRedirect(req.getContextPath() + "/cart");

            } else if ("/remove".equals(pathInfo)) {
                Long productId = Long.parseLong(req.getParameter("productId"));
                cartService.removeFromCart(currentUser.getId(), productId);
                resp.sendRedirect(req.getContextPath() + "/cart?success=" +
                    java.net.URLEncoder.encode("Item removed from cart", "UTF-8"));

            } else if ("/clear".equals(pathInfo)) {
                cartService.clearCart(currentUser.getId());
                resp.sendRedirect(req.getContextPath() + "/cart");

            } else {
                resp.sendRedirect(req.getContextPath() + "/cart");
            }
        } catch (ValidationException e) {
            resp.sendRedirect(req.getContextPath() + "/cart?error=" +
                java.net.URLEncoder.encode(e.getMessage(), "UTF-8"));
        } catch (Exception e) {
            resp.sendRedirect(req.getContextPath() + "/cart?error=" +
                java.net.URLEncoder.encode("Invalid request", "UTF-8"));
        }
    }

    private User getCurrentUser(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return (session != null) ? (User) session.getAttribute("currentUser") : null;
    }
}
