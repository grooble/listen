<%@ tag %>
<%@ attribute name="answer" required="true" rtexprvalue="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

  <c:choose>
    <c:when test="${answer=='user'}">
	  <c:forEach var="qn" items="${theTest}" varStatus="a">
		<c:set var="thisSelect" value="${answers[a.index]}" />
		<c:set var="chosen" value="${qn[thisSelect]}" />
		<c:set var="path2" value="/img/${chosen.category}/80/"/>
		<c:set var="filenm2" value="${chosen.image}.gif" />
		<c:url value="${path2}${filenm2}" var="ansPath"/>
	  </c:forEach>
    </c:when>
    <c:otherwise>
	  <c:forEach var="qn" items="${theTest}" varStatus="a">
		<c:set var="thisCorrect" value="${correct[a.index]}" />
		<c:set var="choice" value="${qn[thisCorrect]}" />
		<c:set var="path1" value="/img/${choice.category}/80/"/>
		<c:set var="filenm1" value="${choice.image}.gif" />
		<c:url value="${path1}${filenm1}" var="corrPath"/>
	  </c:forEach>
    </c:otherwise>
  </c:choose>