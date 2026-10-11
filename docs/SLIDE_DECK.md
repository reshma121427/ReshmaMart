# reshmamart — Capstone Final Evaluation Slide Deck

**Anna University R2025 Semester 3 Capstone Evaluation**  
**Project:** reshmamart (`com.reshmamart`) | **Author:** Rubini  
**Live URL:** [https://reshmamart.onrender.com](https://reshmamart.onrender.com)  

---

### Slide 1: Title & Introduction
- **Slide Title**: reshmamart — Enterprise-Grade E-Commerce Platform with Modular AI Shopping Assistant
- **Presenter**: Rubini (Anna University R2025, Semester 3)
- **Tech Stack**: Java 17 · Apache Tomcat 9.0.x · JDBC & HikariCP · H2 Database · Docker · Google Gemini AI
- **Speaker Notes**: "Good morning, respected evaluators. Today I present reshmamart, a full-stack multi-role e-commerce web platform engineered with strict 3-tier layering, ACID transactional checkout, robust security hardening, and an intelligent AI shopping assistant."

---

### Slide 2: Problem Statement & Objectives
- **Key Challenges in Student E-Commerce Systems**:
  - Inadequate role boundaries between shoppers, vendors, and platform admins.
  - Race conditions and inventory overselling during concurrent checkouts.
  - Common web vulnerabilities: SQL Injection, plaintext passwords, session hijacking, XSS.
- **reshmamart Objectives**:
  - Implement full shopping lifecycle (F1–F8) across Buyer, Seller, and Admin personas.
  - Ensure 100% ACID checkout integrity with atomic stock deduction and rollback.
  - Implement defense-in-depth security matching industrial benchmarks.
  - Deploy a resilient AI conversational assistant (O4) with sliding-window rate limiting.

---

### Slide 3: 3-Tier Layered Architecture
- **Layer Breakdown**:
  - **Presentation / Web Layer**: `HttpServlet` controllers, `AuthFilter` (RBAC), `EncodingFilter`, `CORSFilter`, JSP views with JSTL `<c:out>` output escaping.
  - **Business Logic Layer**: Services implementing input validation, DTO transformation, and transaction orchestration.
  - **Persistence Layer**: Data Access Objects (DAOs) using HikariCP connection pooling and 100% `PreparedStatement` queries against persistent H2 database.
- **Design Patterns**: DAO, MVC / Front Controller, Singleton, Strategy, Factory, DTO, Builder.

---

### Slide 4: Database Design & Normalization (D1 ERD)
- **6 Normalized Tables**: `users`, `products`, `cart_items`, `orders`, `order_items`, `reviews`.
- **Engineering Highlights**:
  - Currency strictly formatted as `DECIMAL(10,2)` in Indian Rupees (₹) to eliminate rounding errors.
  - Foreign key cascading and deletion constraints preventing orphaned order records.
  - Composite unique indexes on `(user_id, product_id)` in cart to prevent duplicates.
  - Audit timestamps (`created_at`) on every entity.

---

### Slide 5: Core Functional Highlights (F1 – F8 + O2)
- **F1 Authentication**: Role-based signup (Buyer & Seller), seeded Admin; BCrypt 12 rounds.
- **F2 & F3 Catalog & Search**: 28 verified products across 5 categories with combined search and pagination.
- **F4 Cart**: Dynamic cart with running subtotal, item removal, and stock bounds checks.
- **F5 & O2 Checkout & Order Lifecycle**: Atomic checkout with mock payment (Card, UPI, COD) and status progression (`PENDING` &rarr; `CONFIRMED` &rarr; `SHIPPED` &rarr; `DELIVERED`).
- **F6 & F7 Seller & Admin Portals**: Dedicated seller inventory management and admin platform governance.
- **F8 Reviews & Ratings**: Post-purchase 1–5 star reviews with dynamic product rating updates.

---

### Slide 6: Modular AI Shopping Assistant (O4 / Phase 3)
- **Dual-Mode Strategy Architecture**:
  - **Mock Provider**: Instant, deterministic responses to 11+ domain topics (categories, pricing in ₹, shipping timelines, returns, order tracking) with 0 external network dependencies.
  - **Gemini Provider**: Cloud LLM integration via server-side HTTP proxy using a grounded domain system instruction prompt.
- **Resilience Features**:
  - Sliding-window rate limiting: Enforces maximum 10 inquiries per minute per session.
  - In-memory LRU session cache: Eliminates redundant calls on identical questions.
  - Graceful fallback: Automatically degrades to mock answers if cloud API limits are hit.

---

### Slide 7: Security Engineering & Compliance
- **100% PreparedStatement**: Zero string concatenation in SQL queries across all DAOs.
- **BCrypt Password Protection**: Salted password hashing with 12 rounds; zero plaintext credentials logged.
- **Session Fixation Defense**: Immediate `session.invalidate()` and session ID regeneration upon successful login.
- **Safe Error Pages**: Custom error pages for HTTP 400, 403, 404, and 500 without disclosing stack traces.

---

### Slide 8: Quality Assurance & Testing Results
- **Automated Test Suite**: 30 comprehensive unit and integration tests.
  - `UserDAOTest`, `ProductDAOTest`, `OrderDAOTest` (Embedded H2 testing).
  - `UserServiceTest`, `ProductServiceTest`, `OrderServiceTest` (Mockito DAO mocking).
  - `PasswordUtilTest` (BCrypt hashing and salt verification).
  - `ChatServiceTest` (Rate limiting, question caching, input validation).
- **Execution Summary**: **30 Tests Run, 0 Failures, 0 Errors, 0 Skipped** (`BUILD SUCCESS`).

---

### Slide 9: DevOps & Cloud Deployment
- **Containerization**: Multi-stage `Dockerfile` (`maven:3.9.6-alpine` build + `eclipse-temurin:17-jre-alpine` runtime).
- **Cloud Hosting**: Deployed on **Render** Cloud Platform with continuous deployment from GitHub `main`.
- **Live Health API**: `GET /api/v1/health` &rarr; `{"status":"UP","db":"UP"}`.
- **Public URL**: [https://reshmamart.onrender.com](https://reshmamart.onrender.com).

---

### Slide 10: Conclusion & Q&A
- **Key Takeaways**:
  - reshmamart fulfills 100% of the Anna University R2025 Semester 3 Capstone requirements across all 11 weeks.
  - Production-ready, fully deployed, and verified against live cloud traffic.
- **Live Demo Credentials**:
  - Buyer: `john.buyer@mart.com` / `Buyer@123`
  - Seller: `techseller@mart.com` / `Seller@123`
  - Admin: `admin@mart.com` / `Admin@123`
- *Thank you! Opening the floor for questions.*
