# reshmamart — 5-Minute Rehearsed Live Demo Script

**Anna University R2025 Semester 3 Capstone Evaluation**  
**Evaluation Target:** Full Project Demonstration (Kickoff to Final Review)  
**Live Cloud URL:** [https://reshmamart.onrender.com](https://reshmamart.onrender.com)  

---

## Pre-Demo Checklist
- [ ] Open browser tab: [https://reshmamart.onrender.com](https://reshmamart.onrender.com)
- [ ] Open second browser tab: [https://reshmamart.onrender.com/api/v1/health](https://reshmamart.onrender.com/api/v1/health) (confirm `{"status":"UP","db":"UP"}`)
- [ ] Prepare demo credentials cheat sheet.

---

## 5-Minute Rehearsed Demo Sequence

### Minute 0:00 – 1:00: Platform Architecture & Catalog Browsing (Visitor)
1. **Introduction**:
   - *"Welcome to reshmamart, a multi-role e-commerce platform built on Java 17 and Tomcat 9, deployed live on Render."*
2. **Catalog Browsing**:
   - Point out the 28 products displayed with prices in Indian Rupees (**₹**).
   - Click on the **Electronics** category filter &rarr; verify filtered view.
   - Search for **`keyboard`** in the search bar &rarr; see real-time search results.
   - Click **Details** on *Mechanical Gaming Keyboard* &rarr; showcase product specs, seller info, stock count, and 5-star customer reviews.

---

### Minute 1:00 – 2:30: Shopping Cart, Atomic Checkout & Order Tracking (Buyer)
1. **Sign In as Buyer**:
   - Click **Login** in the navbar &rarr; enter `john.buyer@mart.com` / `Buyer@123`.
   - Highlight that the navbar now greets **John Buyer [BUYER]** and shows the Cart and My Orders links.
2. **Add to Cart & Checkout**:
   - Navigate to *Mechanical Gaming Keyboard* &rarr; click **Add to Cart** (Quantity: 1).
   - View `/cart` &rarr; showcase running subtotal and total (**₹2,999.00**).
   - Click **Proceed to Checkout**.
   - Enter shipping address: `42 Anna Salai, Chennai, Tamil Nadu - 600002`.
   - Select payment method: **UPI / QR Code (Instant Approval)**.
   - Click **Confirm & Place Order**.
3. **Verify Order**:
   - Explain the underlying atomic JDBC transaction: stock verified, order inserted, order items recorded, stock decremented, and cart cleared in one atomic block.
   - Showcase the generated order detail page (Status: **PENDING**, Payment: **COMPLETED**).

---

### Minute 2:30 – 3:30: Seller Order Fulfillment & Inventory Management (Seller)
1. **Sign In as Seller**:
   - Logout &rarr; Login as `techseller@mart.com` / `Seller@123`.
   - Showcase the **Seller Portal** (`/seller/dashboard`) with listed products and stock counts.
2. **Order Fulfillment**:
   - Click **Seller Orders** (`/seller/orders`).
   - Locate the newly placed incoming order for *Mechanical Gaming Keyboard*.
   - Use the **Update Status** dropdown &rarr; transition status from **`PENDING`** &rarr; **`SHIPPED`** &rarr; click **Update**.
   - Verify the badge updates to cyan **SHIPPED**.

---

### Minute 3:30 – 4:15: Administrative Governance & GMV Oversight (Admin)
1. **Sign In as Admin**:
   - Logout &rarr; Login as `admin@mart.com` / `Admin@123`.
   - View the **System Administration Dashboard** (`/admin/dashboard`).
2. **Demonstrate Metrics**:
   - Showcase real-time platform statistics:
     - Total Users: 5 registered accounts.
     - Total Products: 28 active listings.
     - Platform Revenue: Cumulative GMV calculated in Rupees (**₹**).
   - Click **Product Moderation** (`/admin/listings`) to showcase admin ability to deactivate or reactivate seller listings.

---

### Minute 4:15 – 5:00: AI Shopping Assistant Demonstration (O4)
1. **Open AI Chatbot**:
   - Click the floating **💬 AI** button in the bottom-right corner of the screen.
   - The modern glassmorphic chat modal smoothly slides open.
2. **Domain Inquiries (Live Demonstration)**:
   - Click the **🛍️ Categories** chip &rarr; AI assistant immediately returns all 5 categories and sample products.
   - Ask: *"What is your return policy?"* &rarr; AI answers with the 7-day return window and refund timeline.
   - Ask: *"What payment methods are supported?"* &rarr; AI answers with Cards, UPI, and COD.
   - Ask: *"How can I track my order?"* &rarr; AI directs the user to the `/orders` portal.
3. **Showcase Defense & Rate Limiting**:
   - Explain the sliding-window rate limiter protecting against spam (10 msg/min) and in-memory question caching.

---

## Conclusion
*"reshmamart fulfills every functional requirement (F1–F8), operational extensions (O2, O4), security standards (Section 9), and deployment benchmarks across the 11-week Anna University R2025 curriculum. Thank you!"*
