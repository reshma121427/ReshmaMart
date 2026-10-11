# reshmamart — Manual End-to-End Test Cases

This document details the step-by-step manual test cases covering the complete customer and merchant journey (**Register → Browse → Cart → Order → Fulfillment → Review**).

---

## Test Suite Summary

| Test ID | Journey Step | Objective | Expected Result | Status |
|---|---|---|---|---|
| **TC-01** | Registration | Register a new Buyer account | Account created, redirected to login with success alert | PASS |
| **TC-02** | Security / Auth | Attempt duplicate email registration | System rejects with HTTP 400 and field error "An account with this email already exists" | PASS |
| **TC-03** | Authentication | Login as Buyer | Session regenerated, user name displayed in navbar | PASS |
| **TC-04** | Catalog & Search | Search keyword "Headphones" and filter by "Electronics" | Only matching active electronics appear in grid | PASS |
| **TC-05** | Cart Management | Add 2 units of Noise-Cancelling Headphones to Cart | Cart item count increments, running subtotal and total update | PASS |
| **TC-06** | Edge Case | Attempt to add more items than available stock | System rejects with error message displaying remaining stock | PASS |
| **TC-07** | Checkout Flow | Submit shipping address & select mock payment | Order created with status `PENDING`, cart cleared, stock decremented | PASS |
| **TC-08** | Edge Case | Attempt checkout with an empty cart | System redirects to cart with error "Your cart is empty" | PASS |
| **TC-09** | Seller Fulfillment | Login as `techseller@mart.com` and advance status to `SHIPPED` | Order status changes to `SHIPPED`, visible on seller & buyer dashboards | PASS |
| **TC-10** | Review Submission | Buyer views delivered order and submits 5-star review | Review saved, product detail page reflects new rating and comment | PASS |
| **TC-11** | Admin Oversight | Login as `admin@mart.com` and disable a listing | Product becomes inactive and disappears from buyer catalog | PASS |
| **TC-12** | Health API | Call `GET /api/v1/health` | Returns HTTP 200 `{"status":"UP","db":"UP"}` | PASS |

---

## Step-by-Step Test Execution Guide

### 1. Registration (TC-01)
1. Navigate to `http://localhost:8080/register`.
2. Fill in:
   - Full Name: `Test Buyer`
   - Email: `testbuyer1@example.com`
   - Password: `Password@123`
   - Confirm Password: `Password@123`
   - Account Type: `Buyer`
3. Click **Register**.
4. **Verification**: Redirected to `/login` with green banner: *"Registration successful! Please log in with your credentials."*

### 2. Login & Session (TC-03)
1. On `/login`, enter:
   - Email: `testbuyer1@example.com`
   - Password: `Password@123`
2. Click **Sign In**.
3. **Verification**: Logged in, navbar shows `Hello, Test Buyer` with `BUYER` badge.

### 3. Browse, Search & Filter (TC-04)
1. Go to `http://localhost:8080/products`.
2. In the search box, enter `Keyboard` and click **Filter**.
3. **Verification**: Only `Mechanical Gaming Keyboard` appears.
4. Click **Reset** to restore catalog.

### 4. Cart Operations (TC-05 & TC-06)
1. Click on `Mechanical Gaming Keyboard`.
2. Select quantity `2`, click **Add to Cart**.
3. **Verification**: Redirected to `/cart`, showing 2 items, Unit Price `$89.99`, Subtotal `$179.98`, Total `$179.98`.
4. Update quantity to `3` and click **Update**. Subtotal updates to `$269.97`.

### 5. Checkout & Mock Payment (TC-07)
1. In `/cart`, click **Proceed to Checkout**.
2. Enter delivery address: `123 Innovation Drive, Tech Park, Chennai, Tamil Nadu - 600001`.
3. Select **Credit / Debit Card (Mock Instant Approval)**.
4. Click **Confirm & Place Order**.
5. **Verification**: Redirected to `/orders/detail?id=...&placed=true` with status `PENDING`. Cart is now empty.

### 6. Seller Order Status Advancement (TC-09)
1. Log out, then log in as `techseller@mart.com` / `Seller@123`.
2. Navigate to `/seller/orders`.
3. Locate the order placed above.
4. Change status from `PENDING` to `CONFIRMED`, then to `SHIPPED`, then to `DELIVERED`.
5. **Verification**: Status updates successfully with confirmation banner.

### 7. Product Review (TC-10)
1. Log out, then log back in as `testbuyer1@example.com` / `Password@123`.
2. Go to `/orders`. Click **View Details** on the delivered order.
3. Under *Rate & Review Items in this Order*, select rating `★★★★★ (5/5)`, enter comment: `"Super fast delivery and great keyboard!"`.
4. Click **Submit Review**.
5. **Verification**: Review is displayed under the product detail page, and average rating updates.
