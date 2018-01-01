<!DOCTYPE html>

<%@ page import="java.util.*" language="java" 
	contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="pix" tagdir="/WEB-INF/tags" %>

<%--sql to display list of completed tests for current user--%>
<c:catch var="thrown">
  <sql:query var="rs" dataSource="jdbc/LDB">
	SELECT test_id, id, given, correct, 
	DATE_FORMAT(date_taken, '%m/%d/%y') AS thedate,
	count(id) AS cnt,
	count(CASE WHEN given = correct THEN 1 ELSE NULL END) AS ok
	FROM tests 
	WHERE user='${friend.id}' 
	GROUP BY test_id
	ORDER BY date_taken DESC 
	LIMIT 20
  </sql:query>
</c:catch>

<c:if test="${thrown!=null}">
  <c:remove var="thrown"/>
  <jsp:forward page="/GoTest.do" />
</c:if>

<html>
<head>
  <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>test-03.jsp</title>
  <link rel="icon" 
      type="image/png" 
      href="img/titles/moeigo-icon.png" />
  <link href="bootstrap/css/bootstrap.css" rel="stylesheet">
  <link href="css/grid.css" rel="stylesheet">
  <link href="css/profile.css" rel="stylesheet">
</head>

<body>
  <%@ include file="include/Navbar.jspf" %>
  <div class="container-fluid">
    <div class="row">
      <div class="col-md-4">
        <div class="row">
          <div class="col-md-6">
            <pix:profileName profilename="${user.firstName}" 
                text="User:"
                update="true" />
          </div>
          <div class="col-md-6">
            <pix:profilePic 
                  profilepic="${user.profilePic}"
                  size="small" /> 
          </div>
        </div>
        <div class="row">
          <div class="col-xs-6" id="friendpic">
            <pix:profilePic profilepic="${friend.profilePic}" />
          </div>
          <div class="col-xs-6">
            <pix:profileName profilename="${friend.firstName}" text="View Profile:"/>
            <c:out value='${friend.id}' />
          </div>
        </div>
      </div>
      <div class="col-md-8">
        <div class="row">
          <div class="col-md-12">
		    <p>最近のニュース</p>
          </div>
        </div>
        <div class="row">
          <div class="col-md-12">
			<p>Write on ${friend.firstName}'s wall</p>
			　　<% request.setCharacterEncoding("utf-8"); %>
			  <input type="text" id="statustext" class="input-large" name="status" />
		  </div>
        </div>
        <div class="row">
          <div class="col-md-12">
			  <% request.setCharacterEncoding("utf-8"); %> 		
			  <input type="button" class="status-submit" 
			      id="status-submit" value="アップデート" />
          </div>
        </div>
      </div><!-- end col-md-8 -->
    </div><!-- end row -->
    
    <div class="row">
	<div class="panel-group" id="accordion">

      <div class="col-md-3" >
		  <div class="panel panel-default">
	    <div class="panel-heading">
	      <h4 class="panel-title">
	        <a data-toggle="collapse" data-parent="#accordion" href="#collapseFriends">
	          ${friend.firstName}のともだち
	        </a>
	      </h4>
	    </div>      
	    <div id="collapseFriends" class="panel-collapse collapse">
	      <div class="panel-body">
		  <c:forEach var="friend" items="${fof}">
	        <div class="row">
	          <div class="col-md-6">
			      <a href="FView.do?friendId=${friend.id}">
		            <pix:profilePic 
		                  profilepic="${friend.profilePic}"
		                  size="small" /> 
		          </a>
	          </div>
	          <div class="col-md-6">
	            <p><c:out value='${friend.firstName}' /></p>
	          </div>
	        </div>
          </c:forEach>
	      </div>
	    </div>
	   </div>
      </div><!-- end col-md-3 -->
      
	  <div class="col-md-6">
	  <div class="panel panel-default">
	    <div class="panel-heading">
	      <h4 class="panel-title">
	        <a data-toggle="collapse" data-parent="#accordion" href="#collapseStatus">
	          Status
	        </a>
	      </h4>
	    </div>      
	    <div id="collapseStatus" class="panel-collapse collapse">
	      <div class="panel-body">
		    <div id="doNews"></div>
		    <a href="javascript:void(0)" onclick="moreStatus()">get more</a>
	      </div>
	    </div>
	   </div>
      </div><!-- end col-md-6 -->
    
    <div class="col-md-3">
	  <div class="panel panel-default">
	    <div class="panel-heading">
	      <h4 class="panel-title">
	        <a data-toggle="collapse" data-parent="#accordion" href="#collapseTests">
	          Tests
	        </a>
	      </h4>
	    </div>      
	    <div id="collapseTests" class="panel-collapse collapse">
	      <div class="panel-body">
			<c:forEach var="row" items="${rs.rows}">
		    <div class="row">
		      <div class="col-md-8">
	       	    <a href="javascript:void(0)" 
					onclick="javascript: myFormSubmitter('${row.test_id}')"> 
	                ${row.thedate}</a>
	          </div>
		      <div class="col-md-4">score</div>        
		    </div>
		    </c:forEach>
	      </div>
	      </div>
	    </div>
	  </div>
	</div>
    </div>

    <footer>
      <p>&copy; moeigo.com 2014</p>
    </footer>
  </div><!-- .container -->

    <!-- Placed at the end of the document so the pages load faster -->
    <script type="text/javascript" src="scripts/jQuery-min-1.8.1.js"></script>
    <script src="bootstrap/js/bootstrap.min.js"></script>
    <script type="text/javascript" src="scripts/util.js"></script>
    <script type="text/javascript" src="scripts/showfriend.js"></script>

</body>
</html>