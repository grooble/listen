<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<!DOCTYPE html>

<%--sql display list of categories--%>
<c:catch var="thrown">
<sql:query var="rs" dataSource="jdbc/LDB">
select category 
from questions2 
group by category
having count(category)>12
</sql:query>
</c:catch>
<c:if test="${thrown!=null}">
<c:remove var="thrown"/>
<jsp:forward page="GoTest.do" />
</c:if>

<html>
<head profile="http://www.w3.org/2005/10/profile">
	<title>studyselect.jsp</title>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link rel="icon" 
      type="image/png" 
      href="img/titles/moeigo-icon.png">
	<link rel="stylesheet" type="text/css" href="styles.css" />
	<link rel="stylesheet" type="text/css" href="3-col-even.css" />
	<link rel="stylesheet" type="text/css" href="css/studySelectCSS.css" />
	<script type="text/javascript" src="scripts/util.js"></script>
	<script type="text/javascript" src="scripts/studychoose.js"></script>

</head>
<body>

  <%@ include file="include/Navbar.jspf" %>

<div class="wrapper threecol">
	<div class="colmid">		
		<div class="colleft">
		
			<div class="col1">
				<form name="selector" method="POST" action="GetTest.do">
					<div id="selectTestLink">
						<c:forEach var="row" items="${rs.rows}">
							<a id="${row.category}" title="${row.category}" 
							href="javascript:submitForm('${row.category}')" >
							${row.category}</a><br />	
						</c:forEach>
					</div>
					<!-- 
					<input type="checkbox" name="flashCardCheck" value="flashcard" />
					Flash Cards<br />
					 -->
					<input type="hidden" id="selectCat" name="category" value="default"/>
					<input type="hidden" id="selectDiff" name="difficulty" value="any"/>
				</form>
			</div>		
			
			<div class="col2">
				<% request.setCharacterEncoding("utf-8"); %> 		
				<div id="selectNews">
				<jsp:include page="/includes/news.jsp" >
					<jsp:param name="newstext" value="リストから科目選ぼう（マウスポインターを科目の上にしたら、ヒントがでる）。" />
				</jsp:include>
				</div>
				<div id="titleimage">
					<img src="img/titles/index-default.jpg" title="default" width="180" >
				</div>
			</div>
			
			<div class="col3">
			  	<div id="selectAbout"><a href="about.jsp">advertise</a></div>
			</div>
		</div>
	</div>
</div>



</body>
</html>