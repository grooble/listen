window.onload = readyPage;

var currentIndex = 0;
var JSONResponse = new Array();
var testSounds = new Array(2);
var questionSounds = new Array();

function readyPage() {
  var navPics = $("#navi img");
  alert("navPics length: " + navPics.length);
  navPics[0].style.display = "none";
  for (i=0; i<navPics.length; i++){
	  navPics[i].onclick = navigate;
  }

  var navLabels = $("#btn1 label");
  navLabels[0].style.display = "none";
  
  var taketest = $("#taketest");
  taketest[0].style.display = "none";
  taketest.on('click', takeTest());

  $("#spinner").hide();
  
  doRequest();
}

function doRequest(){
	alert("in doRequest");
	$('#learn').hide();
	$('#spinner').show();
	$.ajaxSetup({ cache: false });
	$.getJSON(
		'LearnMe.do?ajax=ajax', 
		function(data) {
		    alert("got JSON data\n" + data[1][4]);
			JSONResponse = data;
			showQuestion(data);
		}
	);
}

//テストの問題（絵、サウンドファイル、インデクス）のコールバック関数
function showQuestion() {
	var testDiv = document.getElementById("test"); 
	while( testDiv.hasChildNodes() ){
	    testDiv.removeChild(testDiv.lastChild);
	}
	$.each(JSONResponse, function(i,item){
		//create div for four pics and set id
		alert("i: " + i);
		var currentQnJQ = $("<div/>", {"class": "questionContainer", id: "qnDiv" + i});
		var currentQnDiv = currentQnJQ[0];

		//add 'questionContainer' class to all question divs for layout purposes
		
		// create and place first image
		// imgエレメントを作ってページに設置する
		var pic0DivJQ = $("<div/>", {"class": "questionOneDiv",	id: "Qn" + i + "Sel0"});
		var pic0Div = pic0DivJQ[0];
		
		var pic0JQ = $('<img/>',{'src': "img/" + item[0] + ".gif", 'title': '0'});
		var pic0 = pic0JQ[0];			
		currentQnDiv.appendChild(pic0Div);
		pic0Div.appendChild(pic0);
		
		// create and place second image
		var pic1DivJQ = $("<div/>", {"class": "questionTwoDiv",	id: "Qn" + i + "Sel1"});
		var pic1Div = pic1DivJQ[0];
		
		var pic1JQ = $('<img/>',{'src': "img/" + item[1] + ".gif", 'title': '1'});
		var pic1 = pic1JQ[0];
		currentQnDiv.appendChild(pic1Div);
		pic1Div.appendChild(pic1);
		
		// create and place third image
		var pic2DivJQ = $("<div/>", {"class": "questionThreeDiv", id: "Qn" + i + "Sel2"});
		var pic2Div = pic2DivJQ[0];
		
		var pic2JQ = $('<img/>',{'src': "img/" + item[2] + ".gif", 'title': '2'});
		var pic2 = pic2JQ[0];			
		currentQnDiv.appendChild(pic2Div);
		pic2Div.appendChild(pic2);
		
		// create and place fourth image
		var pic3DivJQ = $("<div/>", {"class": "questionFourDiv", id: "Qn" + i + "Sel3"});
		var pic3Div = pic3DivJQ[0];
		
		var pic3JQ = $('<img/>',{'src': "img/" + item[3] + ".gif", 'title': '3'});
		var pic3 = pic3JQ[0];			
		currentQnDiv.appendChild(pic3Div);
		pic3Div.appendChild(pic3);
		
		
		currentQnDiv.style.display = "none"; //don't display created qn
		testDiv.appendChild(currentQnDiv);	   
		if(i==JSONResponse.length-1){
//			alert("activated if");
			$('#spinner').hide();
			$('#test').show();
		}
	});
	
	//  set onclick property for first qn pics
	//  前設置したimgにonclick関数をつける
	//  locate and display 1st question which is called "qnDiv0" 
	var imagesDiv = document.getElementById("qnDiv0");
	imagesDiv.style.display = "";
	var images = imagesDiv.getElementsByTagName("img");
	for(var j=0; j<images.length; j++){
		images[j].onclick = playSound;
		currentPic.className = "spacing";
	}
	
	prepareSounds();	
}

