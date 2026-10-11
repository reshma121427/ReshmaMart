# D3: reshmamart Place-Order Transaction Sequence Diagram

## Transactional Architecture
The **Place-Order flow** in reshmamart is engineered as an **ACID-compliant atomic transaction** managed via JDBC Connection controls (`setAutoCommit(false)`, `commit()`, `rollback()`). If inventory becomes insufficient or a database constraint fails, the entire transaction rolls back cleanly, preventing inventory leakage and inconsistent order states.

---

## Mermaid Sequence Diagram

```mermaid
sequenceDiagram
    autonumber
    actor Buyer as Buyer (Browser)
    participant Auth as AuthFilter
    participant Controller as CheckoutServlet
    participant Service as OrderServiceImpl
    participant CartDAO as CartDAOImpl
    participant ProductDAO as ProductDAOImpl
    participant OrderDAO as OrderDAOImpl
    participant DB as H2 Database (HikariCP)

    Buyer->>Auth: POST /checkout (address, paymentMethod)
    Auth->>Auth: Validate HttpSession & BUYER role
    Auth->>Controller: Forward authenticated request
    
    Controller->>Controller: Sanitize inputs (ValidationUtil)
    Controller->>Service: placeOrder(buyerId, address, paymentMethod)
    
    Service->>DB: Connection conn = pool.getConnection()
    Service->>DB: conn.setAutoCommit(false) [BEGIN TRANSACTION]
    
    Service->>CartDAO: getCartByUserId(conn, buyerId)
    CartDAO->>DB: SELECT * FROM cart_items WHERE user_id = ?
    DB-->>CartDAO: List<CartItem>
    CartDAO-->>Service: Cart with items & totalAmount
    
    alt Cart is Empty
        Service->>DB: conn.rollback()
        Service-->>Controller: ValidationException("Cart is empty")
        Controller-->>Buyer: HTTP 400 / Redirect with Error Alert
    else Cart has Items
        loop For each CartItem
            Service->>ProductDAO: getById(conn, productId)
            ProductDAO->>DB: SELECT * FROM products WHERE id = ?
            DB-->>ProductDAO: Product entity
            ProductDAO-->>Service: Current Product & stock_quantity
            
            alt Insufficient Stock (requested > stock)
                Service->>DB: conn.rollback()
                Service-->>Controller: ValidationException("Insufficient stock for product")
                Controller-->>Buyer: HTTP 400 / Redirect with Out-of-Stock Alert
            end
        end
        
        Note over Service,DB: All items validated. Proceeding to persist order.
        
        Service->>OrderDAO: createOrder(conn, order)
        OrderDAO->>DB: INSERT INTO orders (buyer_id, total_amount, status, shipping_address, payment_status)
        DB-->>OrderDAO: Generated Order ID
        OrderDAO-->>Service: Persisted Order
        
        loop For each CartItem
            Service->>OrderDAO: createOrderItem(conn, orderId, item)
            OrderDAO->>DB: INSERT INTO order_items (order_id, product_id, quantity, unit_price)
            
            Service->>ProductDAO: decreaseStock(conn, productId, quantity)
            ProductDAO->>DB: UPDATE products SET stock_quantity = stock_quantity - ? WHERE id = ?
        end
        
        Service->>CartDAO: clearCart(conn, buyerId)
        CartDAO->>DB: DELETE FROM cart_items WHERE user_id = ?
        
        Service->>DB: conn.commit() [COMMIT TRANSACTION]
        Service->>DB: conn.setAutoCommit(true)
        Service-->>Controller: OrderDTO (Status: PENDING, Payment: COMPLETED)
        
        Controller-->>Buyer: HTTP 302 Redirect to /orders/detail?id={orderId}&placed=true
    end
```
