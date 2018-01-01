<!DOCTYPE html>

<%@ page import="java.util.*" language="java" 
	contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="pix" tagdir="/WEB-INF/tags" %>

<html>
<head>
  <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>result2.jsp</title>
  <link rel="icon" 
      type="image/png" 
      href="img/titles/moeigo-icon.png" />
  <link href="bootstrap/css/bootstrap.css" rel="stylesheet">
  <link href="css/grid.css" rel="stylesheet">
  <link href="css/styles.css" rel="stylesheet">
  <link href="css/results.css" rel="stylesheet">
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
      
      <div class="filler"></div>

      <div class="col-md-8">
        <div class="col-xs-12">
          <div class="col-xs-12 text-center rounded">
            <div class="row h3 text-center">
              <div class="filler"></div>
                <div class="col-xs-12 col-md-8 col-md-offset-2">
                  <div class="col-xs-6 text-center h4">
                    <p>結果: ${result}%</p>
                  </div>
                  <div class="col-xs-6 text-center h4">
                    <p>${requestScope.header}</p>
                  </div>
                </div>
              </div>  
              <div class="row text-center">
                <div class="col-md-6">
                  <div class="row">
                    <div class="col-xs-12">
          	          <a class="h4 btn btn-xlarge btn-primary glyphicon glyphicon-refresh" 
                          href="<c:out value='${queryString}'/>"> もう一度！
                      </a>
                    </div>
                  </div>
                  <div class="row">
                    <div class="col-xs-12 smaller">
                      <p>おなじテストをもういちど</p>
                    </div>
                  </div>
                </div>
                <div class="col-md-6">
                  <div class="row">
                    <div class="col-xs-12">
	                  <a class="h4 btn btn-xlarge btn-primary glyphicon glyphicon-check" 
		                  href="ReviewTest.do?review=newReview">　練習しましょう！
	                  </a>
                    </div>
                  </div>
                  <div class="row">
                    <div class="col-xs-12 smaller">
                      <p>クリックして、今やったテストの単語を練習</p>
                    </div>
                  </div>
                </div>
              </div> 
            </div><!-- col-md-8 -->
          </div><!-- col-xs-12 text-center rounded -->
      </div><!-- col-xs-12 -->
    </div><!-- row h3 text-center -->

    
    <div class="test-top row text-center">
      <div class="h4 col-md-6 col-xs-12 col-md-offset-3">

        <div class="panel panel-default">
	    <div class="panel-heading">
	       <div class="h4 panel-title">
	         <a class="h4 glyphicon glyphicon-th-list" 
	             data-toggle="collapse" data-parent="#accordion" href="#collapseStatus"> こたえ合わせ</a>
	       </div>
	    </div>
	    <div id="collapseStatus" class="panel-collapse collapse">
	      <div class="panel-body">
	        <pix:resultPic />
	      </div>
	    </div>
	   </div>
      </div><!-- end .col-md-6 -->
	</div><!-- end .row -->
	
  <footer>
      <p>&copy; moeigo.com 2014</p>
  </footer>
  </div><!-- .container -->

    <!-- Placed at the end of the document so the pages load faster -->
    <script type="text/javascript" src="scripts/jQuery-min-1.8.1.js"></script>
    <script src="bootstrap/js/bootstrap.min.js"></script>
    <script type="text/javascript" src="scripts/util.js"></script>
	<script type="text/javascript" src="scripts/results.js"></script>

</body>
</html>