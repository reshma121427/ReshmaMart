<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

</div> <%-- End container --%>

<jsp:include page="/WEB-INF/views/common/chat-widget.jsp" />

<footer>
    <p>&copy; 2026 ReshmaMart Capstone E-Commerce Platform. Built for Anna University R2025.</p>
    <p style="font-size: 0.8rem; margin-top: 0.25rem;">Java Servlets · JDBC · Apache Tomcat 9.0.x · H2 Database</p>
</footer>

<script src="${pageContext.request.contextPath}/static/js/main.js?v=3.0.0"></script>
</body>
</html>
