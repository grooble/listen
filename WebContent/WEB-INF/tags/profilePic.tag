<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ attribute name="profilepic" required="true" rtexprvalue="true" %>
<%@ attribute name="size" required="false" rtexprvalue="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

  <c:choose>
	<%--include the image selection jsp fragment--%>
	<%--for users without a current picture--%>
	<c:when test="${profilepic == null}">
	  <img name="smiley" src="img/smiley_face.gif" />
	</c:when>
    
    <c:when test="${size == 'small'}">
	  <c:set var="pathparts" 
		  value="${fn:split(profilepic, '/')}" />
	  <c:set var="filename" 
		  value="${pathparts[fn:length(pathparts) - 1]}" />
	  <c:set var="pathid"
	      value="${pathparts[1]}" />
	  <c:set var="profpic" 
		  value="profile/${pathid}/images/thumb/${filename}"
		  scope="session" />
	  <c:url value="${profpic}" var="myPicPath"/>
	  <img name="profilepic" src="<c:out value='${profpic}' />" />
    </c:when>
	
	<%--display currently selected picture--%>
	<c:otherwise>
      <c:set var="profpic" 
			value="${profilepic}" 
			scope="session" />
	  <c:url value="${profpic}" var="myPicPath"/>
	  <img name="profilepic" src="<c:out value='${profpic}' />" />
	</c:otherwise>
  </c:choose>
