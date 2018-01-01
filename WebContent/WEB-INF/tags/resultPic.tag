<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:forEach var="qn" items="${theTest}" varStatus="a">

	<c:set var="thisSelect" value="${answers[a.index]}" />
	<c:set var="chosen" value="${qn[thisSelect]}" />
	<c:set var="path2" value="/img/${chosen.category}/80/"/>
	<c:set var="filenm2" value="${chosen.image}.gif" />
	<c:url value="${path2}${filenm2}" var="ansPath"/>

	<c:set var="thisCorrect" value="${correct[a.index]}" />
	<c:set var="choice" value="${qn[thisCorrect]}" />
	<c:set var="path1" value="/img/${choice.category}/80/"/>
	<c:set var="filenm1" value="${choice.image}.gif" />
	<c:url value="${path1}${filenm1}" var="corrPath"/>
  
      <div class="row">
        <div class="col-md-3 col-xs-6 col-md-offset-3">
          <img name="useranswerpic" src="<c:out value='${ansPath}' />" />
        </div>
        <div class="col-md-3 col-xs-6">
          <img name="corranswerpic" src="<c:out value='${corrPath}' />" />		
        </div>
      </div>
  
</c:forEach>