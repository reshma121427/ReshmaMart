<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!-- ===================================================================
     reshmamart AI Chatbot Floating Widget (Section 11 / Phase 3)
     Self-contained Production Build — Deep Plum Theme
     =================================================================== -->
<style>
/* Scoped Self-Contained AI Chatbot Styles — Deep Plum Theme */
#reshma-chat-widget {
    position: fixed !important;
    bottom: 24px !important;
    right: 24px !important;
    z-index: 999999 !important;
    font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif !important;
}

#chat-launcher-btn {
    display: inline-flex !important;
    align-items: center !important;
    gap: 8px !important;
    padding: 10px 18px 10px 14px !important;
    height: 54px !important;
    border-radius: 28px !important;
    background: linear-gradient(135deg, #51204F, #351334) !important;
    color: #ffffff !important;
    border: 2px solid #ffffff !important;
    box-shadow: 0 10px 25px -5px rgba(81, 32, 79, 0.5), 0 8px 12px -6px rgba(81, 32, 79, 0.3) !important;
    cursor: pointer !important;
    position: relative !important;
    transition: transform 0.2s ease, box-shadow 0.2s ease, background 0.2s ease !important;
    text-decoration: none !important;
}

#chat-launcher-btn:hover {
    transform: scale(1.05) !important;
    background: linear-gradient(135deg, #6D3268, #51204F) !important;
    box-shadow: 0 14px 28px -4px rgba(109, 50, 104, 0.6) !important;
}

.chat-launcher-icon {
    font-size: 1.5rem !important;
    line-height: 1 !important;
}

.chat-launcher-label {
    font-size: 0.92rem !important;
    font-weight: 700 !important;
    letter-spacing: 0.02em !important;
    color: #ffffff !important;
    white-space: nowrap !important;
}

.chat-launcher-badge {
    background: #10b981 !important;
    color: #ffffff !important;
    font-size: 0.68rem !important;
    font-weight: 800 !important;
    padding: 2px 6px !important;
    border-radius: 10px !important;
    margin-left: 2px !important;
}

#chat-modal {
    position: absolute !important;
    bottom: 68px !important;
    right: 0 !important;
    width: 390px !important;
    max-width: calc(100vw - 32px) !important;
    height: 540px !important;
    max-height: calc(100vh - 100px) !important;
    background: #ffffff !important;
    border-radius: 18px !important;
    border: 1px solid #E8DDE8 !important;
    box-shadow: 0 24px 48px rgba(53, 19, 52, 0.2), 0 0 0 1px rgba(81, 32, 79, 0.08) !important;
    flex-direction: column !important;
    overflow: hidden !important;
    animation: reshmaChatFadeIn 0.22s ease-out !important;
}

@keyframes reshmaChatFadeIn {
    from { opacity: 0; transform: translateY(12px) scale(0.96); }
    to { opacity: 1; transform: translateY(0) scale(1); }
}

