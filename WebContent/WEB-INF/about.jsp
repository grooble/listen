<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <title>about.jsp</title>
  <link rel="icon" 
      type="image/png" 
      href="img/titles/moeigo-icon.png" />
  <link href="bootstrap/css/bootstrap.css" rel="stylesheet">
  <link href="css/grid.css" rel="stylesheet">
  <link href="css/styles.css" rel="stylesheet">
  <link href="css/about.css" rel="stylesheet">
</head>
<body>
<%@ include file="include/Navbar.jspf" %>
  <div class="container-fluid profile-top">
  <div class="col-xs-offset-2 col-xs-8 ">
  <div class="row">
  <div class="col-xs-12">
  <div class="col-xs-12 rounded">
	<form role="form" name="mailForm" method="POST" action="SendMail.do">
	<div class="filler"></div>
    <div class="form-group row">
	  <div class="col-sm-6 col-sm-offset-3">
	    <label>あなたのメールアドレス</label>
	    <input type="text" class="form-control" id="email" name="email" placeholder="メールアドレス"/>
	    <label for="email">enter your mail address</label>
	  </div>
	</div>
	
	<div class="filler"></div>
	<div class="form-group row">
	  <div class="col-sm-6 col-sm-offset-3">
		<label for="subject">件名</label>
		<input type="text" id="subject" class="form-control" name="subject" placeholder="件名"/>
		<label for="subject">subject</label>
	  </div>
	</div>
	
	<div class="filler"></div>
	<div class="form-group row">
	  <div class="col-sm-6 col-sm-offset-3">
		<label for="message">本文</label>
		<textarea id="message" class="form-control" name="message" placeholder="本文"></textarea>
		<label for="message">message</label>
	  </div>
	</div>

	<div class="filler"></div>
	<div class="form-group row">
	  <div class="col-sm-6 col-sm-offset-3">
        <button type="submit" class="btn btn-default">Submit</button>
	  </div>
    </div>
	</form>
	<div class="filler"></div>

  </div><!-- col-xs-12 rounded -->
  </div><!-- col-xs-12 -->
  </div><!-- row -->
  </div><!-- col-xs-12 -->
  </div><!-- container-fluid -->
  
    <script type="text/javascript" src="scripts/jQuery-min-1.8.1.js"></script>
    <script src="bootstrap/js/bootstrap.min.js"></script>
  
</body>
</html>