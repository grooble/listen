<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
  <head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>Insert title here</title>
	<link rel="stylesheet" type="text/css" href="styles.css" />
	<link rel="stylesheet" type="text/css" href="css/errorPages.css" />
  </head>
  <body>
	<div class="banner">
	  <span id="moeigo">moeigo</span>
	  <span id="com">.com</span>
	</div>

	<div class="error general">
	  何かおかしいな。。。
	</div>
	<c:choose>
	  <c:when test="${user!=null}">
		<div class="error">
		  <a class="error" href="ShowProfile.do">OK</a>
		</div>
	  </c:when>
	  <c:otherwise>
		<div class="error">
		  <a class="error" href="index.jsp">OK</a>
		</div>
	  </c:otherwise>
	</c:choose>

  </body>
</html>