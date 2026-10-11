package com.reshmamart.service.chat;

/**
 * Strategy interface for AI Chatbot providers.
 * Allows switching between Mock and real LLM providers (e.g. Gemini)
 * without altering business logic or controller layers.
 */
public interface ChatProvider {

    /**
     * Processes a user question and returns an AI or rule-based response.
     *
     * @param userMessage Sanitized user query
     * @param sessionId   Unique session identifier for contextual tracing
     * @return Formatted domain response
     * @throws Exception If an unrecoverable provider error occurs
     */
    String chat(String userMessage, String sessionId) throws Exception;

    /**
     * Returns the human-readable identifier of the provider.
     *
     * @return Provider name (e.g., "MockProvider", "GeminiProvider")
     */
    String getProviderName();
}
