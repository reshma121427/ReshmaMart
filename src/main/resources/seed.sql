-- reshmamart Seed Data (v1.0 - INR Pricing & Expanded Catalog)
-- Passwords:
-- Admin: admin@mart.com / Admin@123
-- Sellers: techseller@mart.com, fashionhub@mart.com / Seller@123
-- Buyers: john.buyer@mart.com, alice.buyer@mart.com / Buyer@123

-- 1. Seed Users
INSERT INTO users (id, name, email, password_hash, role) VALUES
(1, 'System Admin', 'admin@mart.com', '$2a$12$TA8fek5qjgsBgJvjSwvlkOVQiGock98kZ89UtQY4V.tXI/p0JvP5S', 'ADMIN'),
(2, 'TechGizmo India', 'techseller@mart.com', '$2a$12$hrl37Xwf3jf.A2I4xscHO.nlGj4U3tFzHd4fVWtfyEagxeW7idmCC', 'SELLER'),
(3, 'StyleCraft Lifestyle', 'fashionhub@mart.com', '$2a$12$hrl37Xwf3jf.A2I4xscHO.nlGj4U3tFzHd4fVWtfyEagxeW7idmCC', 'SELLER'),
(4, 'John Buyer', 'john.buyer@mart.com', '$2a$12$/ppH/6MdqRkcUO1AnSEYdeU94twRIO7DLyfdgEsEsd9r.wOdFPSCC', 'BUYER'),
(5, 'Alice Smith', 'alice.buyer@mart.com', '$2a$12$/ppH/6MdqRkcUO1AnSEYdeU94twRIO7DLyfdgEsEsd9r.wOdFPSCC', 'BUYER'),
(6, 'reshmamart Admin', 'admin@reshmamart.com', '$2a$12$TA8fek5qjgsBgJvjSwvlkOVQiGock98kZ89UtQY4V.tXI/p0JvP5S', 'ADMIN');

