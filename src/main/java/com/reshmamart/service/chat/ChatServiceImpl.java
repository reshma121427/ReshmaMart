package com.reshmamart.service.chat;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementation of ChatService providing sliding-window rate limiting,
 * in-memory question caching per session, length validation, and provider fallback.
 */
public class ChatServiceImpl implements ChatService {

    private static final Logger LOGGER = Logger.getLogger(ChatServiceImpl.class.getName());
    private static final int MAX_INPUT_LENGTH = 300;
    private static final int RATE_LIMIT_MAX_REQUESTS = 10;
    private static final long RATE_LIMIT_WINDOW_MS = 60_000L; // 1 minute

    private final ChatProvider provider;
    // Cache: key = sessionId + "::" + normalizedQuery -> cached answer
    private final Map<String, String> sessionQuestionCache = new ConcurrentHashMap<>();
    // Rate limiter: key = sessionId -> list of request timestamps
    private final Map<String, List<Long>> sessionRateLimits = new ConcurrentHashMap<>();

    public ChatServiceImpl() {
        this(resolveConfiguredProvider());
    }

    public ChatServiceImpl(ChatProvider provider) {
        this.provider = (provider != null) ? provider : new MockChatProvider();
        LOGGER.info("ChatService initialized with provider: " + this.provider.getProviderName());
    }

    private static ChatProvider resolveConfiguredProvider() {
        String configuredProvider = "mock";
        String geminiKey = System.getenv("GEMINI_API_KEY");

        try (InputStream in = ChatServiceImpl.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (in != null) {
                Properties props = new Properties();
                props.load(in);
                String propProvider = props.getProperty("ai.chatbot.provider");
                if (propProvider != null && !propProvider.trim().isEmpty()) {
                    configuredProvider = propProvider.trim();
                }
                String propKey = props.getProperty("ai.chatbot.gemini.key");
                if (propKey != null && !propKey.trim().isEmpty() && (geminiKey == null || geminiKey.trim().isEmpty())) {
                    geminiKey = propKey.trim();
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error reading chatbot config: " + e.getMessage());
        }

        if ("gemini".equalsIgnoreCase(configuredProvider) || (geminiKey != null && !geminiKey.trim().isEmpty())) {
            return new GeminiChatProvider(geminiKey);
        }
        return new MockChatProvider();
    }

    @Override
    public String processMessage(String message, String sessionId) {
        String sessionKey = (sessionId != null && !sessionId.trim().isEmpty()) ? sessionId.trim() : "anonymous";

        // 1. Input Validation
        if (message == null || message.trim().isEmpty()) {
            return "Please type a question about reshmamart products, orders, or policies.";
        }

        String trimmed = message.trim();
        if (trimmed.length() > MAX_INPUT_LENGTH) {
            return "Your question is too long. Please keep your question under " + MAX_INPUT_LENGTH + " characters.";
        }

        // 2. Per-Session Rate Limiting (10 requests / minute)
        if (isRateLimited(sessionKey)) {
            return "You are asking questions very quickly! For security and service stability, please wait a moment before sending another message (limit: 10 messages per minute).";
        }

        // 3. Question Caching (per session)
        String cacheKey = sessionKey + "::" + trimmed.toLowerCase();
        String cachedAnswer = sessionQuestionCache.get(cacheKey);
        if (cachedAnswer != null) {
            LOGGER.fine("Serving cached chatbot answer for session: " + sessionKey);
            return cachedAnswer;
        }

        // 4. Provider Execution with Graceful Degradation
        try {
            String reply = provider.chat(trimmed, sessionKey);
            if (reply != null && !reply.trim().isEmpty()) {
                sessionQuestionCache.put(cacheKey, reply);
                return reply;
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Chat provider failed: " + e.getMessage(), e);
        }

        // Static degraded fallback response (Section 11)
        String fallback = "I apologize, but our AI assistant is experiencing heavy traffic right now. "
                + "Please feel free to browse our full product catalog at /products or contact us at support@reshmamart.com.";
        return fallback;
    }

    private boolean isRateLimited(String sessionId) {
        long now = System.currentTimeMillis();
        List<Long> timestamps = sessionRateLimits.computeIfAbsent(sessionId, k -> Collections.synchronizedList(new ArrayList<>()));

        synchronized (timestamps) {
            // Evict entries outside the sliding window
            timestamps.removeIf(time -> (now - time) > RATE_LIMIT_WINDOW_MS);

            if (timestamps.size() >= RATE_LIMIT_MAX_REQUESTS) {
                return true;
            }
            timestamps.add(now);
            return false;
        }
    }

    @Override
    public String getActiveProviderName() {
        return provider.getProviderName();
    }

    @Override
    public void clearSessionCache(String sessionId) {
        if (sessionId != null) {
            sessionQuestionCache.keySet().removeIf(key -> key.startsWith(sessionId + "::"));
            sessionRateLimits.remove(sessionId);
        }
    }
}
