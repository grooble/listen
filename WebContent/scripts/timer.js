window.onload = initPage;

var timeVal = 500;
var intervalId = 0;

function initPage(){
	alert("initPage");
	document.getElementById("timer").onclick = doHelpWindow;
	
}

function doHelpWindow(){
	window.open( '', 'myWindow', 'width=200, height=300, location=0, toolbar=0');
}

function doTimer(){
	//setTimeout('alert(\'timer\')', timeVal);
	intervalId = setInterval('doInterval()', 10);
}

function doInterval(){
	var time = document.getElementById("remTime");
	if(timeVal > 0){
	  if(time.firstChild != null){
	    time.removeChild(time.firstChild);
	  }
	  var timeString = "0" + timeVal;
	  if ((timeVal <100)&&(timeVal > 9)){
		  timeString = "00" + timeVal;
	  }
	  if (timeVal < 10){
		  timeString = "000" + timeVal;
	  }
	  var seconds = timeString.substring(0,2);
	  var hundredths = timeString.substring(2);
	  var txt = document.createTextNode(seconds + ":" + hundredths);
	  time.appendChild(txt);
	  timeVal = timeVal - 1;
	}
	else {
	  if(time.firstChild != null){
	    time.removeChild(time.firstChild);
	  }
	  var bangTxt = document.createTextNode("Bang!");
	  time.appendChild(bangTxt);
	  clearInterval(intervalId);
	  return false;
	}
}