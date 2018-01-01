//  This file handles ajax calls to load images,
//  send correct answers to the server and load 
//  and play sounds. 

//  このファイルは、イメージやサウンドファイルのファイルをAjaxでロードして、また答え
//  をサーバーに送ったり、サウンドファイルを再生するファイルです。

//  グローバル変数を設定する。
//  ページがロードしたらinitPage関数を設定する。

window.onload = startup;

var currentIndex = "0";
var answers = new Array();
var JSONResponse = new Array();
var testSounds = new Array(2);
var questionSounds = new Array();

function startup() {
  alert("startup");
  document.getElementById("sndButtonDiv").style.display = "";
  soundManager.onload = function(){
//	  alert("SoundManager ready");
	  chooseTest();
  };
}

//テストのレベルを選択するAjax コール
function chooseTest() {
  request = createRequest(); // Ajaxリクエストオブジェクト
  if(request==null) { // リクエスト取得できない
	alert("unable to create request");
	return;
  }
  var timestamp = new Date(); // ブラウザーがリクエストをキャシュすることを防ぐ
  var url = "Manage.do?ajax=ajax" +
    "&timestamp=" + (timestamp*1);
  request.open("GET", url, true);
  request.onreadystatechange = showQuestion;
  request.send(null);
  showLoader();
  
  //  make sound button visible and set plaｙSound 
  //  function for onclick
  //  スピーカのボタンをvisibleにして、playSoundの関数を
  //  onclickにする。
  
  var soundBtnDiv = 
	  document.getElementById("sndButtonDiv");
  soundBtnDiv.style.display = "";
  soundBtnDiv.onclick = playSound;
}

