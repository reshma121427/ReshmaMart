# D4: reshmamart AI Chatbot Request Flow Sequence Diagram

## Chatbot Subsystem Architecture
The **reshmamart AI Chatbot** (Phase 3 / Section 11) is engineered as an asynchronous, defensive subsystem that decouples client UI interactions from external LLM availability:
1. **Sliding-Window Rate Limiter**: Strictly prevents abuse (maximum 10 messages per minute per user session).
2. **In-Memory LRU Cache**: Eliminates redundant compute and latency on repeated identical domain inquiries.
3. **Strategy Pattern Provider**: Interchanges dynamically between local `MockChatProvider` and `GeminiChatProvider`.
4. **Graceful Degradation**: Protects user experience with static fallback answers if API rate limits or network partitions occur.

---

## Mermaid Sequence Diagram

```mermaid
sequenceDiagram
    autonumber
    actor User as User (Browser Widget)
    participant Servlet as ChatServlet (/api/chat)
    participant Service as ChatServiceImpl
    participant RateLimiter as SlidingWindowLimiter
    participant Cache as SessionCache (Map)
    participant Provider as ChatProvider (Strategy)
    participant Gemini as Google Gemini API (Cloud)

    User->>Servlet: POST /api/chat { "message": "What is the return policy?" }
    Servlet->>Servlet: Extract sessionId & message from JSON body
    
    alt Empty or Null Message
        Servlet-->>User: HTTP 400 { "success": false, "error": "Message cannot be empty" }
    end

    Servlet->>Service: processMessage(message, sessionId)
    
    Service->>Service: Check length <= 300 characters
    alt Length > 300 chars
        Service-->>Servlet: "Your question is too long. Please keep under 300 chars."
        Servlet-->>User: HTTP 200 { "success": true, "data": { "reply": "..." } }
    end

    Service->>RateLimiter: isRateLimited(sessionId)
    RateLimiter->>RateLimiter: Clean expired timestamps (> 60s)
    
    alt Request count > 10 in 60s
        RateLimiter-->>Service: Rate limit exceeded (true)
        Service-->>Servlet: "Please wait a moment before sending another message (limit: 10 msg/min)."
        Servlet-->>User: HTTP 200 { "success": true, "data": { "reply": "Rate limit notice..." } }
    else Within limit (<= 10)
        RateLimiter->>RateLimiter: Record current timestamp
        RateLimiter-->>Service: Allowed (false)
        
        Service->>Cache: get(sessionId + "::" + query)
        alt Cache Hit
            Cache-->>Service: Return cached reply
            Service-->>Servlet: Cached Answer
            Servlet-->>User: HTTP 200 { "success": true, "data": { "reply": "...", "provider": "Cached" } }
        else Cache Miss
            Service->>Provider: chat(sanitizedQuery, sessionId)
            
            alt Active Provider = GeminiChatProvider
                Provider->>Gemini: POST /v1beta/models/gemini-1.5-flash (with System Prompt)
                alt Gemini Call Success (HTTP 200 within 5s)
                    Gemini-->>Provider: Generated Response JSON
                    Provider-->>Service: Parsed Text
                else Gemini Call Fails or Times out
                    Provider->>Provider: Fallback to MockChatProvider
                    Provider-->>Service: High-fidelity FAQ Answer
                end
            else Active Provider = MockChatProvider
                Provider->>Provider: Keyword regex matching on 11 domain topics
                Provider-->>Service: Grounded Domain Answer
            end
            
            Service->>Cache: put(sessionId + "::" + query, reply)
            Service-->>Servlet: Generated Reply
            Servlet-->>User: HTTP 200 { "success": true, "data": { "reply": reply, "provider": providerName } }
        end
    end
```
