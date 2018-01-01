window.onload = initPage;

function initPage() {
  alert("initPage");
  var pics = document.getElementById("chooser").getElementsByTagName("img");

  for(var i=0; i<pics.length; i++){
	  var currentPic = pics[i];
      addEventHandler(currentPic, "mouseover", showHand);
      addEventHandler(currentPic, "mouseout", hideHand);
  }
}

function showHand(e){
	var my = getActivatedObject(e);
	my.style.cursor = 'hand';
}

function hideHand(e){
	var my = getActivatedObject(e);
	my.style.cursor = 'pointer';
}

