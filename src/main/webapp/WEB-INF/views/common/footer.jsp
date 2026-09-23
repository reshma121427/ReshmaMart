<%@ page contentType="text/html;charset=UTF-8" language="java" %>
</main>

<!-- Multi-tier Classical Footer -->
<footer class="site-footer">
    <div class="footer-top">
        <div class="container footer-grid">
            <div class="footer-col">
                <div class="brand-logo" style="margin-bottom: var(--space-4);">
                    <div class="brand-symbol" style="background: var(--color-primary-hover); color: #ffffff;" aria-hidden="true">
                        <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                            <path d="M6.5 8H17.5L16.6 20.2C16.53 21.19 15.7 21.95 14.71 21.95H9.29C8.3 21.95 7.47 21.19 7.4 20.2L6.5 8Z" stroke="currentColor" stroke-width="1.6" stroke-linejoin="round"/>
                            <path d="M9 8V6.2C9 4.43 10.43 3 12.2 3H11.8C13.57 3 15 4.43 15 6.2V8" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round"/>
                            <path d="M12 11.3C10.6 12.6 10.6 14.4 12 15.7C13.4 14.4 13.4 12.6 12 11.3Z" fill="currentColor"/>
                        </svg>
                    </div>
                    <span class="brand-name" style="color: #ffffff;">Reshma<span style="color: var(--color-accent);">Mart</span></span>
                </div>
                <p style="color: #94a3b8; font-size: 0.875rem; line-height: 1.6;">
                    A curated multi-seller commerce destination engineered for discerning buyers and verified artisans. Built on enterprise Java, Servlets, HikariCP, and H2.
                </p>
            </div>
            <div class="footer-col">
                <h4>Collections</h4>
                <ul>
                    <li><a href="${pageContext.request.contextPath}/products?category=Electronics">Fine Electronics</a></li>
                    <li><a href="${pageContext.request.contextPath}/products?category=Books">Timeless Literature</a></li>
                    <li><a href="${pageContext.request.contextPath}/products?category=Clothing">Apparel &amp; Textiles</a></li>
                    <li><a href="${pageContext.request.contextPath}/products?category=Home">Home &amp; Sanctuary</a></li>
                </ul>
            </div>
            <div class="footer-col">
                <h4>Marketplace</h4>
                <ul>
                    <li><a href="${pageContext.request.contextPath}/products">Browse Catalog</a></li>
                    <li><a href="${pageContext.request.contextPath}/cart">Shopping Cart</a></li>
                    <li><a href="${pageContext.request.contextPath}/orders">Track Orders</a></li>
                    <li><a href="${pageContext.request.contextPath}/seller/dashboard">Merchant Portal</a></li>
                </ul>
            </div>
            <div class="footer-col">
                <h4>Integrity &amp; Standards</h4>
                <ul>
                    <li><span style="color: #cbd5e1; font-size: 0.875rem;">7-Day Refund Guarantee</span></li>
                    <li><span style="color: #cbd5e1; font-size: 0.875rem;">Verified Merchant Auditing</span></li>
                    <li><span style="color: #cbd5e1; font-size: 0.875rem;">Transactional Escrow</span></li>
                    <li><span style="color: #cbd5e1; font-size: 0.875rem;">Zero Injected Adware</span></li>
                </ul>
            </div>
        </div>
    </div>
    <div class="footer-bottom">
        <div class="container">
            <p>&copy; 2026 ReshmaMart. Academic Capstone Project. Engineered with classical precision.</p>
        </div>
    </div>
</footer>

<!-- Global Component Containers -->
<div id="toast-container" aria-live="polite"></div>
<div id="confirm-modal" class="modal-backdrop" role="dialog" aria-modal="true"></div>
<div id="global-loader"></div>

<script src="${pageContext.request.contextPath}/assets/js/main.js"></script>
</body>
</html>
