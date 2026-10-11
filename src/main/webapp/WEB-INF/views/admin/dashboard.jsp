<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="pageTitle" value="Admin Dashboard" />
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 2rem;">
    <div>
        <h2>System Administration Dashboard</h2>
        <p style="color: var(--text-muted);">Overview of ReshmaMart platform metrics and operations</p>
    </div>
</div>

<%-- Metric Cards --%>
<div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(220px, 1fr)); gap: 1.5rem; margin-bottom: 2.5rem;">
    <div style="background: white; padding: 1.5rem; border-radius: var(--radius); border: 1px solid var(--border-color); box-shadow: var(--shadow);">
        <div style="font-size: 0.85rem; color: var(--text-muted); font-weight: 600; text-transform: uppercase;">Total Users</div>
        <div style="font-size: 2.25rem; font-weight: bold; color: var(--primary); margin-top: 0.5rem;"><c:out value="${totalUsers}"/></div>
        <a href="${pageContext.request.contextPath}/admin/users" style="color: var(--primary); font-size: 0.85rem; text-decoration: none; display: inline-block; margin-top: 0.5rem;">View all users &rarr;</a>
    </div>

    <div style="background: white; padding: 1.5rem; border-radius: var(--radius); border: 1px solid var(--border-color); box-shadow: var(--shadow);">
        <div style="font-size: 0.85rem; color: var(--text-muted); font-weight: 600; text-transform: uppercase;">Total Products</div>
        <div style="font-size: 2.25rem; font-weight: bold; color: var(--success); margin-top: 0.5rem;"><c:out value="${totalProducts}"/></div>
        <a href="${pageContext.request.contextPath}/admin/listings" style="color: var(--primary); font-size: 0.85rem; text-decoration: none; display: inline-block; margin-top: 0.5rem;">Moderate listings &rarr;</a>
    </div>

    <div style="background: white; padding: 1.5rem; border-radius: var(--radius); border: 1px solid var(--border-color); box-shadow: var(--shadow);">
        <div style="font-size: 0.85rem; color: var(--text-muted); font-weight: 600; text-transform: uppercase;">Total Orders</div>
        <div style="font-size: 2.25rem; font-weight: bold; color: var(--primary); margin-top: 0.5rem;"><c:out value="${totalOrders}"/></div>
        <a href="${pageContext.request.contextPath}/admin/orders" style="color: var(--primary); font-size: 0.85rem; text-decoration: none; display: inline-block; margin-top: 0.5rem;">View all orders &rarr;</a>
    </div>

    <div style="background: white; padding: 1.5rem; border-radius: var(--radius); border: 1px solid var(--border-color); box-shadow: var(--shadow);">
        <div style="font-size: 0.85rem; color: var(--text-muted); font-weight: 600; text-transform: uppercase;">Platform Revenue</div>
        <div style="font-size: 2.25rem; font-weight: bold; color: #059669; margin-top: 0.5rem;">&#8377;<c:out value="${totalRevenue}"/></div>
        <span style="color: var(--text-muted); font-size: 0.85rem; display: inline-block; margin-top: 0.5rem;">Cumulative GMV</span>
    </div>
</div>

<%-- Quick Actions --%>
<div style="background: white; padding: 1.5rem; border-radius: var(--radius); border: 1px solid var(--border-color); box-shadow: var(--shadow);">
    <h3 style="margin-bottom: 1rem;">Administrative Controls</h3>
    <div style="display: flex; gap: 1rem; flex-wrap: wrap;">
        <a href="${pageContext.request.contextPath}/admin/users" class="btn btn-secondary">
            👥 User Management
        </a>
        <a href="${pageContext.request.contextPath}/admin/orders" class="btn btn-secondary">
            📦 Order Oversight
        </a>
        <a href="${pageContext.request.contextPath}/admin/listings" class="btn btn-secondary">
            🛡️ Product Moderation
        </a>
        <a href="${pageContext.request.contextPath}/api/v1/health" target="_blank" class="btn btn-secondary">
            🩺 System Health Check (API)
        </a>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
