<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
  <head>
    <title>もえいごリスニング</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1, user-scalable=no" 
          http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link rel="icon" 
      type="image/png" 
      href="img/titles/moeigo-icon.png">
    <link href="bootstrap/css/bootstrap.css" rel="stylesheet">
    <link href="css/grid.css" rel="stylesheet">
    <link href="css/styles.css" rel="stylesheet">
  </head>
  <body>
  <div class="container-fluid profile-top">
    <div class="col-xs-offset-2 col-xs-8">
      <div class="col-xs-12 rounded">
        <div class="row">
          <div class="col-xs-12">
	        <form method="POST" name="pwdEntryForm" id="pwdEntryForm" action="PwdEntry.to">
	          <div id="userMail">
		        <p>email: ${user.email}</p>
	          </div>
	          <div id="pwd1">
		        <p>パスワードを入れてください</p>
		        <input id="newpwd1" class="password" type="password" name="password1" />
	          </div>
	          <div id="pwd2">
		        <p>もういちどパスワードを入れてください</p>
		        <input id="newpwd2" class="password" type="password" name="password2" />
	          </div>
	          <div id="pwd2">
		        <input class="submitter" name="submitBtn" value="Submit" type="submit" />
	          </div>
            </form>
          </div><!-- col-xs-12 -->
        </div><!-- row -->
      </div><!-- col-xs-12 rounded -->
    </div><!-- col-xs-offset-2 col-xs-8 -->
  </div><!-- container-fluid profile-top -->
  </body>
</html>