package com.reshmamart.service.chat;

/**
 * Service interface managing chatbot business logic, rate limiting,
 * in-memory caching, input validation, and provider orchestration.
 */
public interface ChatService {

    /**
     * Answers a user inquiry within the reshmamart domain.
     *
     * @param message   Raw user message input
     * @param sessionId Web session ID for rate limiting and caching
     * @return Formatted AI reply
     */
    String processMessage(String message, String sessionId);

    /**
     * Returns the name of the active chat provider.
     *
     * @return Name of provider
     */
    String getActiveProviderName();

    /**
     * Clears cached questions for a given session.
     *
     * @param sessionId Session ID to clear
     */
    void clearSessionCache(String sessionId);
}