function nextQn(e) {
//  alert("in nextQn");
	var naviPics = 
		document.getElementById("navi").getElementsByTagName("img");
	var naviLabels =
		document.getElementById("navi").getElementsByTagName("label");
	var backPic;
	var nextPic;
	var backLabel;
	var nextLabel;
	if (naviPics[0].name=="back"){
		backPic = naviPics[0];
		nextPic = naviPics[1];
	} else {
		backPic = naviPics[1];
		nextPic = naviPics[0];
	}
	if (naviLabels[0].title=="back"){
		backLabel = naviLabels[0];
		nextLabel = naviLabels[1];
	} else {
		backLabel = naviLabels[1];
		nextLabel = naviLabels[0];
	}
	
	if (currentIndex > 0){
		backPic.style.display = "";
		backLabel.style.display = "";
	}
	if (currentIndex < JSONResponse.length-1){
		nextPic.style.display = "";
		nextLabel.style.display = "";
	}
	if(currentIndex==JSONResponse.length-1){
		nextPic.style.display = "none";
		nextLabel.style.display = "none";
		var takeTestBtn = document.getElementById("taketest");
		takeTestBtn.style.display = "";
	  	addEventHandler(takeTestBtn, "mouseover", showHand);
		addEventHandler(takeTestBtn, "mouseout", showPointer);

	}
	if(currentIndex==0){
		backPic.style.display = "none";
		backLabel.style.display = "none";
	}
	
	var qnName = "qnDiv" + currentIndex;
	var currentDiv = document.getElementById(qnName);
	currentDiv.style.display = "";
	var images = currentDiv.getElementsByTagName("img");
	for(var i=0; i<images.length; i++){
		var img = images[i];
		addEventHandler(img, "click", playSound);
	  	addEventHandler(img, "mouseover", showHand);
		addEventHandler(img, "mouseout", showPointer);

	}
}

function navigate(e){
  alert("navigate called");
  var clicked = getActivatedObject(e);
  if ((clicked.name == "back")&&(currentIndex>0)){
	  var qnName = "qnDiv" + currentIndex;
	  var oldQuestion = document.getElementById(qnName);
	  oldQuestion.style.display = "none";
	  currentIndex--;
  }
  if ((clicked.name == "next")&&(currentIndex<JSONResponse.length-1)){
	  var qnName = "qnDiv" + currentIndex;
	  var oldQuestion = document.getElementById(qnName);
	  oldQuestion.style.display = "none";
	  currentIndex++;
  }
  nextQn();
}

function playSound(e){
  var object = getActivatedObject(e);
  var index = parseInt(object.title);
  var toPlayIndex = parseInt((parseInt(currentIndex)*4) + index);
  questionSounds[toPlayIndex].play();
}

function takeTest(){
	document.forms["testForm"].submit();
	var url = "GetTest.do?exclusion=yes&timestamp=" + (timestamp*1);
}

function prepareSounds(){
    var arraySize = (JSONResponse.length)*4;
    questionSounds = new Array(arraySize);

	  // create sounds for correct answers.
    var soundNames = new Array();
    var oggUser = ["sound/ogg/", ".ogg"];
    var mp3User = ["sound/mp3/", ".mp3"];
    var audioHtmlTag;
    
    // check for HTML5 compatability, then check for ogg or mp3 format
	if(document.createElement('audio').canPlayType){
		if(document.createElement('audio').canPlayType('audio/ogg')){
			  audioHtmlTag = oggUser;
		  }
		if(document.createElement('audio').canPlayType('audio/mpeg')){
			audioHtmlTag = mp3User;
		}
  	  //create the sounds for the answers
		for(var i=0; i<JSONResponse.length; i++){
			for(var j=0; j<4; j++){
				soundNames[i] = getWord(JSONResponse[i][j]) + i;
				var sound = document.createElement("audio");
				var srcAtt = audioHtmlTag[0] + JSONResponse[i][j] + audioHtmlTag[1];
				sound.setAttribute("src", srcAtt);
				//  			  alert("sound src att: "  + srcAtt);
				sound.setAttribute("preload", "auto");
				sound.setAttribute("id", soundNames[i]);
				sound.load();
				var arrayIndex = parseInt((i*4) + j);
				questionSounds[arrayIndex] = sound;
			}//end j for
		}//end i for

        //  create sounds for correct and incorrect
        var corrSnd = document.createElement("audio");
        corrSnd.setAttribute("id", "correctSound");
        corrSnd.setAttribute("preload", "auto");
        corrSnd.setAttribute("src", "sound/correct" + audioHtmlTag[1]);
        corrSnd.load();
        
        var incorrSnd = document.createElement("audio");
        incorrSnd.setAttribute("id", "incorrectSound");
        incorrSnd.setAttribute("preload", "auto");
        incorrSnd.setAttribute("src", "sound/incorrect" + audioHtmlTag[1]);
        incorrSnd.load();
        
        testSounds[0] = corrSnd;
        testSounds[1] = incorrSnd;      

//        alert("audio tag format: " + audioHtmlTag[0]);
    } else {        
  	  for (var i=0; i<JSONResponse.length; i++){
  		  for(var j=0; j<4; j++){
  			  var soundId = getWord(JSONResponse[i][j]) + i;
  			  var soundUrl = "sound/mp3/" + JSONResponse[i][j] + ".mp3";
  			  var optionSound = soundManager.createSound({
  				  id: soundId,
  				  url: soundUrl,
  				  autoload: true,
  				  autoplay: false,
  				  volume: 150
  			  });
  			  optionSound.load();
  			  var arrayIndex = parseInt((i*4) + j);
  			  questionSounds[arrayIndex] = optionSound;
  		  }//end j for
  	  }//end i for
    }//end else
}