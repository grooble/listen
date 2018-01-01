<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head profile="http://www.w3.org/2005/10/profile">
	<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>もえいごリスニング</title>
    <link rel="icon" 
      type="image/png" 
      href="img/titles/moeigo-icon.png">
	<link href="bootstrap/css/bootstrap.css" rel="stylesheet">
    <link href="css/grid.css" rel="stylesheet">
    <link href="css/styles.css" rel="stylesheet">
    <link href="css/friender-r.css" rel="stylesheet">
	<script type="text/javascript" src="scripts/jQuery-min-1.8.1.js"></script>	
	<script type="text/javascript" src="scripts/findFriend.js"></script>
</head>
<body>
  <%@ include file="include/Navbar.jspf" %>
<div class="container-fluid">
  <div class="row profile-top">
    <div class="col-md-3">
      <div class="row">
        <div class="col-xs-12">
          <div class="col-xs-12 rounded">
          <div class="filler"></div>
            <% request.setCharacterEncoding("utf-8"); %> 		
            <jsp:include page="/includes/news.jsp" >
              <jsp:param name="newstext" 
	                   value="友達見つけよう。<br />友達のメールアドを入れて\'検索\'をクリックしよう。" />
            </jsp:include>
          </div>
        </div>
      </div><!-- end row -->
    </div><!-- end col-md-3 -->
    <div class="col-md-6">
      <div class="row">
        <div class="col-xs-12">
          <div class="col-xs-12 rounded">
            <div class="row">
              <div class="col-xs-12">
                <div class="filler"></div>
	            ${message}
              </div>
            </div>
            <div class="row">
              <div class="col-xs-12">
	          ${user.firstName}ようこそ、
              </div>
            </div>
            <div class="row">
              <div class="col-xs-12">
	                友だちを探しましょう<br />
	                友だちのメールアドレッスを入れてください<br />
              </div>
            </div>
            <div class="filler"></div>
            <div class="row">
              <div class="col-xs-12">
      	      <input type="text" size="30" id="findEmailText" name="emailText" />
	            <a href="#" id="find" onclick="javascript:IsEmpty();" >
			          検索</a>
              </div>
            </div>
            <div class="filler"></div>
            <div class="row found">
            </div>
            <div class="filler"></div>
            <div class="row log">
	          <ul class="loglist" id="list">
	          </ul>
            </div>
          </div><!-- end col-xs-12 rounded -->
        </div><!-- end col-xs-12 -->
      </div><!-- end row -->
    </div><!-- end col-md-6 -->
    <div class="col-md-3">
    </div><!-- end col-md-3 -->
  </div><!-- end row -->

  <footer>
    <p>&copy; moeigo.com 2014</p>
  </footer>
    
  </div><!-- container -->

</body>
</html>