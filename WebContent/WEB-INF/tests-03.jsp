<!DOCTYPE html>

<%@ page import="java.util.*" language="java" 
	contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="pix" tagdir="/WEB-INF/tags" %>

<%--sql to display list of completed tests for current user--%>
<c:catch var="thrown">
  <sql:query var="rs" dataSource="jdbc/LDB">
	SELECT test_id, id, given, correct, 
	DATE_FORMAT(date_taken, '%m/%d/%y') AS thedate,
	count(id) AS cnt,
	count(CASE WHEN given = correct THEN 1 ELSE NULL END) AS ok
	FROM tests 
	WHERE user='${user.id}' 
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
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" 
        name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
  <title>test-03.jsp</title>
  <link rel="icon" 
      type="image/png" 
      href="img/titles/moeigo-icon.png" />
  <link href="bootstrap/css/bootstrap.css" rel="stylesheet">
  <link href="css/grid.css" rel="stylesheet">
  <link href="css/styles.css" rel="stylesheet">
  <link href="css/profile.css" rel="stylesheet">
</head>

<body>
  <%@ include file="include/Navbar.jspf" %>
  <div class="container-fluid">
    <div class="row profile-top">
      <div class="col-md-4">
        <div class="row">
          <div class="col-xs-12">
            <div class="col-xs-12 text-center rounded">
              <div class="filler"></div>
              <div class="col-xs-4">
                <pix:profileName profilename="${user.firstName}" 
                    update="true" />
              </div>
              <div class="col-xs-8">
                <div class="row">
                  <pix:profilePic profilepic="${user.profilePic}" />
                </div>
                <div class="filler"></div>
                <div class="row">
                  <button class="button" onclick="$('#pic_chooser').toggle();">Change Pic</button>
                </div>
              </div>
              <div style="clear: both;">
              </div> <div class="filler"></div>
            </div>
          </div>
        </div>
        <div id="pic_chooser" class="hid">
          <%@ include file="include/picUpload.jspf" %>
        </div>
      </div>


      <div class="col-md-8">
      <%@ include file="include/message.jspf" %>
        <div class="row">
          <div class="col-xs-12">
            <div class="col-xs-12 text-center rounded">
              <div class="filler"></div>
                <div class="row">
                  <p>最近のニュース</p>
                </div>
                <div class="row">
                  <div class="col-xs-12">
                    <% request.setCharacterEncoding("utf-8"); %>
			        <input type="text" id="statustext" 
			            class="input-large form-control my-input" name="status" />
                  </div>
                </div>
                <div class="filler"></div>
                <div class="row">
                  <div class="col-xs-12">
			        <% request.setCharacterEncoding("utf-8"); %> 		
			        <input type="button" class="status-submit" 
			            id="status-submit" value="アップデート" />
                  </div>
                </div>
              <div style="clear: both;">
              </div> <div class="filler"></div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="filler"></div>        
    
    <div class="row" id="status-top">
    
	<div class="panel-group" id="accordion">
      <div class="col-md-3" >
		<div class="panel panel-default">
	    <div class="panel-heading">
	      <div class="panel-title h4 glyphicon glyphicon-heart">
	        <a data-toggle="collapse" data-parent="#accordion" href="#collapseFriends">
	          友だち
	        </a>
	      </div>
	    </div>      
	    <div id="collapseFriends" class="panel-collapse collapse">
	      <div class="panel-body">
	      <a href="findByEmail.do">
	        友だちさがしましょう
	      </a>
	      <ul>
		  <c:forEach var="friend" items="${friends}">
		    <li class="row">
	          <div class="col-md-5">
			      <a href="FView.do?friendId=${friend.id}">
		            <pix:profilePic 
		                  profilepic="${friend.profilePic}"
		                  size="small" /> 
		          </a>
	          </div>
	          <div class="col-md-7">
	            <p><c:out value='${friend.firstName}' /></p>
				<a class="smaller" href="UnFriender.do?exfriend=${friend.id}"
						onclick="return confirm('本当に削除しますか？')">削除</a>
	          </div>
	        </li>
          </c:forEach>
	      </ul>
	      </div>
	    </div>
	   </div>

      
	  <div class="panel panel-default">
	    <div class="panel-heading">
	      <div class="h4 panel-title">
	        <a data-toggle="collapse" data-parent="#accordion" href="#collapsePended">
	          承認待ち
	        </a>
	      </div>
	    </div>      
	    <div id="collapsePended" class="panel-collapse collapse">
	      <div class="panel-body">
	      <c:choose>
		  <c:when test="${pending==null}">
	        <p>NONE PENDED</p>
		    <p>友だちのリクエストがありません</p>
		  </c:when>
		  <c:otherwise>
		  <ul>
		  <c:forEach var="pended" items="${pending}">
		    <li class="row">
	          <div class="col-md-4">
			      <a href="FView.do?friendId=${pended.id}">
		            <pix:profilePic 
		                  profilepic="${pended.profilePic}"
		                  size="small" /> 
		          </a>
	          </div>
	          <div class="col-md-8">
	            <p><c:out value='${pended.firstName}' /></p>
				  <a href="Pender.do?action=accept&friend=${pended.email}">友達になる</a><br />
				  <a href="Pender.do?action=deny&friend=${pended.email}" 
				  		onclick="return confirm('本当に削除しますか？')" >友達にならない</a><br />
	          </div>
		    </li>
	      </c:forEach>
		  </ul>
		  </c:otherwise>
	      </c:choose>
	      </div>
	    </div>
	   </div>

      </div><!-- end col-md-3 -->

      
	  <div class="col-md-6">
	  <div class="panel panel-default">
	    <div class="panel-heading">
	      <div class="h4 panel-title text-center glyphicon glyphicon-comment">
	        <a data-toggle="collapse" data-parent="#accordion" href="#collapseStatus">
	          ステータス
	        </a>
	      </div>
	    </div>      
	    <div id="collapseStatus" class="panel-collapse collapse">
	      <div class="panel-body">
		    <div id="doNews"></div>
		    <a href="javascript:void(0)" onclick="moreStatus()">get more</a>
	      </div>
	    </div>
	   </div>
      </div><!-- end col-md-6 -->
    
    
    <div class="col-md-3" id="test-top">
	  <div class="panel panel-default">
	    <div class="panel-heading">
	      <div class="h4 panel-title glyphicon glyphicon-tasks">
	        <a data-toggle="collapse" data-parent="#accordion" href="#collapseTests" class="" >
	          完成テスト
	        </a>
	      </div>
	    </div>      
	    <div id="collapseTests" class="panel-collapse collapse">
	      <div class="panel-body">
			<c:forEach var="row" items="${rs.rows}" varStatus="loopStatus">
			<div class="${loopStatus.index % 2 == 0 ? 'row even' : 'row odd'}">
		      <div class="col-md-8">
	       	    <a href="javascript:void(0)" 
					onclick="javascript: myFormSubmitter('${row.test_id}')"> 
	                ${row.thedate}</a>
	          </div>
		      <div class="col-md-4">
		        <fmt:formatNumber 
		            value="${row.ok/row.cnt*100}" 
                    maxFractionDigits="1"/>%
		      </div>        
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
    <script type="text/javascript" src="scripts/testviewer-02.js"></script>

</body>
</html>