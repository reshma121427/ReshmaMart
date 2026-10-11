package com.reshmamart.controller;

import com.reshmamart.dto.LoginRequestDTO;
import com.reshmamart.dto.RegisterRequestDTO;
import com.reshmamart.exception.AuthenticationException;
import com.reshmamart.exception.ValidationException;
import com.reshmamart.model.Role;
import com.reshmamart.model.User;
import com.reshmamart.service.UserService;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "AuthServlet", urlPatterns = {"/auth/*", "/login", "/register", "/logout"})
public class AuthServlet extends HttpServlet {

    private UserService userService;

    @Override
    public void init() throws ServletException {
        userService = (UserService) getServletContext().getAttribute("userService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        String pathInfo = req.getPathInfo();

        if ("/logout".equals(path) || (pathInfo != null && pathInfo.equals("/logout"))) {
            handleLogout(req, resp);
            return;
        }

        if ("/register".equals(path) || (pathInfo != null && pathInfo.equals("/register"))) {
            req.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(req, resp);
            return;
        }

        // Default to login page
        req.setAttribute("redirect", req.getParameter("redirect"));
        req.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        String pathInfo = req.getPathInfo();

        if ("/register".equals(path) || (pathInfo != null && pathInfo.equals("/register"))) {
            handleRegister(req, resp);
        } else if ("/logout".equals(path) || (pathInfo != null && pathInfo.equals("/logout"))) {
            handleLogout(req, resp);
        } else {
            handleLogin(req, resp);
        }
    }

    private void handleLogin(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        String redirect = req.getParameter("redirect");

        LoginRequestDTO loginReq = new LoginRequestDTO(email, password);

        try {
            User user = userService.login(loginReq);

            // Session Management: Regenerate session ID on login to prevent session fixation attacks
            HttpSession oldSession = req.getSession(false);
            if (oldSession != null) {
                oldSession.invalidate();
            }
            HttpSession newSession = req.getSession(true);
            newSession.setMaxInactiveInterval(30 * 60); // 30 minutes explicit timeout
            newSession.setAttribute("currentUser", user);

            // Redirect based on role or original redirect URL
            if (redirect != null && !redirect.trim().isEmpty() && !redirect.contains("://")) {
                resp.sendRedirect(redirect);
            } else if (user.getRole() == Role.ADMIN) {
                resp.sendRedirect(req.getContextPath() + "/admin/dashboard");
            } else if (user.getRole() == Role.SELLER) {
                resp.sendRedirect(req.getContextPath() + "/seller/dashboard");
            } else {
                resp.sendRedirect(req.getContextPath() + "/products");
            }

        } catch (ValidationException | AuthenticationException e) {
            req.setAttribute("errorMessage", e.getMessage());
            req.setAttribute("email", email);
            req.setAttribute("redirect", redirect);
            req.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(req, resp);
        }
    }

    private void handleRegister(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String name = req.getParameter("name");
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        String confirmPassword = req.getParameter("confirmPassword");
        String roleStr = req.getParameter("role");

        Role role = Role.fromString(roleStr);
        if (role == null) {
            role = Role.BUYER;
        }

        RegisterRequestDTO regReq = new RegisterRequestDTO(name, email, password, confirmPassword, role);

        try {
            userService.register(regReq);
            req.setAttribute("successMessage", "Registration successful! Please log in with your credentials.");
            req.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(req, resp);
        } catch (ValidationException e) {
            req.setAttribute("errorMessage", e.getMessage());
            req.setAttribute("fieldErrors", e.getFieldErrors());
            req.setAttribute("name", name);
            req.setAttribute("email", email);
            req.setAttribute("role", roleStr);
            req.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(req, resp);
        }
    }

    private void handleLogout(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        resp.sendRedirect(req.getContextPath() + "/auth/login?logout=true");
    }
}
