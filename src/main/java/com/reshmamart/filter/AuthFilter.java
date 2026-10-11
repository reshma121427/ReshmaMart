package com.reshmamart.filter;

import com.reshmamart.model.Role;
import com.reshmamart.model.User;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebFilter(filterName = "AuthFilter", urlPatterns = {"/*"})
public class AuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String path = httpRequest.getRequestURI().substring(httpRequest.getContextPath().length());

        // Check if route requires protection
        boolean isProtected = false;
        Role requiredRole = null;

        if (path.startsWith("/admin")) {
            isProtected = true;
            requiredRole = Role.ADMIN;
        } else if (path.startsWith("/seller")) {
            isProtected = true;
            requiredRole = Role.SELLER;
        } else if (path.startsWith("/cart") || path.startsWith("/checkout") ||
                   path.startsWith("/orders") || path.startsWith("/reviews/submit") ||
                   path.startsWith("/api/v1/cart") || path.startsWith("/api/v1/orders")) {
            isProtected = true;
            // Any authenticated user can access buyer routes
        }

        if (isProtected) {
            HttpSession session = httpRequest.getSession(false);
            User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;

            if (currentUser == null) {
                // Not authenticated
                if (path.startsWith("/api/")) {
                    httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    httpResponse.setContentType("application/json");
                    httpResponse.getWriter().write("{\"success\":false,\"error\":\"Authentication required\"}");
                } else {
                    String query = httpRequest.getQueryString();
                    String redirectUrl = path + (query != null ? "?" + query : "");
                    httpResponse.sendRedirect(httpRequest.getContextPath() + "/auth/login?redirect=" +
                        java.net.URLEncoder.encode(redirectUrl, "UTF-8"));
                }
                return;
            }

            // Role authorization check
            if (requiredRole != null && currentUser.getRole() != requiredRole && currentUser.getRole() != Role.ADMIN) {
                // Insufficient role
                if (path.startsWith("/api/")) {
                    httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    httpResponse.setContentType("application/json");
                    httpResponse.getWriter().write("{\"success\":false,\"error\":\"Access denied: insufficient permissions\"}");
                } else {
                    httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied: insufficient permissions");
                }
                return;
            }
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {}
}
