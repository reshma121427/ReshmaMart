package com.reshmamart.controller;

import com.reshmamart.exception.ValidationException;
import com.reshmamart.model.User;
import com.reshmamart.service.ReviewService;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "ReviewServlet", urlPatterns = {"/reviews/submit"})
public class ReviewServlet extends HttpServlet {

    private ReviewService reviewService;

    @Override
    public void init() throws ServletException {
        reviewService = (ReviewService) getServletContext().getAttribute("reviewService");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("currentUser") : null;
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/auth/login");
            return;
        }

        String orderIdStr = req.getParameter("orderId");
        String productIdStr = req.getParameter("productId");
        String ratingStr = req.getParameter("rating");
        String comment = req.getParameter("comment");

        try {
            Long orderId = Long.parseLong(orderIdStr);
            Long productId = Long.parseLong(productIdStr);
            int rating = Integer.parseInt(ratingStr);

            reviewService.addReview(user.getId(), orderId, productId, rating, comment);
            resp.sendRedirect(req.getContextPath() + "/products/detail?id=" + productId + "&reviewed=true");
        } catch (ValidationException e) {
            resp.sendRedirect(req.getContextPath() + "/products/detail?id=" + productIdStr + "&error=" +
                java.net.URLEncoder.encode(e.getMessage(), "UTF-8"));
        } catch (Exception e) {
            resp.sendRedirect(req.getContextPath() + "/products?error=" +
                java.net.URLEncoder.encode("Error submitting review: " + e.getMessage(), "UTF-8"));
        }
    }
}
