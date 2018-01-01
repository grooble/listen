window.onload = startup;

var currentIndex = 0;
var answers = new Array();
var JSONResponse = new Array();
var testSounds = new Array(2);
var questionSounds = new Array();

function startup() {	
  //alert("startup");
  var soundButton = $('#sndButtonDiv input')[0];
  soundButton.style.display = "inline";
  $('#sndButtonDiv')[0].style.display = "inline";
  $('#spinner').hide();

  soundButton.onclick = playSound;
  
  soundManager.setup({
	  url: 'swf/soundmanager2.swf',
	  useFlashBlock: false,
	  onready: chooseTest
  });
  
}

//テストのレベルを選択するAjax コール
function chooseTest() {
	$('#test').hide();
	$('#spinner').show();
	$.ajaxSetup({ cache: false });
	$.getJSON(
		'Manage.do?ajax=ajax', 
		function(data) {
		    //alert("got JSON data\n" + data[1][4]);
			JSONResponse = data;
			showQuestion(data);
		}
	);
}

//テストの問題（絵、サウンドファイル、インデクス）のコールバック関数
function showQuestion(JSONResponse) {
	var testDiv = document.getElementById("test"); 
	while( testDiv.hasChildNodes() ){
	    testDiv.removeChild(testDiv.lastChild);
	}
	$.each(JSONResponse, function(i,item){
		//create div for four pics and set id
//		alert("i: " + i);
		var currentQnJQ = $("<div/>", {"class": "questionContainer", id: "qnDiv" + i});
		var currentQnDiv = currentQnJQ[0];

		//add 'questionContainer' class to all question divs for layout purposes
		
		// create and place first image
		// imgエレメントを作ってページに設置する
		var pic0DivJQ = $("<div/>", {"class": "col-md-3 questionOneDiv",	id: "Qn" + i + "Sel0"});
		var pic0Div = pic0DivJQ[0];
		
		var pic0JQ = $('<img/>',{'src': "img/" + item[0] + ".gif", 'title': '0'});
		var pic0 = pic0JQ[0];			
		currentQnDiv.appendChild(pic0Div);
		pic0Div.appendChild(pic0);
		
		// create and place second image
		var pic1DivJQ = $("<div/>", {"class": "col-md-3 questionTwoDiv",	id: "Qn" + i + "Sel1"});
		var pic1Div = pic1DivJQ[0];
		
		var pic1JQ = $('<img/>',{'src': "img/" + item[1] + ".gif", 'title': '1'});
		var pic1 = pic1JQ[0];
		currentQnDiv.appendChild(pic1Div);
		pic1Div.appendChild(pic1);
		
		// create and place third image
		var pic2DivJQ = $("<div/>", {"class": "col-md-3 questionThreeDiv", id: "Qn" + i + "Sel2"});
		var pic2Div = pic2DivJQ[0];
		
		var pic2JQ = $('<img/>',{'src': "img/" + item[2] + ".gif", 'title': '2'});
		var pic2 = pic2JQ[0];			
		currentQnDiv.appendChild(pic2Div);
		pic2Div.appendChild(pic2);
		
		// create and place fourth image
		var pic3DivJQ = $("<div/>", {"class": "col-md-3 questionFourDiv", id: "Qn" + i + "Sel3"});
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
		var currentPic = images[j];
		//addEventHandler(currentPic, "click", function(){checkMe;currentPic.onclick = null;});
		currentPic.onclick = checker;
	    //currentPic.ondblclick = null;
		currentPic.className = "spacing";
	}
	
	prepareSounds();	
}

function checker(e){
  var my = getActivatedObject(e); //クロスブラウザの"this"オブジェクトを取る。
  //alert("checker\nact. obj.: " + my);
  setTimeout(function(){;my.onclick=null;checkMe(my);my = null;}, 100);
}

function checkMe(e){
  answers[currentIndex] = parseInt(e.title);
  var clickedWord = getWord(e.src);
  var soundIndex = parseInt(JSONResponse[currentIndex][4]);
  var soundWord = getWord(JSONResponse[currentIndex][soundIndex]);
  var imgDiv = e.parentNode;
  if (soundWord==clickedWord){
	testSounds[0].play(); //正解サウンドファイル
	imgDiv.style.background = "rgb(0, 255, 0)";
	FadeColour(imgDiv.id, 0, 255, 0, 255, 255, 255, 1000, 12);
	setTimeout(function(){;nextQn(e); e = null; // サウンドファイルを再生するのに、1秒待つ
	  }, 1000);

  } else {
	testSounds[1].play();  // 不正解サウンドファイル
	imgDiv.style.background = "rgb(255,0,0)";
	FadeColour(imgDiv.id, 255, 0, 0, 255, 255, 255, 1000, 12);
	setTimeout(function(){;nextQn(e); e = null; // サウンドファイルを再生するのに、1秒待つ
	  }, 1000);
  }

}

//次の問題を示すAjax関数。
//最後の問題だったら、フォームをサブミットする。
function nextQn(e) {
if(currentIndex==4){ //最後の問題だから、サブミットする。
	var JSONAnswers = JSON.stringify(answers);
//	alert("JSONAnswers: " + JSONAnswers);
	var timestamp = (new Date().getTime());
	document.forms['testPageForm'].answered.value = JSONAnswers;
	document.forms['testPageForm'].timestamp.value = timestamp;
	document.forms['testPageForm'].submit();
	JSONResponse = null;
	soundNames = null;
} else {
	var testDiv = document.getElementById("test");
	var removed = testDiv.removeChild(testDiv.firstChild);
	var newQn = testDiv.firstChild;
	newQn.style.display = "";
  var images = newQn.getElementsByTagName("img");
  for(var i=0; i<images.length; i++){
	  var currentPic = images[i];
	  //addEventHandler(currentPic, "click", checkMe);
	  currentPic.onclick = checker;
  }
}
currentIndex++;
}

function prepareSounds(){
// create sounds for correct answers.
	//alert("in prepareSounds");
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
		for(var j=0; j<JSONResponse.length; j++){
			var sndIdx = JSONResponse[j][4]; //the index for the sound
			soundNames[j] = getWord(JSONResponse[j][sndIdx]) + j;
			var sound = document.createElement("audio");
			var srcAtt = audioHtmlTag[0] + JSONResponse[j][sndIdx] + audioHtmlTag[1];
			sound.setAttribute("src", srcAtt);
			sound.setAttribute("preload", "auto");
			sound.setAttribute("id", soundNames[j]);
			sound.load();
			questionSounds[j] = sound;
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
		
		
//    alert("audio tag format: " + audioHtmlTag[0]);
	} else {
		// if HTML5 or a valid format is not available,
		// create soundManager sounds for answers.
//	  alert("SoundManager");
		var soundNames = new Array();
		for(var i=0; i<JSONResponse.length; i++){
			var idx = JSONResponse[i][4];
			soundNames[i] = getWord("" + JSONResponse[i][idx]) + i;
//  	alert("soundname[i]: " + soundNames[i] + 
//  			"\nJSONResponse[i][idx]: " + JSONResponse[i][idx]);
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
	}
}

function playSound(){
	questionSounds[currentIndex].play();
}