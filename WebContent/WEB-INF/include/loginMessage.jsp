<%@ page import="java.util.*" language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>

<head>
<style media="screen" type="text/css">
#loginMsg{
  float: left;
  color: #996633;
  border-radius: 10px;
  height: 40px;
  width: 200px;
  margin: 0px auto 0px auto;
  padding: 0px;
  background: ${messageAlert};
  border: ${messageBorder};
}
#loginMsg p {
  padding: 3px 3px 3px 10px;
  margin: 0px;
  font-size: 80%;
  font-weight: bold;
}

</style>
</head>

<div id="loginMsg">
  <p>${message}</p>
</div>