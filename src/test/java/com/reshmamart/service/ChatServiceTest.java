package com.reshmamart.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.reshmamart.service.chat.ChatService;
import com.reshmamart.service.chat.ChatServiceImpl;
import com.reshmamart.service.chat.MockChatProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ChatServiceTest {

    private ChatService chatService;

    @BeforeEach
    public void setUp() {
        chatService = new ChatServiceImpl(new MockChatProvider());
    }

    @Test
    public void testMockProviderAnswersCategories() {
        String reply = chatService.processMessage("What categories do you sell?", "test-session-1");
        assertNotNull(reply);
        assertTrue(reply.contains("Electronics") && reply.contains("Fashion"));
    }

    @Test
    public void testMockProviderAnswersPricing() {
        String reply = chatService.processMessage("What is the price in rupees?", "test-session-2");
        assertNotNull(reply);
        assertTrue(reply.contains("₹") || reply.contains("Rupees"));
    }

    @Test
    public void testMockProviderAnswersTracking() {
        String reply = chatService.processMessage("How can I track my order?", "test-session-3");
        assertNotNull(reply);
        assertTrue(reply.contains("My Orders") || reply.contains("/orders"));
    }

    @Test
    public void testEmptyInputHandling() {
        String reply = chatService.processMessage("", "test-session-4");
        assertNotNull(reply);
        assertTrue(reply.contains("Please type a question"));
    }

    @Test
    public void testInputLengthCapExceeded() {
        String longInput = "A".repeat(350);
        String reply = chatService.processMessage(longInput, "test-session-5");
        assertNotNull(reply);
        assertTrue(reply.contains("too long"));
    }

    @Test
    public void testRateLimitingEnforced() {
        String session = "rate-limit-session";
        // Send 10 messages successfully
        for (int i = 0; i < 10; i++) {
            String res = chatService.processMessage("Hello " + i, session);
            assertNotNull(res);
        }
        // 11th message should be rate-limited
        String rateLimitedReply = chatService.processMessage("One more question", session);
        assertTrue(rateLimitedReply.contains("wait a moment") || rateLimitedReply.contains("limit: 10 messages"));
    }

    @Test
    public void testQuestionCachingPerSession() {
        String session = "cache-session";
        String first = chatService.processMessage("What categories are available?", session);
        String second = chatService.processMessage("What categories are available?", session);
        assertEquals(first, second);
    }
}
