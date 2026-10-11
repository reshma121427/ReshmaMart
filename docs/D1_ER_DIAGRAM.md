# D1: reshmamart Entity-Relationship Diagram (ERD)

## Database Architecture Overview
The **reshmamart** relational schema is implemented on **H2 Database** (v2.2.x) in persistent file mode (`jdbc:h2:file:./data/reshmamart`). It comprises 6 normalized relational entities engineered with:
- Strict primary key (`id BIGINT AUTO_INCREMENT PRIMARY KEY`) design.
- `DECIMAL(10,2)` representation for currency values to eliminate floating-point rounding errors.
- Foreign key constraints with cascading or restricting integrity rules.
- Explicit database indexes on high-frequency search and join foreign keys.
- Timestamp auditing (`created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP`) across all tables.

---

## Mermaid Entity-Relationship Diagram

```mermaid
erDiagram
    USERS ||--o{ PRODUCTS : "lists / manages"
    USERS ||--o{ ORDERS : "places"
    USERS ||--o{ CART_ITEMS : "owns"
    USERS ||--o{ REVIEWS : "authors"
    
    PRODUCTS ||--o{ CART_ITEMS : "referenced in"
    PRODUCTS ||--o{ ORDER_ITEMS : "purchased in"
    PRODUCTS ||--o{ REVIEWS : "evaluated in"
    
    ORDERS ||--|{ ORDER_ITEMS : "contains"
    ORDERS ||--o{ REVIEWS : "verified by"

    USERS {
        BIGINT id PK "Auto Increment"
        VARCHAR email UK "Unique, Not Null"
        VARCHAR password_hash "BCrypt Hash, Not Null"
        VARCHAR name "Full Name, Not Null"
        VARCHAR role "BUYER | SELLER | ADMIN"
        TIMESTAMP created_at "Audit Timestamp"
    }

    PRODUCTS {
        BIGINT id PK "Auto Increment"
        BIGINT seller_id FK "References USERS(id)"
        VARCHAR name "Product Title, Not Null"
        TEXT description "Detailed Description"
        DECIMAL price "DECIMAL(10,2), >= 0"
        INTEGER stock_quantity "Available Stock, >= 0"
        VARCHAR category "Electronics | Fashion | Books | etc"
        VARCHAR image_url "Product Image Asset URL"
        BOOLEAN is_active "Soft Delete Flag"
        TIMESTAMP created_at "Audit Timestamp"
    }

    CART_ITEMS {
        BIGINT id PK "Auto Increment"
        BIGINT user_id FK "References USERS(id)"
        BIGINT product_id FK "References PRODUCTS(id)"
        INTEGER quantity "Purchase Quantity, >= 1"
        TIMESTAMP created_at "Audit Timestamp"
    }

    ORDERS {
        BIGINT id PK "Auto Increment"
        BIGINT buyer_id FK "References USERS(id)"
        DECIMAL total_amount "DECIMAL(10,2), >= 0"
        VARCHAR status "PENDING | CONFIRMED | SHIPPED | DELIVERED | CANCELLED"
        TEXT shipping_address "Delivery Destination"
        VARCHAR payment_status "PENDING | COMPLETED | FAILED"
        TIMESTAMP created_at "Order Placed Timestamp"
    }

    ORDER_ITEMS {
        BIGINT id PK "Auto Increment"
        BIGINT order_id FK "References ORDERS(id) ON DELETE CASCADE"
        BIGINT product_id FK "References PRODUCTS(id)"
        INTEGER quantity "Quantity Ordered, >= 1"
        DECIMAL unit_price "DECIMAL(10,2), Price at Purchase"
        TIMESTAMP created_at "Audit Timestamp"
    }

    REVIEWS {
        BIGINT id PK "Auto Increment"
        BIGINT order_id FK "References ORDERS(id)"
        BIGINT product_id FK "References PRODUCTS(id)"
        BIGINT buyer_id FK "References USERS(id)"
        INTEGER rating "Score 1 to 5"
        TEXT comment "Customer Review Text"
        TIMESTAMP created_at "Review Date"
    }
```

---

## Entity Specifications & Constraints

| Table Name | Primary Key | Foreign Keys | Key Constraints | Description |
| :--- | :--- | :--- | :--- | :--- |
| `users` | `id` | None | `email UNIQUE NOT NULL`, `role IN ('BUYER', 'SELLER', 'ADMIN')` | Stores credentials and user personas |
| `products` | `id` | `seller_id` &rarr; `users(id)` | `price >= 0`, `stock_quantity >= 0`, `is_active DEFAULT TRUE` | Active and moderated product listings |
| `cart_items` | `id` | `user_id` &rarr; `users(id)`, `product_id` &rarr; `products(id)` | `quantity >= 1`, unique compound index `(user_id, product_id)` | Session shopping cart with running total |
| `orders` | `id` | `buyer_id` &rarr; `users(id)` | `status IN ('PENDING','CONFIRMED','SHIPPED','DELIVERED','CANCELLED')` | Buyer orders and fulfillment status |
| `order_items` | `id` | `order_id` &rarr; `orders(id)`, `product_id` &rarr; `products(id)` | `quantity >= 1`, `unit_price >= 0` | Historical snapshot of items and price at checkout |
| `reviews` | `id` | `order_id`, `product_id`, `buyer_id` | `rating BETWEEN 1 AND 5` | Verified customer reviews and ratings |
