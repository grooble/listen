<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>

<html>
<head>
  <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <title>test-03.jsp</title>
  <link rel="icon" 
      type="image/png" 
      href="img/titles/moeigo-icon.png" />
  <link href="bootstrap/css/bootstrap.css" rel="stylesheet">
  <link href="css/grid.css" rel="stylesheet">
</head>
<body>
<%@ include file="include/Navbar.jspf" %>
  <div class="container-fluid">
	<form role="form" name="nameForm" method="POST" action="Namer.do">
    <div class="form-group row">
	  <div class="col-sm-6 col-sm-offset-3">
	    <label>Last name</label>
	    <input type="text" class="form-control" name="lname" placeholder="last name"/>
	    <label for="email">名字</label>
	  </div>
	</div>
	
	<div class="form-group row">
	  <div class="col-sm-6 col-sm-offset-3">
		<label for="subject">First name</label>
		<input type="text" id="subject" class="form-control" name="fname" placeholder="first name"/>
		<label for="subject">名前</label>
	  </div>
	</div>
	
	<div class="form-group row">
	  <div class="col-sm-6 col-sm-offset-3">
		<label for="message">生年月日</label>
					<input type="text" name="year" size="4" style="width:40px;"/>年
					<select name="month" size="1">
					  <option value="1">１月
					  <option value="2">２月
					  <option value="3">３月
					  <option value="4">４月
					  <option value="5">５月
					  <option value="6">６月
					  <option value="7">７月
					  <option value="8">８月
					  <option value="9">９月
					  <option value="10">１０月
					  <option value="11">１１月
					  <option value="12">１２月
					</select>
				    <select name="day" size="1">
				      <option value="1">1
					  <option value="2">2
					  <option value="3">3
					  <option value="4">4
					  <option value="5">5
					  <option value="6">6
					  <option value="7">7
					  <option value="8">8
					  <option value="9">9
					  <option value="10">10
					  <option value="11">11
					  <option value="12">12
					  <option value="13">13
					  <option value="14">14
					  <option value="15">15
					  <option value="16">16
					  <option value="17">17
					  <option value="18">18
					  <option value="19">19
					  <option value="20">20
					  <option value="21">21
					  <option value="22">22
					  <option value="23">23
					  <option value="24">24
					  <option value="25">25
					  <option value="26">26
					  <option value="27">27
					  <option value="28">28
					  <option value="29">29
					  <option value="30">30
					  <option value="31">31
					</select><p>
					
				<label for="message">Date of birth</label>
	  </div>
	</div>
	<div class="form-group row">
	  <div class="col-sm-6 col-sm-offset-3">
        <button type="submit" class="btn btn-default">Submit</button>
	  </div>
    </div>
	</form>

  </div>
</body>
</html>