-- 2. Seed Products (Indian Rupee INR Pricing)
INSERT INTO products (id, seller_id, name, description, price, stock_qty, category, image_url, is_active) VALUES
-- Electronics (Seller: 2)
(1, 2, 'Mechanical Gaming Keyboard', 'RGB backlit mechanical keyboard with tactile blue switches and detachable braided USB-C cable.', 2999.00, 35, 'Electronics', 'https://images.unsplash.com/photo-1587829741301-dc798b83add3?w=500&q=80', TRUE),
(2, 2, 'Noise-Cancelling Wireless Headphones', 'Over-ear Bluetooth 5.2 headphones with active noise cancellation and 35-hour playback.', 4499.00, 25, 'Electronics', 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=500&q=80', TRUE),
(3, 2, 'Ergonomic Optical Wireless Mouse', 'Precision 2.4GHz optical mouse with multi-device Bluetooth pairing and rechargeable battery.', 1299.00, 50, 'Electronics', 'https://images.unsplash.com/photo-1615663245857-ac93bb7c39e7?w=500&q=80', TRUE),
(4, 2, '27-inch 4K UHD IPS Monitor', 'Ultra HD IPS display with HDR10, 99% sRGB color gamut, and height-adjustable stand.', 24999.00, 10, 'Electronics', 'https://images.unsplash.com/photo-1527443224154-c4a3942d3acf?w=500&q=80', TRUE),
(5, 2, 'Smart Fitness Watch with SpO2', 'Waterproof smartwatch with continuous heart rate, blood oxygen tracking, and AMOLED display.', 2499.00, 30, 'Electronics', 'https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=500&q=80', TRUE),
(6, 2, '10000mAh 22.5W Fast Power Bank', 'Compact dual-output power bank with Power Delivery and Quick Charge 3.0 support.', 1199.00, 45, 'Electronics', 'https://images.unsplash.com/photo-1609091839311-d5365f9ff1c5?w=500&q=80', TRUE),
(7, 2, 'True Wireless Earbuds (ANC)', 'Low-latency gaming & music earbuds with 4-mic ENC and wireless charging case.', 3299.00, 20, 'Electronics', 'https://images.unsplash.com/photo-1590658268037-6bf12165a8df?w=500&q=80', TRUE),
(8, 2, 'USB-C 7-in-1 Aluminium Hub', 'Multiport adapter with 4K HDMI, 100W PD charging, SD card reader, and USB 3.0 ports.', 1899.00, 40, 'Electronics', 'https://images.unsplash.com/photo-1544652478-6653e09f18a2?w=500&q=80', TRUE),

-- Fashion (Seller: 3)
(9, 3, 'Classic Denim Trucker Jacket', 'Vintage washed rugged denim jacket with dual chest pockets and relaxed comfort fit.', 2199.00, 25, 'Fashion', 'https://images.unsplash.com/photo-1576995853123-5a10305d93c0?w=500&q=80', TRUE),
(10, 3, 'Lightweight Breathable Running Shoes', 'Mesh athletic trainers with impact-absorbing EVA foam sole for running and gym workouts.', 2499.00, 40, 'Fashion', 'https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=500&q=80', TRUE),
(11, 3, '100% Organic Cotton Crewneck T-Shirt', 'Ultra-soft pre-shrunk ring-spun cotton everyday casual tee in heather grey.', 699.00, 80, 'Fashion', 'https://images.unsplash.com/photo-1521572267360-ee0c2909d518?w=500&q=80', TRUE),
(12, 3, 'Waterproof Canvas Laptop Backpack', 'Multi-pocket 25L travel backpack with anti-theft back pocket and padded 15.6-inch compartment.', 1799.00, 30, 'Fashion', 'https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=500&q=80', TRUE),
(13, 3, 'Polarized UV400 Aviator Sunglasses', 'Classic metal frame sunglasses with anti-glare polarized lenses and protective hard case.', 999.00, 50, 'Fashion', 'https://images.unsplash.com/photo-1511499767150-a48a237f0083?w=500&q=80', TRUE),
(14, 3, 'Genuine Leather Formal Belt', 'Handcrafted full-grain leather belt with brushed stainless steel buckle for formal wear.', 849.00, 45, 'Fashion', 'https://images.unsplash.com/photo-1624222247344-550fb60583dc?w=500&q=80', TRUE),
(15, 3, 'Slim-Fit Stretch Cotton Chinos', 'Versatile casual trousers tailored with stretch cotton fabric for all-day comfort.', 1499.00, 35, 'Fashion', 'https://images.unsplash.com/photo-1473966968600-fa801b869a1a?w=500&q=80', TRUE),

-- Home & Kitchen (Seller: 2 & 3)
(16, 2, 'Double-Wall Insulated Steel Bottle (1L)', 'Sleek stainless steel flask keeps beverages hot for 12 hours or cold for 24 hours.', 799.00, 60, 'Home & Kitchen', 'https://images.unsplash.com/photo-1602143407151-7111542de6e8?w=500&q=80', TRUE),
(17, 2, '15-Bar Pump Espresso Coffee Machine', 'Compact barista-style espresso maker with manual milk frother wand and dual filter basket.', 8999.00, 12, 'Home & Kitchen', 'https://images.unsplash.com/photo-1517668808822-9ebb02f2a0e6?w=500&q=80', TRUE),
(18, 2, 'Modern LED Desk Lamp with Wireless Charger', 'Eye-caring touch control desk light with 5 color temperatures and 10W wireless fast charging base.', 1699.00, 25, 'Home & Kitchen', 'https://images.unsplash.com/photo-1534353436294-0dbd4bdac845?w=500&q=80', TRUE),
(19, 3, 'Pre-Seasoned Cast Iron Skillet (26cm)', 'Heavy-duty induction-compatible frying pan with superior heat retention for searing and baking.', 1450.00, 20, 'Home & Kitchen', 'https://images.unsplash.com/photo-1585515320310-259814833e62?w=500&q=80', TRUE),
(20, 3, 'Ceramic Stoneware Dinner Set (12 Pcs)', 'Microwave & dishwasher-safe artisanal ceramic dinner set including plates, bowls, and mugs.', 3499.00, 15, 'Home & Kitchen', 'https://images.unsplash.com/photo-1610701596007-11502861dcfa?w=500&q=80', TRUE),
(21, 2, 'Ultrasonic Essential Oil Diffuser (500ml)', 'Quiet cool-mist aromatherapy humidifier with 7 LED ambient mood lights and auto-shutoff.', 1299.00, 35, 'Home & Kitchen', 'https://images.unsplash.com/photo-1608571423902-eed4a5ad8108?w=500&q=80', TRUE),

-- Books (Seller: 3)
(22, 3, 'Clean Architecture & Software Design', 'A Craftsman’s Guide to Software Structure, Design Patterns, and Enterprise Microservices.', 999.00, 40, 'Books', 'https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=500&q=80', TRUE),
(23, 3, 'Modern Full-Stack Web Development', 'Comprehensive practical reference covering Java Servlets, REST APIs, and Modern Frontend.', 850.00, 30, 'Books', 'https://images.unsplash.com/photo-1532012164546-f432f2e3777a?w=500&q=80', TRUE),
(24, 3, 'Data Structures & Algorithms in Java', 'Complete guide with code examples, interview problems, and time-complexity optimizations.', 750.00, 50, 'Books', 'https://images.unsplash.com/photo-1512820790803-83ca734da794?w=500&q=80', TRUE),
(25, 3, 'Deep Learning: Foundations & Practice', 'In-depth coverage of neural networks, computer vision, and modern NLP architectures.', 1199.00, 25, 'Books', 'https://images.unsplash.com/photo-1497633762265-9d179a990aa6?w=500&q=80', TRUE),

-- Sports & Fitness (Seller: 2 & 3)
(26, 3, 'High-Density Anti-Skid Yoga Mat (6mm)', 'Eco-friendly TPE exercise mat with alignment lines, carrying strap, and cushioned support.', 899.00, 45, 'Sports & Fitness', 'https://images.unsplash.com/photo-1601925260368-ae2f83cf8b7f?w=500&q=80', TRUE),
(27, 2, 'Adjustable PVC Dumbbell Set (20kg)', 'Home gym barbell & dumbbell convertible weights set with spinlock collars and connector bar.', 3999.00, 15, 'Sports & Fitness', 'https://images.unsplash.com/photo-1584735935682-2f2b69dff9d2?w=500&q=80', TRUE),
(28, 2, 'Stainless Steel Protein Shaker (750ml)', 'Leak-proof gym water bottle with stainless steel whisk ball and measurement markings.', 599.00, 60, 'Sports & Fitness', 'https://images.unsplash.com/photo-1579722821273-0f6c7d44362f?w=500&q=80', TRUE);

-- 3. Seed Completed Orders for John Buyer (INR Values)
INSERT INTO orders (id, buyer_id, total_amount, status, shipping_address, payment_status, created_at) VALUES
(1, 4, 4298.00, 'DELIVERED', '742 Anna Salai, Guindy, Chennai, Tamil Nadu - 600025', 'COMPLETED', DATEADD('DAY', -7, CURRENT_TIMESTAMP)),
(2, 4, 4499.00, 'SHIPPED', '742 Anna Salai, Guindy, Chennai, Tamil Nadu - 600025', 'COMPLETED', DATEADD('DAY', -2, CURRENT_TIMESTAMP));

-- 4. Seed Order Items
INSERT INTO order_items (id, order_id, product_id, quantity, unit_price, created_at) VALUES
(1, 1, 1, 1, 2999.00, DATEADD('DAY', -7, CURRENT_TIMESTAMP)),
(2, 1, 3, 1, 1299.00, DATEADD('DAY', -7, CURRENT_TIMESTAMP)),
(3, 2, 2, 1, 4499.00, DATEADD('DAY', -2, CURRENT_TIMESTAMP));

-- 5. Seed Reviews for Delivered Items
INSERT INTO reviews (id, order_id, product_id, buyer_id, rating, comment, created_at) VALUES
(1, 1, 1, 4, 5, 'Outstanding mechanical keyboard! The keys feel crisp, tactile, and build quality is top-notch.', DATEADD('DAY', -5, CURRENT_TIMESTAMP)),
(2, 1, 3, 4, 4, 'Very comfortable wireless mouse for daily programming and gaming. Battery lasts weeks.', DATEADD('DAY', -4, CURRENT_TIMESTAMP));

-- 6. Restart auto-increment sequences past seed IDs
ALTER TABLE users ALTER COLUMN id RESTART WITH 100;
ALTER TABLE products ALTER COLUMN id RESTART WITH 100;
ALTER TABLE orders ALTER COLUMN id RESTART WITH 100;
ALTER TABLE order_items ALTER COLUMN id RESTART WITH 100;
ALTER TABLE reviews ALTER COLUMN id RESTART WITH 100;

