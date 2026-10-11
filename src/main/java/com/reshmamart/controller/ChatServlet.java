package com.reshmamart.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.reshmamart.dto.ApiResponse;
import com.reshmamart.service.chat.ChatService;
import com.reshmamart.service.chat.ChatServiceImpl;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Controller endpoint handling chatbot interactions via JSON API.
 * Mapped to /api/chat and /api/v1/chat.
 */
@WebServlet(name = "ChatServlet", urlPatterns = {"/api/chat", "/api/v1/chat"})
public class ChatServlet extends HttpServlet {

    private final ChatService chatService;
    private final Gson gson;

    public ChatServlet() {
        this.chatService = new ChatServiceImpl();
        this.gson = new Gson();
    }

    public ChatServlet(ChatService chatService) {
        this.chatService = chatService;
        this.gson = new Gson();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        Map<String, Object> status = new HashMap<>();
        status.put("service", "reshmamart AI Chatbot");
        status.put("status", "UP");
        status.put("provider", chatService.getActiveProviderName());
        ApiResponse<Map<String, Object>> response = ApiResponse.success(status);

        try (PrintWriter out = resp.getWriter()) {
            out.print(gson.toJson(response));
            out.flush();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");

        HttpSession session = req.getSession(true);
        String sessionId = session.getId();

        String userMessage = extractMessage(req);

        if (userMessage == null || userMessage.trim().isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            ApiResponse<Void> errorResp = ApiResponse.error("Message cannot be empty");
            try (PrintWriter out = resp.getWriter()) {
                out.print(gson.toJson(errorResp));
                out.flush();
            }
            return;
        }

        try {
            String botReply = chatService.processMessage(userMessage, sessionId);

            Map<String, Object> data = new HashMap<>();
            data.put("reply", botReply);
            data.put("provider", chatService.getActiveProviderName());

            ApiResponse<Map<String, Object>> successResp = ApiResponse.success(data);
            try (PrintWriter out = resp.getWriter()) {
                out.print(gson.toJson(successResp));
                out.flush();
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            ApiResponse<Void> err = ApiResponse.error("Unable to process message at this time.");
            try (PrintWriter out = resp.getWriter()) {
                out.print(gson.toJson(err));
                out.flush();
            }
        }
    }

    private String extractMessage(HttpServletRequest req) {
        // Try reading JSON body first
        String contentType = req.getContentType();
        if (contentType != null && contentType.toLowerCase().contains("application/json")) {
            try (BufferedReader reader = req.getReader()) {
                JsonObject json = gson.fromJson(reader, JsonObject.class);
                if (json != null && json.has("message")) {
                    return json.get("message").getAsString();
                }
            } catch (Exception ignored) {
                // fallback to request parameter
            }
        }

        // Fallback to form parameter
        return req.getParameter("message");
    }
}
