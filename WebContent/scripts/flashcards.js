window.onload = readyPage;

var currentIndex = "0";
var JSONResponse = new Array();
var flashcards = new Array();
var results = new Array();
var testSounds = new Array(2);
var questionSounds;
var HTML5Compat;
var HTML5Type;

function readyPage() {
  var navPics = 
	  document.getElementById("navi").getElementsByTagName("img");
  navPics[0].style.display = "none";
  var navLabels =
	  document.getElementById("btn1").getElementsByTagName("label");
  navLabels[0].style.display = "none";
  for(var i=0; i<navPics.length; i++){
	  navPics[i].onclick = navigate;
  }
  var taketest =
	  document.getElementById("taketest");
  taketest.style.display = "none";
  taketest.onclick = takeTest;
  soundManager.onload = function(){
//	  alert("SoundManager ready");
	  doRequest();
  };
}

function doRequest(){
	  request = createRequest(); // Ajaxリクエストオブジェクト
	  if(request==null) { // リクエスト取得できない
		alert("unable to create request");
		return;
	  }
	  var timestamp = new Date(); // ブラウザーがリクエストをキャシュすることを防ぐ
	  var url = "LearnMe.do?timestamp=" + (timestamp*1) +
	  	"&doajax=ajax";
	  request.open("GET", url, true);
	  request.onreadystatechange = showQuestion;
	  request.send(null);
}

//テストの問題（絵、サウンドファイル、インデクス）のコールバック関数
function showQuestion() {
//  alert("in showQuestion\nreadyState: " + request.readyState);
  var testDiv = document.getElementById("learn"); 
  if(request.readyState == 4) {
	if (request.status == 200) {
	// JSONレスポンスを構文解析する
	// JSONレスポンスのアレイ０から３は問題のイメージ
	  JSONResponse = eval('(' + request.responseText + ')');
	  for(var i=0; i<JSONResponse.length; i++) {
		var JSONQuestion = JSONResponse[i];
		for (var j=0; j<4; j++){
			flashcards[(i*4)+j] = JSONQuestion[j];
		}
	  } //end for
//	  alert("flashcards array: " + flashcards + "\n\nflashcards.length: " + flashcards.length);

	  for (var k=0; k < flashcards.length; k++){
//		  alert("in K for. k = " + k + "\nflashcards[k] = " + flashcards[k]);
		  var qnDivName = "qnDiv" + k;
		  var currentQnDiv = document.createElement("div");
		  var qnDivId = document.createAttribute("id");
		  qnDivId.nodeValue = qnDivName;
		  currentQnDiv.setAttributeNode(qnDivId);
		
		  //add 'questionContainer class to all question divs for layout purposes
		  currentQnDiv.className = "questionContainer";
		
		  // create and place image
		  // imgエレメントを作ってページに設置する
		  var flashCardPic = document.createElement("img");
		  flashCardPic.setAttribute("src", "img/" + flashcards[k] + ".gif");
		  currentQnDiv.appendChild(flashCardPic);
		  currentQnDiv.style.display = "none"; //don't display created qn
		  testDiv.appendChild(currentQnDiv);	    
       } //end K for loop   	
	  
	  //  set onclick property for new pics
	  //  前設置したimgにonclick関数をつける
	  //  locate and display 1st question which is called "qnDiv0" 
      var imagesDiv = document.getElementById("qnDiv0");
      imagesDiv.style.display = "";
      currentIndex = 0;
 
      var images = imagesDiv.getElementsByTagName("img");
      for(var i=0; i<images.length; i++){
  	    images[i].onclick = playSound;
	  }
//------------------------------------------------------------------------------------      
      questionSounds = new Array(flashcards.length);

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
    	  for (var i=0; i<flashcards.length; i++){
    		  soundNames[i] = getWord(flashcards[i]) + i;
    		  var sound = document.createElement("audio");
    		  var srcAtt = audioHtmlTag[0] + flashcards[i] + audioHtmlTag[1];
    		  sound.setAttribute("src", srcAtt);
//			  alert("sound src att: "  + srcAtt);
    		  sound.setAttribute("preload", "auto");
    		  sound.setAttribute("id", soundNames[i]);
    		  sound.load();
    		  questionSounds[i] = sound;
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

//          alert("audio tag format: " + audioHtmlTag[0]);
      } else {        
    	  for (var i=0; i<flashcards.length; i++){
    		  var soundId = getWord(flashcards[i]) + i;
    		  var soundUrl = "sound/mp3/" + flashcards[i] + ".mp3";
    		  var optionSound = soundManager.createSound({
    			id: soundId,
    			url: soundUrl,
    			autoload: true,
    			autoplay: false,
    			volume: 150
    		  });
    		  optionSound.load();
    		  questionSounds[i] = optionSound;
    	  }//end i for
      }//end else
//----------------------------------------------------------------------
      delete request;
	}//end AJAX request.status if
  }//end AJAX readyState if
}//close function

function nextQn(e) {
//    alert("in nextQn");
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
	if (currentIndex < flashcards.length-1){
		nextPic.style.display = "";
		nextLabel.style.display = "";
	}
	if(currentIndex==flashcards.length-1){
		nextPic.style.display = "none";
		nextLabel.style.display = "none";
		document.getElementById("taketest").style.display = "";
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
		images[i].onclick = playSound;
	}
}

function navigate(e){
  var clicked = getActivatedObject(e);
  if ((clicked.name == "back")&&(currentIndex>0)){
	  var qnName = "qnDiv" + currentIndex;
	  var oldQuestion = document.getElementById(qnName);
	  oldQuestion.style.display = "none";
	  currentIndex--;
  }
  if ((clicked.name == "next")&&(currentIndex<flashcards.length-1)){
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
  questionSounds[currentIndex].play();
}

function takeTest(){
	document.forms["testForm"].submit();
//	var url = "GetTest.do?exclusion=yes&timestamp=" + (timestamp*1);
}