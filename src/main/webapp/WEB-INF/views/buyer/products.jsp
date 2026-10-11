<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="pageTitle" value="Product Catalog" />
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<%-- AI Shopping Assistant Feature Banner --%>
<div style="background: linear-gradient(135deg, #FFF9F5 0%, #F5EBF4 100%); border: 1px solid var(--border-color); border-radius: var(--radius); padding: 1rem 1.25rem; margin-bottom: 1.5rem; display: flex; align-items: center; justify-content: space-between; flex-wrap: wrap; gap: 1rem; box-shadow: var(--shadow);">
    <div style="display: flex; align-items: center; gap: 0.85rem;">
        <span style="font-size: 2.2rem; line-height: 1;">🤖</span>
        <div>
            <strong style="color: var(--primary); font-size: 1.05rem; display: block; margin-bottom: 0.2rem;">ReshmaMart AI Shopping Assistant</strong>
            <span style="color: var(--text-muted); font-size: 0.88rem;">Need product recommendations in ₹, delivery timelines, or order tracking help?</span>
        </div>
    </div>
    <button type="button" onclick="if(window.openReshmaChat)window.openReshmaChat();" class="btn btn-primary" style="display: inline-flex; align-items: center; gap: 0.4rem; font-weight: 600; padding: 0.55rem 1.1rem; border-radius: 8px;">
        💬 Chat with AI Assistant
    </button>
</div>

<%-- Search & Filter Controls --%>
<div style="background: white; padding: 1.25rem; border-radius: var(--radius); border: 1px solid var(--border-color); box-shadow: var(--shadow); margin-bottom: 2rem;">
    <form action="${pageContext.request.contextPath}/products" method="GET" style="display: flex; gap: 1rem; flex-wrap: wrap; align-items: flex-end;">
        <div style="flex: 2; min-width: 200px;">
            <label class="form-label" for="keyword">Search Products</label>
            <input type="text" id="keyword" name="keyword" class="form-control"
                   placeholder="Search by name or description..." value="<c:out value='${keyword}'/>">
        </div>

        <div style="flex: 1; min-width: 150px;">
            <label class="form-label" for="category">Category</label>
            <select id="category" name="category" class="form-control">
                <option value="ALL" ${selectedCategory == 'ALL' ? 'selected' : ''}>All Categories</option>
                <c:forEach var="cat" items="${categories}">
                    <option value="<c:out value='${cat}'/>" ${selectedCategory == cat ? 'selected' : ''}>
                        <c:out value="${cat}"/>
                    </option>
                </c:forEach>
            </select>
        </div>

        <div style="flex: 1; min-width: 150px;">
            <label class="form-label" for="sortBy">Sort By</label>
            <select id="sortBy" name="sortBy" class="form-control">
                <option value="newest" ${sortBy == 'newest' ? 'selected' : ''}>Newest Arrivals</option>
                <option value="price_asc" ${sortBy == 'price_asc' ? 'selected' : ''}>Price: Low to High</option>
                <option value="price_desc" ${sortBy == 'price_desc' ? 'selected' : ''}>Price: High to Low</option>
                <option value="rating" ${sortBy == 'rating' ? 'selected' : ''}>Highest Rated</option>
            </select>
        </div>

        <div>
            <button type="submit" class="btn btn-primary">Filter</button>
            <a href="${pageContext.request.contextPath}/products" class="btn btn-secondary">Reset</a>
        </div>
    </form>
</div>

