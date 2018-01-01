window.onload = startup;

var currentIndex = 0;
var answers = new Array();
var JSONResponse = new Array();
//var testSounds = new Array(2);
var questionSounds = new Array();
var audioHtmlTag;
var soundNames = new Array();
var oggUser = ["sound/ogg/", ".ogg"];
var mp3User = ["sound/mp3/", ".mp3"];

function startup() {	
  //alert("startupe");
  audioHtmlTag = "uninitialized";
  
  var audio = new Audio();
  var canPlayOgg = !!audio.canPlayType && audio.canPlayType('audio/ogg; codecs="vorbis"') != "";
  if(canPlayOgg){
	  audioHtmlTag = oggUser;
  } else {
	  audioHtmlTag = mp3User;
  }

  var corrSnd = new Audio("sound/correct" + audioHtmlTag[1]);
  corrSnd.setAttribute("id", "correctSound");
  $('#correct-sounds')[0].appendChild(corrSnd);

  var incorrSnd = new Audio("sound/incorrect" + audioHtmlTag[1]);
  incorrSnd.setAttribute("id", "incorrectSound");
  $('#correct-sounds')[0].appendChild(incorrSnd);
  
  var soundButton = $('#sndButtonDiv')[0];
  soundButton.style.display = "inline";
  $('#sndButtonDiv')[0].style.display = "inline";
  $('#spinner').hide();

  soundButton.onclick = playSound;
  chooseTest();
}

//テストのレベルを選択するAjax コール
function chooseTest() {
	//alert("choosetest");
	$.ajax({
		dataType: "json",
		cache: false,
		url: 'Manage.do?ajax=ajax',
		success: function(data){showQuestion(data);},
		timeout:6000
	});
	$.ajaxStart(function(){
		$('#test').hide();
		$('#spinner').show();
	    }
	);
	
	$.ajaxStop(function(){
		$('#test').show();
		$('#spinner').hide();
        }
    );
	/*	
*/	
}

//テストの問題（絵、サウンドファイル、インデクス）のコールバック関数
function showQuestion(data) {
	JSONResponse = data;
	//alert("showquestion");
	var testDiv = document.getElementById("test"); 
	while( testDiv.hasChildNodes() ){
	    testDiv.removeChild(testDiv.lastChild);
	}
	$.each(JSONResponse, function(i,item){
		//create div for four pics and set id
		//alert("i: " + i);
		//add 'questionContainer' class to all question divs for layout purposes
		var currentQnJQ = $("<div/>", {"class": "questionContainer row", id: "qnDiv" + i});
		var currentQnDiv = currentQnJQ[0];
		
		// create and place first image
		// imgエレメントを作ってページに設置する
		var pic0DivJQ = $("<div/>", {"class": "col-md-3 col-sm-6 col-xs-12 questionOneDiv",	id: "Qn" + i + "Sel0"});
		var pic0Div = pic0DivJQ[0];
		
		var pic0JQ = $('<img/>',{'src': "img/" + item[0] + ".png", 'title': '0',
			"class": "img-responsive center-block"});
		var pic0 = pic0JQ[0];			
		currentQnDiv.appendChild(pic0Div);
		pic0Div.appendChild(pic0);
		
		// create and place second image
		var pic1DivJQ = $("<div/>", {"class": "col-md-3 col-sm-6 col-xs-12 questionTwoDiv",	id: "Qn" + i + "Sel1"});
		var pic1Div = pic1DivJQ[0];
		
		var pic1JQ = $('<img/>',{'src': "img/" + item[1] + ".png", 'title': '1',
			"class": "img-responsive center-block"});
		var pic1 = pic1JQ[0];
		currentQnDiv.appendChild(pic1Div);
		pic1Div.appendChild(pic1);
		
		// create and place third image
		var pic2DivJQ = $("<div/>", {"class": "col-md-3 col-sm-6 col-xs-12 questionThreeDiv", id: "Qn" + i + "Sel2"});
		var pic2Div = pic2DivJQ[0];
		
		var pic2JQ = $('<img/>',{'src': "img/" + item[2] + ".png", 'title': '2',
			"class": "img-responsive center-block"});
		var pic2 = pic2JQ[0];			
		currentQnDiv.appendChild(pic2Div);
		pic2Div.appendChild(pic2);
		
		// create and place fourth image
		var pic3DivJQ = $("<div/>", {"class": "col-md-3 col-sm-6 col-xs-12 questionFourDiv", id: "Qn" + i + "Sel3"});
		var pic3Div = pic3DivJQ[0];
		
		var pic3JQ = $('<img/>',{'src': "img/" + item[3] + ".png", 'title': '3',
			"class": "img-responsive center-block"});
		var pic3 = pic3JQ[0];			
		currentQnDiv.appendChild(pic3Div);
		pic3Div.appendChild(pic3);

		/*var currentSound = */makeSound(i);
		//currentQnDiv.appendChild(currentSound);
		/*		
*/		
		currentQnDiv.style.display = "none"; //don't display created qn
		testDiv.appendChild(currentQnDiv);	   
		if(i==JSONResponse.length-1){
			//alert("activated if");
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
		currentPic.onclick = checker;

	}
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
	$('#correctSound')[0].play(); //正解サウンドファイル
	imgDiv.style.background = "rgb(0, 255, 0)";
	FadeColour(imgDiv.id, 0, 255, 0, 255, 255, 255, 1000, 12);
	setTimeout(function(){;nextQn(e); e = null; // サウンドファイルを再生するのに、1秒待つ
	  }, 1000);
/*
	nextQn(e);
*/
  } else {
	$('#incorrectSound')[0].play(); //不正解サウンドファイル
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
	testDiv.removeChild(testDiv.firstChild);
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

function makeSound(index){
	//alert("makeSounds: " + JSONResponse[index][4]);
	var sndIdx = JSONResponse[index][4]; //the index for the sound
	var soundWord = JSONResponse[index][sndIdx];
	soundNames[index] = getWord(JSONResponse[index][sndIdx]) + index;
	//alert("sound name: " + soundWord + ".mp3");
	var sound = new Audio(audioHtmlTag[0] + soundWord + audioHtmlTag[1]);
	//sound.play();
	sound.setAttribute("id", soundNames[index]);
	questionSounds[index] = sound;
}

function playSound(){
	//alert("playSound: " + questionSounds[currentIndex].getAttribute('src'));
	questionSounds[currentIndex].play();
}