//テストの問題（絵、サウンドファイル、インデクス）のコールバック関数
function showQuestion() {
//  alert("in showQuestion\nreadyState: " + request.readyState);
  var testDiv = document.getElementById("test"); 
  if(request.readyState == 4) {
	if (request.status == 200) {
	// JSONレスポンスを構文解析する
	// JSONレスポンスのアレイ０から３は問題のイメージ
	  JSONResponse = eval('(' + request.responseText + ')');
//	  alert("JSON: " + JSONResponse + "\n\nJSONResponse.length" + JSONResponse.length);
	  for(var i=0; i<JSONResponse.length; i++) {
		var JSONQuestion = JSONResponse[i];

		//create div for four pics and set id
		var qnDivName = "qnDiv" + i;
		var currentQnDiv = document.createElement("div");
		var qnDivId = document.createAttribute("id");
		qnDivId.nodeValue = qnDivName;
		currentQnDiv.setAttributeNode(qnDivId);
		
		//add 'questionContainer class to all question divs for layout purposes
		currentQnDiv.className = "questionContainer";

		
		// create and place first image
		// imgエレメントを作ってページに設置する
	    var pic0 = document.createElement("img");
	    var pic0Div = document.createElement("div");
	    pic0.setAttribute("src", "img/" + JSONQuestion[0] + ".gif");
	    pic0.setAttribute("title", '0');
		pic0Div.className = "questionOneDiv";
		var pic0DivIdName = "Qn" + i + "Sel0";
		var pic0DivId = document.createAttribute("id");
		pic0DivId.nodeValue = pic0DivIdName;
		pic0Div.setAttributeNode(pic0DivId);
//		alert("id check: " + pic0Div.id);
		
	    currentQnDiv.appendChild(pic0Div);
	    pic0Div.appendChild(pic0);
	  
	  // create and place second image
	    var pic1 = document.createElement("img");
	    var pic1Div = document.createElement("div");
	    pic1.setAttribute("src", "img/" + JSONQuestion[1] + ".gif");
	    pic1.setAttribute("title", '1');
	    pic1Div.className = "questionTwoDiv";
	    var pic1DivIdName = "Qn" + i + "Sel1";
		var pic1DivId = document.createAttribute("id");
		pic1DivId.nodeValue = pic1DivIdName;
		pic1Div.setAttributeNode(pic1DivId);
		
		currentQnDiv.appendChild(pic1Div);
	    pic1Div.appendChild(pic1);
 
	  // create and place third image
	    var pic2 = document.createElement("img");
	    var pic2Div = document.createElement("div");
	    pic2.setAttribute("src", "img/" + JSONQuestion[2] + ".gif");
	    pic2.setAttribute("title", '2');
	    pic2Div.className = "questionThreeDiv";
	    var pic2DivIdName = "Qn" + i + "Sel2";
		var pic2DivId = document.createAttribute("id");
		pic2DivId.nodeValue = pic2DivIdName;
		pic2Div.setAttributeNode(pic2DivId);
		
		currentQnDiv.appendChild(pic2Div);
	    pic2Div.appendChild(pic2);

	  // create and place fourth image
	    var pic3 = document.createElement("img");
	    var pic3Div = document.createElement("div");
	    pic3.setAttribute("src", "img/" + JSONQuestion[3] + ".gif");
	    pic3.setAttribute("title", '3');
	    pic3Div.className = "questionFourDiv";
	    var pic3DivIdName = "Qn" + i + "Sel3";
		var pic3DivId = document.createAttribute("id");
		pic3DivId.nodeValue = pic3DivIdName;
		pic3Div.setAttributeNode(pic3DivId);
		
		currentQnDiv.appendChild(pic3Div);
	    pic3Div.appendChild(pic3);

	    currentQnDiv.style.display = "none"; //don't display created qn
	    testDiv.appendChild(currentQnDiv);	    
	  }	//end for loop
	  
	  //  set onclick property for new pics
	  //  前設置したimgにonclick関数をつける
	  //  locate and display 1st question which is called "qnDiv0" 
      var imagesDiv = document.getElementById("qnDiv0");
      imagesDiv.style.display = "";
      var images = imagesDiv.getElementsByTagName("img");
      for(var i=0; i<images.length; i++){
  	    var currentPic = images[i];
  	  	addEventHandler(currentPic, "mouseover", showHand);
  		addEventHandler(currentPic, "mouseout", showPointer);
  		addEventHandler(currentPic, "click", checkMe);
  	    currentPic.className = "spacing";
	  }
      
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
        	var sndIdx = JSONResponse[i][4]; //the index for the sound
          	soundNames[i] = getWord(JSONResponse[i][sndIdx]) + i;
      	    var sound = document.createElement("audio");
      	    var srcAtt = audioHtmlTag[0] + JSONResponse[i][sndIdx] + audioHtmlTag[1];
      	    sound.setAttribute("src", srcAtt);
//      	    alert("sound src att: "  + srcAtt);
      	    sound.setAttribute("preload", "auto");
      	    sound.setAttribute("id", soundNames[i]);
      	    sound.load();
      	    questionSounds[i] = sound;
      	  }  

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
    	  // if HTML5 or a valid format is not available,
    	  // create soundManager sounds for answers.
//    	  alert("SoundManager");
          var soundNames = new Array();
          for(var i=0; i<JSONResponse.length; i++){
        	var idx = JSONResponse[i][4];
        	soundNames[i] = getWord("" + JSONResponse[i][idx]) + i;
//        	alert("soundname[i]: " + soundNames[i] + 
//        			"\nJSONResponse[i][idx]: " + JSONResponse[i][idx]);
            var questionSound = soundManager.createSound({
              id: soundNames[i],
              url: 'sound/mp3/' + JSONResponse[i][idx] + '.mp3',
              autoload: true,
              autoplay: false,
              volume: 170
            });
            questionSound.load();
            questionSounds[i] = questionSound;
          }
          
          var correctSnd = soundManager.createSound({
          	id: 'correct',
          	url: 'sound/correct.mp3',
          	autoload: true,
          	autoplay: false
            });
          var wrongSnd = soundManager.createSound({
            id: 'incorrect',
            url: 'sound/incorrect.mp3',
            autoload: true,
            autoplay: false,
            volume: 70
            });
          testSounds[0] = correctSnd;
          testSounds[1] = wrongSnd;
          testSounds[0].load();
          testSounds[1].load();
//      }
      }
	  delete request;
	  
	  hideLoader();
	}
  }
}