<%-- Product Grid --%>
<div>
    <div style="display: flex; justify-content: space-between; align-items: center;">
        <h2>Explore Catalog</h2>
        <span style="color: var(--text-muted); font-size: 0.9rem;">
            Showing <c:out value="${totalProducts}"/> product(s)
        </span>
    </div>

    <c:choose>
        <c:when test="${empty products}">
            <div style="text-align: center; padding: 4rem 1rem; background: white; border-radius: var(--radius); margin-top: 1.5rem; border: 1px solid var(--border-color);">
                <p style="font-size: 1.2rem; color: var(--text-muted); margin-bottom: 1rem;">No products found matching your search.</p>
                <a href="${pageContext.request.contextPath}/products" class="btn btn-primary">View All Products</a>
            </div>
        </c:when>
        <c:otherwise>
            <div class="grid-products">
                <c:forEach var="p" items="${products}">
                    <div class="card">
                        <c:choose>
                            <c:when test="${not empty p.imageUrl}">
                                <img src="<c:out value='${p.imageUrl}'/>" alt="<c:out value='${p.name}'/>" class="card-img"
                                     onerror="this.src='https://placehold.co/400x300?text=Product+Image'">
                            </c:when>
                            <c:otherwise>
                                <div class="card-img" style="display:flex;align-items:center;justify-content:center;color:#94a3b8;font-size:2rem;">📦</div>
                            </c:otherwise>
                        </c:choose>
                        <div class="card-body">
                            <span class="card-category"><c:out value="${p.category}"/></span>
                            <a href="${pageContext.request.contextPath}/products/detail?id=${p.id}" class="card-title">
                                <c:out value="${p.name}"/>
                            </a>

                            <div style="display: flex; align-items: center; gap: 0.35rem; margin-bottom: 0.5rem;">
                                <span class="star-rating">★</span>
                                <span style="font-weight: 600; font-size: 0.9rem;">
                                    <c:out value="${p.averageRating != null ? String.format('%.1f', p.averageRating) : '0.0'}"/>
                                </span>
                                <span style="color: var(--text-muted); font-size: 0.8rem;">
                                    (<c:out value="${p.reviewCount}"/> reviews)
                                </span>
                            </div>

                            <div class="card-price">&#8377;<c:out value="${p.price}"/></div>

                            <div class="card-footer">
                                <c:choose>
                                    <c:when test="${p.stockQty > 0}">
                                        <span class="badge" style="background-color: #dcfce7; color: #15803d;">
                                            In Stock (<c:out value="${p.stockQty}"/>)
                                        </span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="badge" style="background-color: #fee2e2; color: #b91c1c;">
                                            Out of Stock
                                        </span>
                                    </c:otherwise>
                                </c:choose>

                                <a href="${pageContext.request.contextPath}/products/detail?id=${p.id}" class="btn btn-secondary btn-sm">
                                    Details
                                </a>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>

            <%-- Pagination --%>
            <c:if test="${totalPages > 1}">
                <div style="display: flex; justify-content: center; gap: 0.5rem; margin-top: 2.5rem;">
                    <c:if test="${currentPage > 1}">
                        <a href="${pageContext.request.contextPath}/products?category=<c:out value='${selectedCategory}'/>&keyword=<c:out value='${keyword}'/>&sortBy=<c:out value='${sortBy}'/>&page=${currentPage - 1}"
                           class="btn btn-secondary btn-sm">&laquo; Prev</a>
                    </c:if>

                    <c:forEach var="pageNum" begin="1" end="${totalPages}">
                        <a href="${pageContext.request.contextPath}/products?category=<c:out value='${selectedCategory}'/>&keyword=<c:out value='${keyword}'/>&sortBy=<c:out value='${sortBy}'/>&page=${pageNum}"
                           class="btn btn-sm ${pageNum == currentPage ? 'btn-primary' : 'btn-secondary'}">
                            ${pageNum}
                        </a>
                    </c:forEach>

                    <c:if test="${currentPage < totalPages}">
                        <a href="${pageContext.request.contextPath}/products?category=<c:out value='${selectedCategory}'/>&keyword=<c:out value='${keyword}'/>&sortBy=<c:out value='${sortBy}'/>&page=${currentPage + 1}"
                           class="btn btn-secondary btn-sm">Next &raquo;</a>
                    </c:if>
                </div>
            </c:if>
        </c:otherwise>
    </c:choose>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
