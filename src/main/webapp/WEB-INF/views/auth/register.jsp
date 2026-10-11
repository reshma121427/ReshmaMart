<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="pageTitle" value="Create Account" />
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<div style="max-width: 480px; margin: 2rem auto; background: white; padding: 2rem; border-radius: var(--radius); box-shadow: var(--shadow); border: 1px solid var(--border-color);">
    <h2 style="margin-bottom: 1.5rem; text-align: center;">Join ReshmaMart</h2>

    <form action="${pageContext.request.contextPath}/auth/register" method="POST">
        <div class="form-group">
            <label class="form-label" for="name">Full Name</label>
            <input type="text" id="name" name="name" class="form-control"
                   value="<c:out value='${name}'/>" required autofocus>
            <c:if test="${not empty fieldErrors.name}">
                <div class="field-error"><c:out value="${fieldErrors.name}"/></div>
            </c:if>
        </div>

        <div class="form-group">
            <label class="form-label" for="email">Email Address</label>
            <input type="email" id="email" name="email" class="form-control"
                   value="<c:out value='${email}'/>" required>
            <c:if test="${not empty fieldErrors.email}">
                <div class="field-error"><c:out value="${fieldErrors.email}"/></div>
            </c:if>
        </div>

        <div class="form-group">
            <label class="form-label" for="password">Password (min. 6 characters)</label>
            <input type="password" id="password" name="password" class="form-control" required minlength="6">
            <c:if test="${not empty fieldErrors.password}">
                <div class="field-error"><c:out value="${fieldErrors.password}"/></div>
            </c:if>
        </div>

        <div class="form-group">
            <label class="form-label" for="confirmPassword">Confirm Password</label>
            <input type="password" id="confirmPassword" name="confirmPassword" class="form-control" required minlength="6">
            <c:if test="${not empty fieldErrors.confirmPassword}">
                <div class="field-error"><c:out value="${fieldErrors.confirmPassword}"/></div>
            </c:if>
        </div>

        <div class="form-group">
            <label class="form-label" for="role">Account Type</label>
            <select id="role" name="role" class="form-control" required>
                <option value="BUYER" ${role == 'BUYER' ? 'selected' : ''}>Buyer (Shop & Purchase)</option>
                <option value="SELLER" ${role == 'SELLER' ? 'selected' : ''}>Seller (List & Sell Products)</option>
            </select>
            <c:if test="${not empty fieldErrors.role}">
                <div class="field-error"><c:out value="${fieldErrors.role}"/></div>
            </c:if>
        </div>

        <button type="submit" class="btn btn-primary" style="width: 100%; margin-top: 1rem;">Register</button>
    </form>

    <div style="margin-top: 1.5rem; text-align: center; font-size: 0.9rem; color: var(--text-muted);">
        Already have an account? <a href="${pageContext.request.contextPath}/login" style="color: var(--primary); font-weight: 600;">Sign In</a>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
