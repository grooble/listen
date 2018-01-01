<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ attribute name="profilename" required="true" rtexprvalue="true" %>
<%@ attribute name="text" required="false" rtexprvalue="false" %>
<%@ attribute name="update" required="false" rtexprvalue="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:choose>
  <c:when test="${profilename == null}">
    <a href="ChangeName.do">名前を入れましょう</a>
  </c:when>
  <c:otherwise>
    <p>${text} ${profilename}</p>
    <c:if test="${update=='true'}">
        <a href="ChangeName.do" class="smaller"> 変更</a>
    </c:if>
  </c:otherwise>
</c:choose>
