<!DOCTYPE html>

<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="pix" tagdir="/WEB-INF/tags" %>

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
  <link rel="stylesheet" type="text/css" href="css/loader.css" />
  <link href="bootstrap/css/bootstrap.css" rel="stylesheet">
  <link href="css/grid.css" rel="stylesheet">
  <link href="css/styles.css" rel="stylesheet">
  <link href="css/testpage.css" rel="stylesheet">
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
                    profilepic="${user.profilePic}" 
                    size="small" /> 
              </div>
              <div style="clear: both;"></div>
              <div class="filler"></div>       
            </div>
          </div>
        </div>  
      </div><!-- end col-md-4 -->
      <div class="col-md-8">
        <div class="row h3 text-center">
		    <p>テスト中</p>
		    <c:if test="${sessionScope.friendPercent!=null}">
              <p>友だちチャレンジ！</p>
            </c:if>
        </div>
      </div><!-- end col-md-8 -->
    </div><!-- end row profile-top -->
  
	<form method="POST" action="Manage.do" id="testPageForm">
    
    <div id="sndButtonDiv" class="row">
      <div class="col-xs-12">
      <div class="col-md-6 col-md-offset-3 col-xs-12 btn btn-xlarge btn-primary " >
	    Play Sound
	  </div>
      </div>
    </div>
   	<div id="spinner" class="loading bar">
	  <div></div>
	  <div></div>
	  <div></div>
	  <div></div>
	  <div></div>
	  <div></div>
	  <div></div>
	  <div></div>
	</div>
	
    <div class="test-top row">
      <div class="col-xs-offset-2 col-xs-8" id="test" >
      </div>
    </div>
    
	<div id="correct-sounds"></div>
	
    <input id="answeredValue" type="hidden" name="answered" value="">
    <input id="timer" type="hidden" name="timestamp" value="">
	
	</form>

  <footer>
      <p>&copy; moeigo.com 2014</p>
  </footer>
  </div><!-- .container -->

    <!-- At end of document to enable faster pageloading --> 
	<script type="text/javascript" src="scripts/jQuery-min-1.8.1.js"></script>	
    <script src="bootstrap/js/bootstrap.min.js"></script>
	<script type="text/javascript" src="scripts/soundmanager2-nodebug-jsmin.js"></script>
	<script type="text/javascript" src="scripts/util.js"></script>
	<script type="text/javascript" src="scripts/json2.js"></script>
	<script type="text/javascript" src="scripts/clicker5-3.js"></script>

</body>

</html>