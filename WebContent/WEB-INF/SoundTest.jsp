<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="pix" tagdir="/WEB-INF/tags" %>
<!DOCTYPE html>
<html>
<head>
  <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

  <title>test-03.jsp</title>
  <link rel="icon" 
      type="image/png" 
      href="img/titles/moeigo-icon.png" />
  <link href="bootstrap/css/bootstrap.css" rel="stylesheet">
  <link href="css/grid.css" rel="stylesheet">
  <link href="css/profile.css" rel="stylesheet">
  <script type="text/javascript" src="scripts/jQuery-min-1.8.1.js"></script>	
  <script src="bootstrap/js/bootstrap.min.js"></script>
  <script type="text/javascript" src="scripts/util.js"></script>
  <script type="text/javascript" src="scripts/soundtest.js"></script>
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
                update="false" />
          </div>
          <div class="col-md-6">
            <pix:profilePic 
                  profilepic="${user.profilePic}"
                  size="small" /> 
          </div>
        </div><!-- end row -->
      </div><!-- end col-md-4 -->
      <div class="col-md-8">
        <div class="row h4 text-center">
        </div>
      </div><!-- end col-md-8 -->
    </div><!-- end row -->

    
    <div class="row">
      <div id="sndButtonDiv" 
           class="col-md-6 col-md-offset-3 col-xs-12 btn btn-xlarge btn-primary">
	    Select
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
    
      <div id="sound">
      </div>
      <div id="test" class="row">
	  </div>

  <footer>
      <p>&copy; moeigo.com 2014</p>
  </footer>
  </div><!-- .container -->

</body>
</html>