package com.reshmamart.controller;

import com.reshmamart.dto.ApiResponse;
import com.reshmamart.dto.CartDTO;
import com.reshmamart.dto.OrderDTO;
import com.reshmamart.dto.ProductDTO;
import com.reshmamart.model.User;
import com.reshmamart.service.CartService;
import com.reshmamart.service.OrderService;
import com.reshmamart.service.ProductService;
import com.reshmamart.util.JsonUtil;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "ApiServlet", urlPatterns = {"/api/v1/products", "/api/v1/products/*", "/api/v1/cart", "/api/v1/orders"})
public class ApiServlet extends HttpServlet {

    private ProductService productService;
    private CartService cartService;
    private OrderService orderService;

    @Override
    public void init() throws ServletException {
        productService = (ProductService) getServletContext().getAttribute("productService");
        cartService = (CartService) getServletContext().getAttribute("cartService");
        orderService = (OrderService) getServletContext().getAttribute("orderService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        String uri = req.getRequestURI();

        if (uri.contains("/api/v1/products")) {
            String idParam = req.getParameter("id");
            if (idParam != null) {
                try {
                    Long id = Long.parseLong(idParam);
                    ProductDTO product = productService.getProductById(id);
                    resp.getWriter().write(JsonUtil.toJson(ApiResponse.success(product)));
                } catch (Exception e) {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    resp.getWriter().write(JsonUtil.toJson(ApiResponse.error("Product not found")));
                }
            } else {
                String category = req.getParameter("category");
                String keyword = req.getParameter("keyword");
                String sortBy = req.getParameter("sortBy");
                List<ProductDTO> products = productService.searchProducts(category, keyword, sortBy, 1, 50);
                resp.getWriter().write(JsonUtil.toJson(ApiResponse.success(products)));
            }
            return;
        }

        User currentUser = getCurrentUser(req);
        if (currentUser == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write(JsonUtil.toJson(ApiResponse.error("Authentication required")));
            return;
        }

        if (uri.contains("/api/v1/cart")) {
            CartDTO cart = cartService.getCart(currentUser.getId());
            resp.getWriter().write(JsonUtil.toJson(ApiResponse.success(cart)));
        } else if (uri.contains("/api/v1/orders")) {
            List<OrderDTO> orders = orderService.getOrdersByBuyer(currentUser.getId());
            resp.getWriter().write(JsonUtil.toJson(ApiResponse.success(orders)));
        } else {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write(JsonUtil.toJson(ApiResponse.error("Endpoint not found")));
        }
    }

    private User getCurrentUser(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return (session != null) ? (User) session.getAttribute("currentUser") : null;
    }
}
