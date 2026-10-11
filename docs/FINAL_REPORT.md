# reshmamart: Enterprise-Grade E-Commerce Platform with Modular AI Shopping Assistant

**Anna University R2025 Regulations — Semester 3 Capstone Project Final Report**  
**Student Name:** Rubini | **Package:** `com.reshmamart` | **Evaluation Window:** Jul 27 – Oct 10, 2026  
**Live Production URL:** [https://reshmamart.onrender.com](https://reshmamart.onrender.com)  
**Repository:** [https://github.com/Rubiniponnar/reshmamart](https://github.com/Rubiniponnar/reshmamart)  

---

## 1. Executive Summary & Problem Statement

Modern e-commerce systems require high reliability, bulletproof transactional integrity, robust defense-in-depth security, and intuitive customer support. The goal of this capstone project is to engineer an enterprise-grade, full-stack multi-role e-commerce web platform—**reshmamart**—strictly adhering to modern Java EE and software engineering standards.

### Core Problems Addressed:
1. **Multi-Role E-Commerce Isolation**: Providing discrete, role-enforced interfaces for **Buyers** (browsing, cart management, mock payment, tracking, reviewing), **Sellers** (inventory management, fulfillment tracking), and **Administrators** (user oversight, listing moderation, GMV tracking).
2. **ACID Transaction Integrity**: Preventing race conditions, inventory overselling, and orphaned records during checkout operations.
3. **Defense-in-Depth Security**: Eliminating SQL Injection (100% `PreparedStatement`), securing credentials via BCrypt (12 work factor rounds), preventing Session Fixation attacks, enforcing strict output encoding against Cross-Site Scripting (XSS), and eliminating stack trace leakage.
4. **Intelligent Conversational Assistance**: Embedding an asynchronous, rate-limited, domain-grounded conversational AI assistant (O4) capable of answering customer questions in real time with graceful degradation.

---

## 2. System Architecture & 3-Tier Layering

reshmamart adopts a strict **3-Tier MVC Architecture** that enforces clear separation of concerns across the codebase:

```
[ Client Browser / HTTP Request ]
               │
               ▼
┌─────────────────────────────────────────────────────────────┐
│ 1. Presentation & Controller Layer                          │
│    • Filters: EncodingFilter, CORSFilter, AuthFilter (RBAC) │
│    • Servlets: Auth, Product, Cart, Checkout, Order,        │
│                Seller, Admin, Review, Health, ChatServlet   │
│    • JSP Views: JSTL <c:out> XSS escaping, CSS, JS widgets  │
└──────────────────────────────┬──────────────────────────────┘
                               │ (DTOs)
                               ▼
┌─────────────────────────────────────────────────────────────┐
│ 2. Business Logic / Service Layer                           │
│    • UserServiceImpl, ProductServiceImpl, CartServiceImpl   │
│    • OrderServiceImpl (Transaction Coordinator)             │
│    • ReviewServiceImpl, ChatServiceImpl (Rate Limiter/Cache)│
│    • Providers: MockChatProvider, GeminiChatProvider        │
└──────────────────────────────┬──────────────────────────────┘
                               │ (Entities)
                               ▼
┌─────────────────────────────────────────────────────────────┐
│ 3. Data Access Layer (Persistence)                          │
│    • HikariCP Connection Pool (AppContextListener Lifecycle)│
│    • UserDAO, ProductDAO, CartDAO, OrderDAO, ReviewDAO      │
│    • 100% PreparedStatement Parameterized SQL Queries      │
│    • Persistent H2 Database Engine (schema.sql / seed.sql)  │
└─────────────────────────────────────────────────────────────┘
```

---

## 3. Database Architecture (D1 ER Diagram)

The schema comprises 6 relational tables designed with third normal form (3NF) principles:
- **`users`**: Multi-role user identity store with unique email indexing and BCrypt password hashes.
- **`products`**: Product catalog with active status flags, category metadata, and `DECIMAL(10,2)` pricing.
- **`cart_items`**: User-specific cart entries with composite uniqueness constraint `(user_id, product_id)`.
- **`orders`**: Order ledger capturing buyer reference, total amount, shipping address, and fulfillment lifecycle (`PENDING` → `CONFIRMED` → `SHIPPED` → `DELIVERED`).
- **`order_items`**: Immutable audit records capturing product ID, quantity, and historical unit price at the time of purchase.
- **`reviews`**: Verified customer ratings (1 to 5 stars) and review comments.

*Refer to the full Mermaid diagram in [`docs/D1_ER_DIAGRAM.md`](file:///C:/Users/acer/.gemini/antigravity/scratch/reshmamart/docs/D1_ER_DIAGRAM.md).*

---

## 4. Software Design Patterns Implemented

| Pattern | Architectural Component | Purpose & Implementation |
| :--- | :--- | :--- |
| **Data Access Object (DAO)** | `UserDAO`, `ProductDAO`, `OrderDAO`, etc. | Decouples persistence queries and JDBC operations completely from business services. |
| **Front Controller / MVC** | `HttpServlet` instances (`ProductServlet`, etc.) | Centralizes request dispatching, parameter parsing, and view redirection. |
| **Singleton** | `DBConnectionPool.java` | Manages a single shared `HikariDataSource` connection pool across the entire JVM lifecycle. |
| **Strategy Pattern** | `ChatProvider` (`MockChatProvider`, `GeminiChatProvider`) | Enables dynamic interchange between rule-based local FAQ and real cloud LLM backends. |
| **Factory Method** | `ChatServiceImpl.resolveConfiguredProvider()` | Decides provider instantiation based on environment variables and `config.properties`. |
| **Data Transfer Object (DTO)** | `UserResponseDTO`, `OrderDTO`, `ProductDTO` | Prevents sensitive entity fields (e.g. `passwordHash`) from leaking to controllers or API responses. |

---

## 5. Transactional Integrity (D3 Place-Order Flow)

The checkout transaction coordinate (`OrderServiceImpl.placeOrder`) implements ACID semantics:
1. **Begin Transaction**: Disables auto-commit (`conn.setAutoCommit(false)`).
2. **Stock Verification**: Iterates through each cart item and locks current inventory. If any item is out of stock, immediately invokes `conn.rollback()` and throws `ValidationException`.
3. **Order Record Creation**: Inserts order row and captures generated primary key.
4. **Order Item Insertion & Stock Reduction**: Inserts each `order_item` and runs `UPDATE products SET stock_quantity = stock_quantity - ? WHERE id = ?`.
5. **Cart Clearance**: Deletes all items from `cart_items` for the buyer.
6. **Commit**: Executes `conn.commit()` and restores auto-commit.

*Refer to the complete sequence diagram in [`docs/D3_SEQUENCE_DIAGRAM.md`](file:///C:/Users/acer/.gemini/antigravity/scratch/reshmamart/docs/D3_SEQUENCE_DIAGRAM.md).*

---

## 6. AI Chatbot Subsystem (O4 / Phase 3)

The AI assistant provides real-time customer support:
- **Sliding-Window Rate Limiter**: Strictly enforces maximum 10 inquiries per minute per user session.
- **In-Memory Question Cache**: Caches identical questions within a user session to prevent redundant processing.
- **Domain Guardrails**: System instructions ground answers to the 28 reshmamart products, Indian Rupee (`₹`) pricing, delivery within 2–4 business days, and 7-day return policy.
- **Graceful Fallback**: If network partitions or quota limits occur, automatically degrades to `MockChatProvider` with zero interruption to the user experience.

*Refer to the complete sequence diagram in [`docs/D4_CHATBOT_SEQUENCE_DIAGRAM.md`](file:///C:/Users/acer/.gemini/antigravity/scratch/reshmamart/docs/D4_CHATBOT_SEQUENCE_DIAGRAM.md).*

---

## 7. Security Hardening & Section 9 Compliance

- **SQL Injection Defense**: 100% of database interactions are parameterized via `PreparedStatement`. Zero dynamic SQL string concatenations exist in the codebase.
- **Password Protection**: Passwords hashed using jBCrypt with 12 salt rounds; plaintext passwords never touch database or log output.
- **Session Security**: Session IDs are regenerated upon login to prevent Session Fixation attacks. 30-minute timeout explicitly enforced.
- **Role-Based Access Control**: `AuthFilter` intercepts protected paths (`/cart/*`, `/checkout/*`, `/orders/*`, `/seller/*`, `/admin/*`) and verifies user persona.
- **XSS Output Escaping**: All user-rendered outputs in JSPs are escaped via JSTL `<c:out>` or `${fn:escapeXml()}`.
- **Error Handling**: Custom error pages mapped in `web.xml` for HTTP 400, 403, 404, and 500 without disclosing stack traces.

---

## 8. Quality Assurance & Automated Testing

- **Automated Test Suite**: 30 comprehensive unit and integration tests covering DAOs, Services, Password utilities, and the Chatbot subsystem.
- **Results**: **30 Tests Run, 0 Failures, 0 Errors, 0 Skipped** (`BUILD SUCCESS`).
- **Continuous Integration**: Automated GitHub Actions workflow (`.github/workflows/build.yml`) running `mvn -B clean verify` on every push.

---

## 9. Cloud Deployment & DevOps

- **Containerization**: Multi-stage `Dockerfile` (`maven:3.9.6-alpine` build stage + `eclipse-temurin:17-jre-alpine` runtime stage).
- **Hosting Platform**: Live on **Render** Cloud Platform with automated GitHub deployment on `main`.
- **Live Health API**: `GET /api/v1/health` returning `{"status":"UP","db":"UP"}`.

---

## 10. Conclusion

reshmamart successfully delivers all required deliverables from Kickoff through Week 11 for the Anna University R2025 Semester 3 curriculum. The platform is fully operational, publicly accessible, secure, and resilient.
