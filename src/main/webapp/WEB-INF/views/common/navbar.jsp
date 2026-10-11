<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<header class="navbar">
    <div class="navbar-container">
        <a href="${pageContext.request.contextPath}/products" class="brand">
            🛒 <span>ReshmaMart</span>
        </a>
        <nav>
            <ul class="nav-links">
                <li><a href="${pageContext.request.contextPath}/products">Browse</a></li>
                <li>
                    <a href="javascript:void(0)" onclick="if(window.openReshmaChat)window.openReshmaChat();" style="display: inline-flex; align-items: center; gap: 0.35rem; font-weight: 600; color: var(--primary);">
                        🤖 AI Assistant
                    </a>
                </li>

                <c:choose>
                    <c:when test="${not empty sessionScope.currentUser}">
                        <%-- Buyer Links --%>
                        <c:if test="${sessionScope.currentUser.role == 'BUYER' || sessionScope.currentUser.role == 'ADMIN'}">
                            <li>
                                <a href="${pageContext.request.contextPath}/cart">
                                    Cart
                                </a>
                            </li>
                            <li><a href="${pageContext.request.contextPath}/orders">My Orders</a></li>
                        </c:if>

                        <%-- Seller Links --%>
                        <c:if test="${sessionScope.currentUser.role == 'SELLER' || sessionScope.currentUser.role == 'ADMIN'}">
                            <li><a href="${pageContext.request.contextPath}/seller/dashboard">Seller Portal</a></li>
                            <li><a href="${pageContext.request.contextPath}/seller/orders">Seller Orders</a></li>
                        </c:if>

                        <%-- Admin Links --%>
                        <c:if test="${sessionScope.currentUser.role == 'ADMIN'}">
                            <li><a href="${pageContext.request.contextPath}/admin/dashboard">Admin Panel</a></li>
                        </c:if>

                        <li style="margin-left: 0.5rem; color: var(--text-muted); font-size: 0.9rem;">
                            Hello, <strong><c:out value="${sessionScope.currentUser.name}"/></strong>
                            <span class="badge" style="background-color: #F5EBF4; color: var(--primary); margin-left: 0.25rem;">
                                <c:out value="${sessionScope.currentUser.role}"/>
                            </span>
                        </li>
                        <li><a href="${pageContext.request.contextPath}/logout" class="btn btn-secondary btn-sm">Logout</a></li>
                    </c:when>
                    <c:otherwise>
                        <li><a href="${pageContext.request.contextPath}/login" class="btn btn-secondary btn-sm">Login</a></li>
                        <li><a href="${pageContext.request.contextPath}/register" class="btn btn-primary btn-sm">Sign Up</a></li>
                    </c:otherwise>
                </c:choose>
            </ul>
        </nav>
    </div>
</header>
