<%@ page import="java.util.*" language="java" errorPage="error/testnotfound.jsp" 
	contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@ taglib prefix="pix" tagdir="/WEB-INF/tags" %>

<!DOCTYPE html>

<%--sql display list of categories--%>
<c:catch var="thrown">
<sql:query var="rs" dataSource="jdbc/LDB">
select category 
from questions 
group by category
having count(category)>12
</sql:query>
</c:catch>
<c:if test="${thrown!=null}">
<c:remove var="thrown"/>
<jsp:forward page="Setup.do" />
</c:if>

<%--	============================================	--%>
<%--													--%>
<%--	このJSPはManager、そうしてNewTestサーブレトを呼んで、		--%>
<%--	Ajaxコールをやっている。								--%>
<%--													--%>
<%--	*Member　DIVに、ユーザーログインとそのステータス。			--%>
<%--													--%>
<%--	*chooser DIVは最初にテストのレベルを選ぶ。				--%>
<%--													--%>
<%--	*sound DIVとsoundBtnはそれぞれの問題の正しい音と		--%>
<%--	正解と不正解の音を設定して、再生する。					--%>
<%--													--%>
<%--	*images DIVはAjaxコールバック関数に使われている。		--%>
<%--													--%>
<%--	clicker.jsはこのフォームの							--%>
<%--	onclick,Ajaxなどを設定する。						--%>
<%--													--%>
<%--	=============================================	--%>

<html>
<head>
  <title>testmenu-r.jsp</title>
  <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <link rel="icon" 
        type="image/png" 
        href="img/titles/moeigo-icon.png" />
  <link href="bootstrap/css/bootstrap.css" rel="stylesheet">
  <link href="css/grid.css" rel="stylesheet">
  <link href="css/styles.css" rel="stylesheet">
  <link href="css/chooser.css" rel="stylesheet">
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
                    text="Hi "
                    update="false" />
              </div>
              <div class="col-xs-8">
                <pix:profilePic 
                    profilepic="${user.profilePic}" /> 
              </div>
              <div style="clear: both;"></div>
              <div class="filler"></div>
              
            </div>
          </div>
        </div>  
      </div><!-- end col-md-4 -->
      <div class="col-md-8">
        <div class="row h3 text-center">
		    <p>テストを選びましょう</p>
        </div>
      </div><!-- end col-md-8 -->
    </div><!-- end row -->
    
    <div class="filler"></div>
    
    <div id="test-list" class="row">
    <div class="panel panel-default col-xs-offset-1 col-xs-3">

      <div class="panel-heading">
        <h3 class="panel-title">難易度</h3>
      </div>
      <div class="panel-body">
        <div class="col-xs-12 text-center rounded">
          <div class="row">
            <div class="col-xs-12">
		  <ul>
		    <li>
		      <a href="GetTest.do?category=random&difficulty=easy">
		          <img src="img/easy.gif" alt="easy" title="easy" />
		      </a>
		    </li>
		    <li>
		      <a href="GetTest.do?category=random&difficulty=medium">
		          <img src="img/medium.gif" alt="medium" title="medium" />
		      </a>
		    </li>
		    <li>
		      <a href="GetTest.do?category=random&difficulty=easy">
		          <img src="img/hard.gif" alt="hard" title="hard" />
		      </a>
		    </li>
		  </ul>
			</div><!-- col-xs-12 -->
		  </div><!-- row -->
		</div><!-- col-xs-12 text-center rounded -->
      </div>
    </div><!-- panel panel-default col-xs-5 -->
    
    <div class="col-xs-1"></div>
    
    <div class="panel panel-default col-xs-6">
      <div class="panel-heading">
        <h3 class="panel-title">キャテゴリ</h3>
      </div>
      <div class="panel-body">
        <div class="col-xs-12 text-center rounded">
          <div class="row">
            <div class="col-sm-6">
              <ul id="categoryList">
   		        <c:forEach var="row" items="${rs.rows}">
                <li id="selectTestLink">
                  <a id="${row.category}" title="${row.category}" 
	                  href="GetTest.do?category=<c:out value="${row.category}" />&difficulty=any" >
	                  ${row.category}</a><br />	
                </li>
                </c:forEach>
              </ul>
            </div><!-- col-sm-6 -->
            <div id="titleimage" class="col-sm-6">
              <img src="img/titles/index-default.jpg" title="default">
            </div>
          </div><!-- row -->
        </div><!-- col-xs-12 text-center rounded -->
      </div>
    </div>
    </div><!-- test-list -->

  <footer>
      <p>&copy; moeigo.com 2014</p>
  </footer>
  </div><!-- .container -->

    <!-- Placed at the end of the document so the pages load faster -->
    <script type="text/javascript" src="scripts/jQuery-min-1.8.1.js"></script>
    <script src="bootstrap/js/bootstrap.min.js"></script>
    <script type="text/javascript" src="scripts/util.js"></script>
    <script type="text/javascript" src="scripts/test-chooser-R.js"></script>

</body>
</html>