.chat-modal-header {
    background: linear-gradient(135deg, #351334, #51204F) !important;
    color: #ffffff !important;
    padding: 1rem 1.15rem !important;
    display: flex !important;
    align-items: center !important;
    justify-content: space-between !important;
}

.chat-modal-title {
    display: flex !important;
    align-items: center !important;
    gap: 0.7rem !important;
}

.chat-bot-avatar {
    font-size: 1.6rem !important;
    line-height: 1 !important;
}

.chat-online-status {
    font-size: 0.75rem !important;
    opacity: 0.9 !important;
    display: flex !important;
    align-items: center !important;
    gap: 5px !important;
}

.status-dot {
    width: 8px !important;
    height: 8px !important;
    border-radius: 50% !important;
    background: #34d399 !important;
    display: inline-block !important;
    box-shadow: 0 0 6px #34d399 !important;
}

.chat-modal-actions {
    display: flex !important;
    gap: 0.4rem !important;
}

.chat-icon-btn {
    background: rgba(255, 255, 255, 0.18) !important;
    border: none !important;
    color: #ffffff !important;
    width: 32px !important;
    height: 32px !important;
    border-radius: 50% !important;
    cursor: pointer !important;
    display: flex !important;
    align-items: center !important;
    justify-content: center !important;
    font-size: 0.95rem !important;
    transition: background 0.15s !important;
}

.chat-icon-btn:hover {
    background: rgba(255, 255, 255, 0.35) !important;
}

.chat-chips-container {
    display: flex !important;
    gap: 6px !important;
    overflow-x: auto !important;
    padding: 0.65rem 0.85rem !important;
    background: #FAF2F8 !important;
    border-bottom: 1px solid #E8DDE8 !important;
    scrollbar-width: thin !important;
}

.chat-chip {
    white-space: nowrap !important;
    background: #ffffff !important;
    border: 1px solid #E8DDE8 !important;
    color: #29232B !important;
    font-size: 0.78rem !important;
    font-weight: 600 !important;
    padding: 5px 11px !important;
    border-radius: 20px !important;
    cursor: pointer !important;
    transition: all 0.15s ease !important;
}

.chat-chip:hover {
    background: #F5EBF4 !important;
    border-color: #C8A2C8 !important;
    color: #51204F !important;
}

.chat-messages {
    flex: 1 !important;
    padding: 1rem !important;
    overflow-y: auto !important;
    display: flex !important;
    flex-direction: column !important;
    gap: 0.85rem !important;
    background: #FFF9F5 !important;
}

.chat-message {
    display: flex !important;
    flex-direction: column !important;
    max-width: 86% !important;
}

.chat-message-bot {
    align-self: flex-start !important;
}

.chat-message-user {
    align-self: flex-end !important;
}

.chat-bubble {
    padding: 0.75rem 1rem !important;
    border-radius: 14px !important;
    font-size: 0.88rem !important;
    line-height: 1.45 !important;
    word-break: break-word !important;
}

.chat-message-bot .chat-bubble {
    background: #FAF2F8 !important;
    color: #29232B !important;
    border-bottom-left-radius: 4px !important;
    border: 1px solid #F0E6F0 !important;
    box-shadow: 0 1px 2px rgba(81, 32, 79, 0.04) !important;
}

.chat-message-user .chat-bubble {
    background: linear-gradient(135deg, #51204F, #351334) !important;
    color: #ffffff !important;
    border-bottom-right-radius: 4px !important;
}

.chat-timestamp {
    font-size: 0.7rem !important;
    color: #716875 !important;
    margin-top: 3px !important;
    padding: 0 4px !important;
}

.chat-message-user .chat-timestamp {
    align-self: flex-end !important;
}

.chat-typing-indicator {
    display: flex !important;
    align-items: center !important;
    padding: 0.5rem 1rem !important;
    background: #FAF2F8 !important;
}

.chat-typing-indicator span {
    width: 6px !important;
    height: 6px !important;
    background: #C8A2C8 !important;
    border-radius: 50% !important;
    margin-right: 4px !important;
    animation: typingBounce 1.2s infinite ease-in-out !important;
}

.chat-typing-indicator span:nth-child(2) { animation-delay: 0.2s !important; }
.chat-typing-indicator span:nth-child(3) { animation-delay: 0.4s !important; }

@keyframes typingBounce {
    0%, 80%, 100% { transform: translateY(0); }
    40% { transform: translateY(-6px); }
}

.chat-input-form {
    display: flex !important;
    align-items: center !important;
    padding: 0.75rem !important;
    background: #ffffff !important;
    border-top: 1px solid #E8DDE8 !important;
    gap: 8px !important;
}

.chat-input {
    flex: 1 !important;
    padding: 0.65rem 0.95rem !important;
    border: 1px solid #E8DDE8 !important;
    border-radius: 24px !important;
    font-size: 0.9rem !important;
    outline: none !important;
    background: #FFF9F5 !important;
    color: #29232B !important;
}

.chat-input:focus {
    border-color: #51204F !important;
    background: #ffffff !important;
    box-shadow: 0 0 0 3px rgba(81, 32, 79, 0.15) !important;
}

.chat-send-btn {
    width: 40px !important;
    height: 40px !important;
    border-radius: 50% !important;
    background: #51204F !important;
    color: #ffffff !important;
    border: none !important;
    cursor: pointer !important;
    display: flex !important;
    align-items: center !important;
    justify-content: center !important;
    font-size: 1rem !important;
    transition: background 0.15s, transform 0.15s !important;
}

.chat-send-btn:hover {
    background: #6D3268 !important;
    transform: scale(1.06) !important;
}
</style>

<div id="reshma-chat-widget">
    <!-- Floating Launcher Button -->
    <button id="chat-launcher-btn" type="button" title="Chat with ReshmaMart AI Assistant" aria-label="Open Chat Assistant" onclick="window.toggleReshmaChat()">
        <span class="chat-launcher-icon">💬</span>
        <span class="chat-launcher-label">Ask AI</span>
        <span class="chat-launcher-badge">ONLINE</span>
    </button>

    <!-- Chat Modal Window -->
    <div id="chat-modal" style="display: none;">
        <!-- Header -->
        <div class="chat-modal-header">
            <div class="chat-modal-title">
                <span class="chat-bot-avatar">🤖</span>
                <div>
                    <strong style="font-size: 0.98rem; display: block;">ReshmaMart AI Assistant</strong>
                    <div class="chat-online-status">
                        <span class="status-dot"></span> Online &middot; Instant Help
                    </div>
                </div>
            </div>
            <div class="chat-modal-actions">
                <button type="button" id="chat-clear-btn" class="chat-icon-btn" title="Clear chat history" aria-label="Clear chat">🗑️</button>
                <button type="button" id="chat-close-btn" class="chat-icon-btn" title="Close chat" aria-label="Close chat" onclick="window.closeReshmaChat()">✕</button>
            </div>
        </div>

        <!-- Quick Suggestion Chips -->
        <div class="chat-chips-container" id="chat-chips">
            <button type="button" class="chat-chip" data-query="What categories do you sell?">🛍️ Categories</button>
            <button type="button" class="chat-chip" data-query="How do I track my order?">📦 Track Order</button>
            <button type="button" class="chat-chip" data-query="What is your return policy?">🔄 Return Policy</button>
            <button type="button" class="chat-chip" data-query="What payment methods are supported?">💳 Payments</button>
            <button type="button" class="chat-chip" data-query="How can I register as a seller?">🏪 Sell with us</button>
        </div>

        <!-- Messages Body -->
        <div class="chat-messages" id="chat-messages">
            <div class="chat-message chat-message-bot">
                <div class="chat-bubble">
                    👋 Namaste! I am your <strong>ReshmaMart AI Shopping Assistant</strong>. Ask me anything about our 28+ products, prices in Rupees (₹), delivery timelines, order tracking, or returns!
                </div>
                <span class="chat-timestamp">Just now</span>
            </div>
        </div>

        <!-- Typing Indicator -->
        <div id="chat-typing-indicator" class="chat-typing-indicator" style="display: none;">
            <span></span><span></span><span></span>
            <small style="margin-left: 8px; color: #716875;">ReshmaMart AI is typing...</small>
        </div>

        <!-- Input Footer -->
        <form id="chat-form" class="chat-input-form" onsubmit="return false;">
            <input type="text" id="chat-input" class="chat-input" placeholder="Type your question here (max 300 chars)..." maxlength="300" autocomplete="off" required>
            <button type="submit" id="chat-send-btn" class="chat-send-btn" title="Send message" aria-label="Send message">
                ➤
            </button>
        </form>
    </div>
</div>

<script>
(function() {
    var chatApiBase = "${pageContext.request.contextPath}";
    var chatEndpoint = (chatApiBase ? chatApiBase : "") + "/api/chat";

    window.openReshmaChat = function() {
        var modal = document.getElementById('chat-modal');
        if (modal) {
            modal.style.display = 'flex';
            var input = document.getElementById('chat-input');
            if (input) input.focus();
            scrollToBottom();
        }
    };

    window.closeReshmaChat = function() {
        var modal = document.getElementById('chat-modal');
        if (modal) {
            modal.style.display = 'none';
        }
    };

    window.toggleReshmaChat = function() {
        var modal = document.getElementById('chat-modal');
        if (!modal) return;
        if (modal.style.display === 'none' || !modal.style.display) {
            window.openReshmaChat();
        } else {
            window.closeReshmaChat();
        }
    };

    function initChatWidget() {
        var clearBtn = document.getElementById('chat-clear-btn');
        var form = document.getElementById('chat-form');
        var input = document.getElementById('chat-input');
        var messagesBox = document.getElementById('chat-messages');
        var typingIndicator = document.getElementById('chat-typing-indicator');
        var chipsContainer = document.getElementById('chat-chips');

        if (!form || !input) return;

        // Clear chat
        if (clearBtn) {
            clearBtn.onclick = function() {
                if (confirm('Clear current chat conversation?')) {
                    if (messagesBox) {
                        messagesBox.innerHTML = '<div class="chat-message chat-message-bot">' +
                            '<div class="chat-bubble">👋 Conversation cleared. What else can I help you find today?</div>' +
                            '<span class="chat-timestamp">Just now</span></div>';
                    }
                }
            };
        }

        // Quick Chips
        if (chipsContainer) {
            chipsContainer.onclick = function(e) {
                var chip = e.target.closest('.chat-chip');
                if (chip && chip.dataset.query) {
                    sendChatMessage(chip.dataset.query);
                }
            };
        }

        // Submit message
        form.onsubmit = function(e) {
            e.preventDefault();
            var text = input.value.trim();
            if (text) {
                sendChatMessage(text);
                input.value = '';
            }
            return false;
        };

        function sendChatMessage(text) {
            appendMessage('user', text);
            if (typingIndicator) typingIndicator.style.display = 'flex';
            scrollToBottom();

            fetch(chatEndpoint, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Accept': 'application/json'
                },
                body: JSON.stringify({ message: text })
            })
            .then(function(res) { return res.json(); })
            .then(function(data) {
                if (typingIndicator) typingIndicator.style.display = 'none';
                if (data && data.success && data.data && data.data.reply) {
                    appendMessage('bot', data.data.reply);
                } else if (data && data.error) {
                    appendMessage('bot', '⚠️ ' + data.error);
                } else {
                    appendMessage('bot', 'Sorry, I could not process that request. Please try again.');
                }
                scrollToBottom();
            })
            .catch(function(err) {
                if (typingIndicator) typingIndicator.style.display = 'none';
                appendMessage('bot', 'Network error reaching AI Assistant. Please check your connection and try again.');
                scrollToBottom();
            });
        }

        function appendMessage(sender, text) {
            if (!messagesBox) return;
            var msgDiv = document.createElement('div');
            msgDiv.className = 'chat-message chat-message-' + sender;

            var bubble = document.createElement('div');
            bubble.className = 'chat-bubble';

            if (sender === 'bot') {
                bubble.innerHTML = formatMarkdown(text);
            } else {
                bubble.textContent = text;
            }

            var timestamp = document.createElement('span');
            timestamp.className = 'chat-timestamp';
            var now = new Date();
            timestamp.textContent = now.getHours().toString().padStart(2, '0') + ':' + now.getMinutes().toString().padStart(2, '0');

            msgDiv.appendChild(bubble);
            msgDiv.appendChild(timestamp);
            messagesBox.appendChild(msgDiv);
        }

        function formatMarkdown(raw) {
            if (!raw) return '';
            var escaped = raw
                .replace(/&/g, "&amp;")
                .replace(/</g, "&lt;")
                .replace(/>/g, "&gt;")
                .replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>')
                .replace(/\[(.*?)\]\((.*?)\)/g, '<a href="$2" style="color: #51204F; text-decoration: underline;">$1</a>')
                .replace(/^• (.*$)/gim, '&bull; $1')
                .replace(/\n/g, '<br>');
            return escaped;
        }
    }

    function scrollToBottom() {
        var box = document.getElementById('chat-messages');
        if (box) {
            setTimeout(function() { box.scrollTop = box.scrollHeight; }, 60);
        }
    }

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', initChatWidget);
    } else {
        initChatWidget();
    }
})();
</script>
