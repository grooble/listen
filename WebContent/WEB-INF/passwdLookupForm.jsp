<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<html>
  <head>
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
	<title>passwdLookupForm.jsp</title>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link rel="icon" 
      type="image/png" 
      href="img/titles/moeigo-icon.png" />
    <link href="bootstrap/css/bootstrap.css" rel="stylesheet">
    <link href="css/grid.css" rel="stylesheet">
    <link href="css/styles.css" rel="stylesheet">
  </head>
  <body>
<%@ include file="include/Navbar.jspf" %>
  <div class="container-fluid profile-top">
    <div class="col-xs-offset-2 col-xs-8">
      <div class="col-xs-12 rounded">
        <div class="row">
          <div class="col-xs-12">
	        <form role="form" method="POST" name="pwdLookupForm" id="pwdLookupForm" action="pwdLookup.to">

              <div class="filler"></div>
              <div class="row">
                <div class="col-xs-12 col-md-6 col-md-offset-3">
                  <p>パスワード忘れた方、メールを入力して、メールまでログイン方法を送ります。</p>
                </div>
              </div>

              <div class="filler"></div>
              <div class="form-group row">
	            <div class="col-sm-6 col-sm-offset-3">
	              <label>あなたのメールアドレス</label>
	              <input type="text" class="form-control" id="usermail" name="usermail" placeholder="メールアドレス"/>
	              <label for="usermail">enter your mail address</label>
	            </div>
	          </div>	

              <div class="filler"></div>
	          <div class="form-group row">
	            <div class="col-sm-6 col-sm-offset-3">
                  <button type="submit" class="btn btn-default" name="submitBtn">Submit</button>
	            </div>
              </div>
              <div class="filler"></div>

            </form>
          </div>
        </div>
      </div>
    </div>
  </div>
  </body>
</html>