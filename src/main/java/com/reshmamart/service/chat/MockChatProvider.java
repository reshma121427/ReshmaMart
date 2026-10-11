package com.reshmamart.service.chat;

import java.util.Locale;

/**
 * High-reliability Mock Chat Provider with intelligent domain keyword matching.
 * Provides canned, accurate FAQ answers grounded in the reshmamart catalog,
 * INR pricing, and e-commerce policies with zero network dependencies.
 */
public class MockChatProvider implements ChatProvider {

    @Override
    public String chat(String userMessage, String sessionId) {
        if (userMessage == null || userMessage.trim().isEmpty()) {
            return "Hello! I am your ReshmaMart AI shopping assistant. How can I assist you with our catalog, orders, or policies today?";
        }

        String msg = userMessage.trim().toLowerCase(Locale.ROOT);

        // 1. Greetings & Bot Identity
        if (msg.matches(".*\\b(hello|hi|hey|greetings|who are you|namaste)\\b.*")) {
            return "Hello! I am your **ReshmaMart AI Shopping Assistant**. I can help you find products, check prices in Rupees (₹), track orders, or answer questions about our delivery and return policies. What can I do for you today?";
        }

        // 2. Product Categories & Catalog
        if (msg.matches(".*\\b(category|categories|catalog|browse|what do you sell|products)\\b.*")) {
            return "ReshmaMart offers 28+ verified products across 5 main categories:\n"
                    + "• **Electronics**: Mechanical Keyboards, 4K Monitors, Noise-Cancelling Headphones, Wireless Mice, Bluetooth Speakers.\n"
                    + "• **Fashion**: Oxford Shirts, Chino Trousers, Leather Wallets, Polarized Sunglasses, Chronograph Watches.\n"
                    + "• **Home & Kitchen**: Cast Iron Skillets, French Presses, Bamboo Cutting Boards, Ceramic Dinnerware, Electric Kettles.\n"
                    + "• **Books**: Clean Architecture, Designing Data-Intensive Applications, Effective Java, Pragmatic Programmer.\n"
                    + "• **Sports & Fitness**: Yoga Mats, Adjustable Dumbbells, Protein Shakers, Resistance Bands.\n\n"
                    + "You can filter by category or search with keywords directly on our **[Browse Page](/products)**!";
        }

        // 3. Pricing, Discounts & Currency
        if (msg.matches(".*\\b(price|pricing|rupee|rupees|inr|cost|expensive|cheap|discount|offer)\\b.*")) {
            return "All products on ReshmaMart are priced in **Indian Rupees (₹)** ranging from ₹349 for fitness gear to ₹24,999 for high-end 4K monitors.\n"
                    + "• Free standard shipping is applied on all orders above **₹999**.\n"
                    + "• Transparent pricing with zero hidden convenience fees!";
        }

        // 4. Order Tracking & Status
        if (msg.matches(".*\\b(track|tracking|my order|where is my order|order status|history)\\b.*")) {
            return "You can view and track your orders in real time by navigating to **[My Orders](/orders)** from the top navigation bar. "
                    + "Each order progresses through four transparent stages: **PENDING** → **CONFIRMED** → **SHIPPED** → **DELIVERED**.";
        }

        // 5. Shipping & Delivery Timelines
        if (msg.matches(".*\\b(ship|shipping|delivery|deliver|dispatch|courier|how long|speed)\\b.*")) {
            return "We deliver across all major pin codes in India within **2 to 4 business days**. "
                    + "Orders placed before 2:00 PM IST are processed and handed over to our logistics partner on the same day.";
        }

        // 6. Return, Refund & Cancellation Policy
        if (msg.matches(".*\\b(return|refund|exchange|cancel|damage|broken|policy)\\b.*")) {
            return "ReshmaMart offers a **7-Day Hassle-Free Return Policy** for items in original condition with packaging intact. "
                    + "Once a returned item is received, refunds are processed back to your original payment method or UPI within **3 to 5 business days**.";
        }

        // 7. Payment Options & Security
        if (msg.matches(".*\\b(payment|pay|card|upi|qr|cod|cash on delivery|gpay|phonepe)\\b.*")) {
            return "We support multiple secure checkout payment options:\n"
                    + "1. **Credit / Debit Cards** (Visa, MasterCard, RuPay)\n"
                    + "2. **UPI / QR Code Instant Approval** (Google Pay, PhonePe, Paytm)\n"
                    + "3. **Cash on Delivery (COD)** on eligible pin codes.\n"
                    + "All payment transactions run in protected mock simulation mode for safe academic testing.";
        }

        // 8. Seller Registration & Product Listing
        if (msg.matches(".*\\b(seller|sell|vendor|merchant|add product|list product)\\b.*")) {
            return "Are you a vendor? You can register as a **Seller** on our **[Registration Page](/register)**. "
                    + "Sellers gain access to the dedicated **Seller Portal** (`/seller/dashboard`) to create new product listings, manage stock, and update fulfillment tracking for incoming orders!";
        }

        // 9. Ratings & Reviews
        if (msg.matches(".*\\b(review|rating|feedback|stars|comment)\\b.*")) {
            return "Buyers who have purchased and received items can submit **1-to-5 star ratings and customer reviews** on the product detail page once the order is delivered! This helps fellow shoppers make informed choices.";
        }

        // 10. Technical Architecture & Capstone Info
        if (msg.matches(".*\\b(tech|technology|stack|architecture|anna university|java|tomcat|h2)\\b.*")) {
            return "ReshmaMart is developed as an enterprise-grade Capstone for **Anna University R2025 Semester 3**.\n"
                    + "• **Backend**: Java 17, Apache Tomcat 9.0.x (`javax.servlet.*`), HikariCP connection pool.\n"
                    + "• **Database**: H2 Database in persistent file mode with 100% PreparedStatement security.\n"
                    + "• **Security**: BCrypt salt hashing, session fixation defense, and Role-Based Access Control (RBAC).\n"
                    + "• **AI Subsystem**: Modular Strategy Pattern ChatProvider with sliding-window rate limiting.";
        }

        // 11. Customer Support / Contact
        if (msg.matches(".*\\b(support|contact|help|email|phone|customer care)\\b.*")) {
            return "You can reach our student capstone support team at **support@reshmamart.com** or browse our help guides on the platform. We are here to help!";
        }

        // Default Intelligent Fallback
        return "I can help with questions about **ReshmaMart products**, **pricing in ₹**, **shipping timelines**, **order tracking**, or **return policies**.\n"
                + "Try asking: *'What categories are available?'*, *'How do I track my order?'*, or *'What is your return policy?'*.";
    }

    @Override
    public String getProviderName() {
        return "MockChatProvider";
    }
}
