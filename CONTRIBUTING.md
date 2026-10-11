# Contributing to reshmamart

Thank you for contributing to the **reshmamart** Capstone E-Commerce platform! This guide provides the step-by-step developer onboarding process from cloning to running and contributing code.

---

## 1. Prerequisites

Ensure the following developer tools are installed on your workstation:
- **Java Development Kit (JDK)**: Version 17 LTS (e.g., Eclipse Temurin 17 or Oracle JDK 17).
- **Apache Maven**: Version 3.8.x or 3.9.x.
- **Git**: Version 2.30+.
- **Web Browser**: Modern browser (Chrome, Firefox, Safari, Edge).

Verify your environment by running:
```bash
java -version
mvn -version
git --version
```

---

## 2. Quickstart: Clone to Running Instance

### Step 1: Clone the Repository
```bash
git clone https://github.com/Rubiniponnar/reshmamart.git
cd reshmamart
```

### Step 2: Environment Configuration
Copy the template configuration file:
```bash
cp .env.example .env
```
Default database and port parameters are pre-configured in `src/main/resources/config.properties`.

### Step 3: Compile and Run Tests
```bash
mvn clean test
```
*All 30 unit and integration tests should pass with 0 failures.*

### Step 4: Launch the Local Embedded Server
```bash
mvn compile exec:java
```
The embedded Apache Tomcat server will boot up and bind to:
- **Storefront**: [http://localhost:8085/](http://localhost:8085/)
- **Health Check API**: [http://localhost:8085/api/v1/health](http://localhost:8085/api/v1/health)

---

## 3. Seed Accounts for Local Testing

| Role | Email | Password | Access Area |
| :--- | :--- | :--- | :--- |
| **Buyer** | `john.buyer@mart.com` | `Buyer@123` | Storefront, Cart, Checkout, Order History, Reviews |
| **Seller** | `techseller@mart.com` | `Seller@123` | `/seller/dashboard`, Listing CRUD, Order Fulfillment |
| **Admin** | `admin@mart.com` | `Admin@123` | `/admin/dashboard`, User Management, Moderation |

---

## 4. Coding Standards & Git Workflow

- **Branching Model**: Keep `main` always deployable. Develop new features on `feature/<name>` branches.
- **Conventional Commits**: Use standard commit prefixes:
  - `feat:` for new capabilities (e.g. `feat: add AI chatbot widget`)
  - `fix:` for bug fixes (e.g. `fix: prevent primary key collision on orders`)
  - `test:` for test additions or refactors (e.g. `test: add ChatService rate limit test`)
  - `docs:` for documentation updates (e.g. `docs: add D1 ER diagram`)
- **Security Invariants**:
  - 100% of SQL queries must use `PreparedStatement` with parameterized placeholders (`?`). String concatenation in SQL is strictly rejected.
  - Never commit plaintext secrets, `.env` files, or production API keys.
