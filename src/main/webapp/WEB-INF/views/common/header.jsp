<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><c:out value="${pageTitle != null ? pageTitle : 'ReshmaMart - Modern E-Commerce'}"/> | ReshmaMart</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/style.css?v=3.0.0">
</head>
<body>
<jsp:include page="/WEB-INF/views/common/navbar.jsp" />
<div class="container">
    <jsp:include page="/WEB-INF/views/common/alerts.jsp" />
