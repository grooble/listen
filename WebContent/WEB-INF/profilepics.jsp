<!DOCTYPE html>
    
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head profile="http://www.w3.org/2005/10/profile">
  <title>profilepics.jsp</title>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  <link rel="icon" 
    type="image/png" 
    href="img/titles/moeigo-icon.png" />
  <link rel="stylesheet" href="styles.css" type="text/css" />
  <script type="text/javascript" src="scripts/jQuery-min-1.8.1.js"></script>
  <script type="text/javascript" src="scripts/profpic.js"></script>  
</head>

<body>

  <%@ include file="include/Navbar.jspf" %>
  <form method="post" action="HandleProfilePic.do">

  <p>user id: ${user.id}</p>
  
  <div class="message">
    ${requestScope.message}
  </div>
  
  <c:forEach var="profPic" items="${profilepics}">
  <div class="picContainer">
    <img src="profile/${user.id}/images/${profPic}" />
    <input class="cbox" type="checkbox" name="cb" value="${profPic}"/>
  </div>
  </c:forEach>
    <input class="submitter" type="submit" name="submitme" value="delete"/>
  </form>
  

</body>
</html>