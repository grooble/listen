window.onload = initPage;

var more = null;
var less = null;

function initPage(){
//	alert("in results.js initPage");
	document.getElementById("results").style.display = "none";
	var linkDiv = 
		document.getElementById("links");
	var links = linkDiv.getElementsByTagName("a");
//	alert("links[0].name is: " +links[0].name);
	if(links[0].name=="show"){
		more = links[0];
		less = links[1];
	} else {
		more = links[1];
		less = links[0];
	}
	more.onclick = showDetail;
	less.onclick = hideDetail;
	less.style.display = "none";
} 

function showDetail(){
//	alert("this is showDetail");
	more.style.display = "none";
	less.style.display = "";
	document.getElementById("results").style.display = "";

}

function hideDetail(){
//	alert("this is hideDetail");
	less.style.display = "none";
	more.style.display = "";
	document.getElementById("results").style.display = "none";
}