//次の問題を示すAjax関数。
//最後の問題だったら、フォームをサブミットする。
function nextQn(e) {
  if(currentIndex==4){ //最後の問題だから、サブミットする。
//	alert("in nextQn if clause.\nanswers: " + answers.toString() +
//			"\ncurrentIndex: " + currentIndex);
	var JSONAnswers = JSON.stringify(answers);
//	alert("JSONAnswers: " + JSONAnswers);
	var timestamp = (new Date()) * 1;
	document.forms['testPageForm'].answered.value = JSONAnswers;
	document.forms['testPageForm'].timestamp.value = timestamp;
	document.forms['testPageForm'].submit();
	JSONResponse = null;
	soundNames = null;
  } else { 
	var testDiv = document.getElementById("test");
	testDiv.removeChild(testDiv.firstChild);
	var newQn = testDiv.firstChild;
	newQn.style.display = "";
    var images = newQn.getElementsByTagName("img");
    for(var i=0; i<images.length; i++){
  	  var currentPic = images[i];
  	  addEventHandler(currentPic, "mouseover", showHand);
  	  addEventHandler(currentPic, "mouseout", showPointer);
  	  addEventHandler(currentPic, "click", checkMe);
    }
  }
  currentIndex++;
}

function playSound(){
	questionSounds[currentIndex].play();
}

function checkMe(e){
  var my = getActivatedObject(e); //クロスブラウザの"this"オブジェクトを取る。
  my.onclick = null;
  answers[currentIndex] = parseInt(my.title);
  var clickedWord = getWord(my.src);
//  alert("my.title: " + my.title + "\nclickedWord: " + clickedWord);
  var soundIndex = parseInt(JSONResponse[currentIndex][4]);
  var soundWord = getWord(JSONResponse[currentIndex][soundIndex]);
//  alert("soundIndex: " + soundIndex + "\nsoundWord: " + soundWord);
  var imgDiv = my.parentNode;
  if (soundWord==clickedWord){
	testSounds[0].play(); //正解サウンドファイル
	imgDiv.style.background = "rgb(0, 255, 0)";
	FadeColour(imgDiv.id, 0, 255, 0, 255, 255, 255, 1000, 12);
//	alert("clicker5-1.js\nimgDiv.id: " + imgDiv.id);
	setTimeout(function(){;nextQn(my); my = null; // サウンドファイルを再生するのに、1秒待つ
	  }, 1000);

  } else {
	testSounds[1].play();  // 不正解サウンドファイル
	imgDiv.style.background = "rgb(255,0,0)";
	FadeColour(imgDiv.id, 255, 0, 0, 255, 255, 255, 1000, 12);
	setTimeout(function(){;nextQn(my); my = null; // サウンドファイルを再生するのに、1秒待つ
	  }, 1000);
  }
//  nextQn(my);
}

function showLoader(){
	var obj = document.getElementById('divLoader');
	var sndbtn = document.getElementById('sndButton');
	var testField = document.getElementById('test');
	sndbtn.disabled = true;
	testField.disabled = true;
	if (obj) obj.style.visibility = 'visible';
	setTimeout("hideLoader", 4000);

}

function hideLoader(){
	var obj = document.getElementById('divLoader');
	if(obj.style.visibility == 'visible'){
		var sndbtn = document.getElementById('sndButton');
		var testField = document.getElementById('test');
		sndbtn.disabled = false;
		testField.disabled = false;
		if(obj) obj.style.visibility = 'hidden';
	}
}