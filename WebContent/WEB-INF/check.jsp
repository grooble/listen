<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="pix" tagdir="/WEB-INF/tags" %>

<html>
<head>
  <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>もえいごリスニング</title>
  <link rel="icon" 
      type="image/png" 
      href="img/titles/moeigo-icon.png" />  
  <link href="bootstrap/css/bootstrap.css" rel="stylesheet">
  <link href="css/grid.css" rel="stylesheet">
  <link href="css/styles.css" rel="stylesheet">
  <link rel="stylesheet" type="text/css" href="css/loader.css" />
</head>

<body>
<%@ include file="include/Navbar.jspf" %>
  <div class="container profile-top"> 
  <div class="row">
    <p>クリックして、正しいおとを聞いてくりかえしましょう<br />
   Listen and Repeat</p>
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
  
  
  <div class="row">
    <div class="col-xs-offset-2 col-xs-8" id="test">
    </div>
  </div>
  
  <div class="filler"></div>

   <div class="row" id="navi">
     <div class="col-xs-offset-2 col-xs-8" >
     <div class="col-sm-3 col-xs-6 col-md-3">
       <button type="button" class="btn btn-default btn-lg center-block" id="btn-back">
         <span class="glyphicon glyphicon-chevron-left"></span> 前
       </button>
     </div>
     <div class="col-sm-3 col-sm-offset-6 col-xs-6 col-md-3 col-md-offset-6">
       <button type="button" class="btn btn-default btn-lg center-block" id="btn-next">
         <span class="glyphicon glyphicon-chevron-right"></span> 次
       </button>
     </div>
     </div>
   </div>
       
  <footer>
      <p>&copy; moeigo.com 2014</p>
  </footer>
  </div><!-- .container -->
       
  <script type="text/javascript" src="scripts/jQuery-min-1.8.1.js"></script>	
  <script src="bootstrap/js/bootstrap.min.js"></script>
  <script type="text/javascript" src="scripts/json2.js"></script>
  <script type="text/javascript" src="scripts/util.js"></script>
  <script type="text/javascript" src="scripts/checker.js"></script>
</body>
</html>