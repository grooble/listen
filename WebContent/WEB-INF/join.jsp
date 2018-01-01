<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>

<head>
  <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <title>join.jsp</title>
  <link rel="icon" 
      type="image/png" 
      href="img/titles/moeigo-icon.png" />
  <link href="bootstrap/css/bootstrap.css" rel="stylesheet">
  <link href="css/grid.css" rel="stylesheet">
</head>

<body>

<%@ include file="include/Navbar.jspf" %>
  <div class="container-fluid">
	<form role="form" name="mailForm" method="POST" action="NewMember.to">
    <div class="form-group row">
	  <div class="col-sm-6 col-sm-offset-3">
	    <label for="email">あなたのメールアドレス</label>
	    <input type="text" class="form-control" id="email" name="new_email" placeholder="メールアドレス"/>
	    <label for="email">enter your mail address</label>
	  </div>
	</div>
	
	<div class="form-group row">
	  <div class="col-sm-6 col-sm-offset-3">
		<label for="pwd">パスワード</label>
		<input type="password" id="pwd" class="form-control" name="pwd1" placeholder="パスワード"/>
		<label for="pwd">password</label>
	  </div>
	</div>

	<div class="form-group row">
	  <div class="col-sm-6 col-sm-offset-3">
		<label for="pwd-check">パスワード確認</label>
		  <input type="password" id="pwd-check" class="form-control" name="pwd2" placeholder="おなじパスワード"/>
		<label for="pwd-check">password check</label>
	  </div>
	</div>

	<div class="form-group row">
	  <div class="col-sm-6 col-sm-offset-3">
        <button id="joinSubmit" type="submit" class="btn btn-default">テストへ行こう！</button>
	  </div>
    </div>

	</form>

  </div>
  
    <script type="text/javascript" src="scripts/jQuery-min-1.8.1.js"></script>
    <script src="bootstrap/js/bootstrap.min.js"></script>
    <script src="scripts/verify.js" ></script>
    <script src="scripts/util.js" ></script>  
</body>

</html>