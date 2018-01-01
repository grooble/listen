function createRequest() {
  try {
    request = new XMLHttpRequest();
  } catch (tryMS) {
    try {
      request = new ActiveXObject("Msxml2.XMLHTTP");
    } catch (otherMS) {
      try {
        request = new ActiveXObject("Microsoft.XMLHTTP");
      } catch (failed) {
        request = null;
      }
    }
  }	
  return request;
}

function addEventHandler(obj, eventName, handler) {
  if(document.attachEvent) {
    obj.attachEvent("on" + eventName, handler);
  } else if (document.addEventListener) {
    obj.addEventListener(eventName, handler, false);
  }
}

function getActivatedObject(e) {
  var obj;
  if (!e) {
    // early version of IE
	obj = window.event.srcElement;
	} else if (e.srcElement) {
	  //IE 7 or later
	  obj = e.srcElement;
	} else {
	  // DOM Level 2 browser
	  obj = e.target;
	}
	
	return obj;
}

//パースからファイル名を解析する
function getWord(line){
  var gotLine = line;
  if(gotLine.length != 0){
	  gotLine = gotLine.substr(0);
	  var dotIndex = gotLine.lastIndexOf(".");
	  var slashIndex = gotLine.lastIndexOf("\/");
	  var theWord = "";
	  if(dotIndex != (-1)){
		  theWord = gotLine.slice(slashIndex+1, dotIndex);
		  return theWord;
	  } else {
		  var tokens = gotLine.split('\/'); 
		  theWord = tokens[tokens.length-1];
		  return theWord;
	  }
  } 
  else {return null;}
}

/*-----------------------------------------------------------------*/
/*       Colour Fade Functions                                     */
/*                                                                 */
/*takes the DOM element to have it's colour set and the rgb values */
function SetColour(elemId, r, g, b)
{
	var elem = document.getElementById(elemId);
//	alert("util.js \nelemId: " + elemId + "\nelem: " + elem);
	if(elem != null){
		if (r > 255)
			r = 255;
		if (g > 255)
			g = 255;
		if (b > 255)
			b = 255;
	
		if (r < 0)
			r = 0; 
		if (g < 0)
			g = 0; 
		if (b < 0)
			b = 0; 
		var colourString = "rgb(" + r + ", " + g + ", " + b + ")";
		elem.style.background = colourString;
	}
}

/* The function that is first called.                              */
/* It calculates the number of steps and their duration and        */
/* the colour change per step. FadeColourStep is called            */
/* to fade individual steps.                                       */
function FadeColour(elemId, fr, fg, fb, tr, tg, tb, time, fps)
{
	var steps = Math.ceil(fps * (time / 1000));
	var deltaR = (tr - fr) / steps;
	var deltaG = (tg - fg) / steps;
	var deltaB = (tb - fb) / steps;
	
	FadeColourStep(elemId, 0, steps, fr, fg, fb, deltaR, deltaG, deltaB, (time / steps));

}

/* This function is called by FadeColour.                          */
/* It iterates through the required number of steps (as calculated */
/* by FadeColour) and calls SetColour to colour the element.       */
function FadeColourStep(elemId, stepNum, steps, fr, fg, fb, deltaR, deltaG, deltaB, timePerStep)
{
	tempDelR =  Math.round(parseInt(fr) + (deltaR * stepNum));
	tempDelG =  Math.round(parseInt(fg) + (deltaG * stepNum));
	tempDelB =  Math.round(parseInt(fb) + (deltaB * stepNum));
//	var elem = document.getElementById(elemId);
	
	SetColour(elemId,tempDelR, tempDelG, tempDelB);
    if (stepNum < steps){
    	stepNum++;
    	timeoutArg = "FadeColourStep('" + elemId + "', " + stepNum + ", " + steps + ", " + 
		tempDelR + ", " + tempDelG + ", " + tempDelB + ", " + 
		deltaR + ", " + deltaG + ", " + deltaB + ", " + 
		timePerStep + ");";
        setTimeout(timeoutArg, timePerStep);
//        alert("util.js\ntimeoutArg: " + timeoutArg);
    }
}

/*********************************************************************/
function checkWhiteSpace(userText){
userText = userText.replace(/^\s+/, '').replace(/\s+$/, '');
if (userText === '') {
//    alert("text is only whitespace");
    return false;
} else {
//    alert("text contains non-whitespace: " + userText);
    return true;
}
}

function replaceURLWithHTMLLinks(text) {
	//alert("replace");
    //var exp = /(\b(https?|ftp|file):\/\/[-A-Z0-9+&@#\/%?=~_|!:,.;]*[-A-Z0-9+&@#\/%=~_|])/ig;
	if(text.substring(0,3)==="www"){
		text2 = "http://" + text;
		var expwww = /((([A-Za-z]{3,9}:(?:\/\/)?)(?:[\-;:&=\+\$,\w]+@)?[A-Za-z0-9\.\-]+|(?:www\.|[\-;:&=\+\$,\w]+@)[A-Za-z0-9\.\-]+)((?:\/[\+~%\/\.\w\-]*)?\??(?:[\-\+=&;%@\.\w]*)#?(?:[\.\!\/\\\w]*))?)/g;
	    return text2.replace(expwww,"<a href='$1'>" + text + "</a>"); 
	}
	var exp = /((([A-Za-z]{3,9}:(?:\/\/)?)(?:[\-;:&=\+\$,\w]+@)?[A-Za-z0-9\.\-]+|(?:www\.|[\-;:&=\+\$,\w]+@)[A-Za-z0-9\.\-]+)((?:\/[\+~%\/\.\w\-]*)?\??(?:[\-\+=&;%@\.\w]*)#?(?:[\.\!\/\\\w]*))?)/g;
    return text.replace(exp,"<a href='$1'>$1</a>"); 
}