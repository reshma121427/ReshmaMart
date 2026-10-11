package com.reshmamart.service.chat;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Real LLM Chat Provider communicating with Google Gemini API.
 * Uses a strict server-side system instruction prompt grounded in
 * the reshmamart catalog, currency, and e-commerce workflows.
 */
public class GeminiChatProvider implements ChatProvider {

    private static final Logger LOGGER = Logger.getLogger(GeminiChatProvider.class.getName());
    private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent";
    private static final int TIMEOUT_MS = 5000;

    private static final String SYSTEM_PROMPT =
            "You are the friendly, helpful AI Shopping Assistant for 'reshmamart', a modern Indian e-commerce platform built for Anna University R2025. "
            + "Key platform context:\n"
            + "- Currency: All prices are strictly in Indian Rupees (₹ / INR), ranging from ₹349 to ₹24,999.\n"
            + "- 5 Product Categories: Electronics, Fashion, Home & Kitchen, Books, Sports & Fitness (28 verified products).\n"
            + "- Delivery: 2-4 business days across India; free shipping on orders above ₹999.\n"
            + "- Returns: 7-day hassle-free return window; refunds processed in 3-5 business days.\n"
            + "- Payments: Credit/Debit Card, UPI / QR (Instant approval), Cash on Delivery.\n"
            + "- User Roles: Buyers (browse, cart, order, review), Sellers (manage inventory, fulfill orders at /seller/dashboard), Admin (/admin/dashboard).\n"
            + "Rules:\n"
            + "1. Keep responses concise, clear, and professional (under 120 words).\n"
            + "2. Strictly refuse queries unrelated to reshmamart products, orders, shopping, policies, or the platform.\n"
            + "3. Always format prices in Rupees with the ₹ symbol.";

    private final String apiKey;
    private final MockChatProvider fallbackProvider;
    private final Gson gson;

    public GeminiChatProvider(String apiKey) {
        this.apiKey = (apiKey != null) ? apiKey.trim() : "";
        this.fallbackProvider = new MockChatProvider();
        this.gson = new Gson();
    }

    @Override
    public String chat(String userMessage, String sessionId) {
        if (apiKey.isEmpty()) {
            LOGGER.info("No Gemini API key supplied; using high-fidelity MockChatProvider fallback.");
            return fallbackProvider.chat(userMessage, sessionId);
        }

        try {
            String endpoint = GEMINI_API_URL + "?key=" + apiKey;
            URL url = new URL(endpoint);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setConnectTimeout(TIMEOUT_MS);
            conn.setReadTimeout(TIMEOUT_MS);
            conn.setDoOutput(true);

            // Construct Gemini Request JSON
            JsonObject root = new JsonObject();

            // System Instruction
            JsonObject systemInstruction = new JsonObject();
            JsonArray systemParts = new JsonArray();
            JsonObject systemPart = new JsonObject();
            systemPart.addProperty("text", SYSTEM_PROMPT);
            systemParts.add(systemPart);
            systemInstruction.add("parts", systemParts);
            root.add("systemInstruction", systemInstruction);

            // User Content
            JsonArray contents = new JsonArray();
            JsonObject content = new JsonObject();
            content.addProperty("role", "user");
            JsonArray parts = new JsonArray();
            JsonObject part = new JsonObject();
            part.addProperty("text", userMessage);
            parts.add(part);
            content.add("parts", parts);
            contents.add(content);
            root.add("contents", contents);

            // Generation Config
            JsonObject genConfig = new JsonObject();
            genConfig.addProperty("temperature", 0.3);
            genConfig.addProperty("maxOutputTokens", 250);
            root.add("generationConfig", genConfig);

            byte[] input = gson.toJson(root).getBytes(StandardCharsets.UTF_8);
            try (OutputStream os = conn.getOutputStream()) {
                os.write(input, 0, input.length);
            }

            int status = conn.getResponseCode();
            if (status == 200) {
                try (InputStream is = conn.getInputStream();
                     BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        response.append(line);
                    }
                    return extractGeminiResponse(response.toString());
                }
            } else {
                LOGGER.warning("Gemini API call failed with HTTP status: " + status + ". Falling back to mock.");
                return fallbackProvider.chat(userMessage, sessionId);
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Exception invoking Gemini API: " + e.getMessage() + ". Using fallback.");
            return fallbackProvider.chat(userMessage, sessionId);
        }
    }

    private String extractGeminiResponse(String jsonString) {
        try {
            JsonObject json = gson.fromJson(jsonString, JsonObject.class);
            JsonArray candidates = json.getAsJsonArray("candidates");
            if (candidates != null && candidates.size() > 0) {
                JsonObject firstCandidate = candidates.get(0).getAsJsonObject();
                JsonObject content = firstCandidate.getAsJsonObject("content");
                if (content != null) {
                    JsonArray parts = content.getAsJsonArray("parts");
                    if (parts != null && parts.size() > 0) {
                        String text = parts.get(0).getAsJsonObject().get("text").getAsString();
                        if (text != null && !text.trim().isEmpty()) {
                            return text.trim();
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error parsing Gemini response JSON: " + e.getMessage());
        }
        return fallbackProvider.chat("catalog", "default");
    }

    @Override
    public String getProviderName() {
        return "GeminiChatProvider";
    }
}
