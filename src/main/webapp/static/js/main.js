// reshmamart Client JavaScript Helper
document.addEventListener('DOMContentLoaded', () => {
    // Auto-dismiss alerts after 5 seconds
    const alerts = document.querySelectorAll('.alert');
    alerts.forEach(alert => {
        setTimeout(() => {
            alert.style.transition = 'opacity 0.5s ease';
            alert.style.opacity = '0';
            setTimeout(() => alert.remove(), 500);
        }, 5000);
    });
});

function confirmAction(message) {
    return confirm(message || 'Are you sure you want to proceed?');
}

// ===================================================================
// reshmamart AI Chatbot Controller (Section 11 / Phase 3)
// ===================================================================
document.addEventListener('DOMContentLoaded', () => {
    const launcherBtn = document.getElementById('chat-launcher-btn');
    const modal = document.getElementById('chat-modal');
    const closeBtn = document.getElementById('chat-close-btn');
    const clearBtn = document.getElementById('chat-clear-btn');
    const form = document.getElementById('chat-form');
    const input = document.getElementById('chat-input');
    const messagesBox = document.getElementById('chat-messages');
    const typingIndicator = document.getElementById('chat-typing-indicator');
    const chipsContainer = document.getElementById('chat-chips');

    if (!launcherBtn || !modal) return;

    // Toggle Modal
    launcherBtn.addEventListener('click', () => {
        const isHidden = modal.style.display === 'none' || !modal.style.display;
        modal.style.display = isHidden ? 'flex' : 'none';
        if (isHidden) {
            input.focus();
            scrollToBottom();
        }
    });

    closeBtn.addEventListener('click', () => {
        modal.style.display = 'none';
    });

    // Clear history
    clearBtn.addEventListener('click', () => {
        if (confirmAction('Clear current chat conversation?')) {
            messagesBox.innerHTML = `
                <div class="chat-message chat-message-bot">
                    <div class="chat-bubble">
                        👋 Conversation cleared. What else can I help you find today?
                    </div>
                    <span class="chat-timestamp">Just now</span>
                </div>
            `;
        }
    });

    // Quick Chips Click
    if (chipsContainer) {
        chipsContainer.addEventListener('click', (e) => {
            const chip = e.target.closest('.chat-chip');
            if (chip && chip.dataset.query) {
                sendMessage(chip.dataset.query);
            }
        });
    }

    // Form Submit
    form.addEventListener('submit', (e) => {
        e.preventDefault();
        const text = input.value.trim();
        if (text) {
            sendMessage(text);
            input.value = '';
        }
    });

    function sendMessage(text) {
        // Append user bubble
        appendMessage('user', text);
        scrollToBottom();

        // Show typing indicator
        typingIndicator.style.display = 'flex';
        scrollToBottom();

        // Send POST to /api/chat
        fetch('/api/chat', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            },
            body: JSON.stringify({ message: text })
        })
        .then(response => response.json())
        .then(res => {
            typingIndicator.style.display = 'none';
            if (res.success && res.data && res.data.reply) {
                appendMessage('bot', res.data.reply);
            } else if (res.error) {
                appendMessage('bot', '⚠️ ' + res.error);
            } else {
                appendMessage('bot', 'Sorry, I could not process that request. Please try again.');
            }
            scrollToBottom();
        })
        .catch(err => {
            typingIndicator.style.display = 'none';
            appendMessage('bot', 'Network error reaching AI Assistant. Please check your connection and try again.');
            scrollToBottom();
        });
    }

    function appendMessage(sender, text) {
        const msgDiv = document.createElement('div');
        msgDiv.className = `chat-message chat-message-${sender}`;

        const bubble = document.createElement('div');
        bubble.className = 'chat-bubble';

        if (sender === 'bot') {
            bubble.innerHTML = formatBotReply(text);
        } else {
            bubble.textContent = text;
        }

        const timestamp = document.createElement('span');
        timestamp.className = 'chat-timestamp';
        const now = new Date();
        timestamp.textContent = now.getHours().toString().padStart(2, '0') + ':' + now.getMinutes().toString().padStart(2, '0');

        msgDiv.appendChild(bubble);
        msgDiv.appendChild(timestamp);
        messagesBox.appendChild(msgDiv);
    }

    function formatBotReply(raw) {
        if (!raw) return '';
        // Escape HTML
        let escaped = raw
            .replace(/&/g, "&amp;")
            .replace(/</g, "&lt;")
            .replace(/>/g, "&gt;");

        // Format Markdown bold: **text**
        escaped = escaped.replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>');

        // Format Markdown links: [text](url)
        escaped = escaped.replace(/\[(.*?)\]\((.*?)\)/g, '<a href="$2" style="color: #2563eb; text-decoration: underline;">$1</a>');

        // Format Bullet points: • or *
        escaped = escaped.replace(/^• (.*$)/gim, '&bull; $1');

        // Newlines to <br>
        escaped = escaped.replace(/\n/g, '<br>');

        return escaped;
    }

    function scrollToBottom() {
        setTimeout(() => {
            messagesBox.scrollTop = messagesBox.scrollHeight;
        }, 50);
    }
});

