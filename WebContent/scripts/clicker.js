//  This file handles ajax calls to load images,
//  send correct answers to the server and load 
//  and play sounds. 

//  このファイルは、イメージやサウンドファイルのファイルをAjaxでロードして、また答え
//  をサーバーに送ったり、サウンドファイルを再生するファイルです。

//  グローバル変数を設定する。
//  ページがロードしたらinitPage関数を設定する。

window.onload = initPage;

var currentIndex = "0";
var answers = new Array();
var JSONResponse = new Array();
var testSounds = new Array();
var questionSounds = new Array();
var validateFlag = true;
var userName = null;
var password = null;

//  それぞれのdivをイニシャライズする。
//  最初にimagesDIVとsoundDIVをdisplayなしにする。imagesのinnerHTMLをカラにする。
//  chooseDIVのイメージにonclick関数を付ける。
function initPage() {
//  alert("initPage");
  document.getElementById("loginArea").style.display = "";
  document.getElementById("chooser").style.display = "";
  document.getElementById("sndButton").style.display = "none";
  userName = document.getElementById("userName");
  password = document.getElementById("userPwd");
  if(userName != null){
//	  alert("userName found");
	  document.getElementById("loginSubmitBtn").onclick = validateForm;
  }
  var pics = document.getElementById("chooser").getElementsByTagName("img");
  for(var i=0; i<pics.length; i++){
    var currentPic = pics[i];
    currentPic.onclick = chooseTest;
  }
}

//テストのレベルを選択するAjax コール
function chooseTest(e) {
  //テストを選んでフォームをスブミットしたら、カラのログインフィールドに
  //validateForm関数をよばれないようにvalidateFlagをfalseにする。
  validateFlag = false; 
  var my = getActivatedObject(e); //クロスブラウザの"this"オブジェクトを取る。
  my.onclick = null;
  request = createRequest(); // Ajaxリクエストオブジェクト
  if(request==null) { // リクエスト取得できない
	alert("unable to create request");
	return;
  }
  var timestamp = new Date(); // ブラウザーがリクエストをキャシュすることを防ぐ
  var url = "Manage.do?difficulty=" +
    escape(my.title) +"&timestamp=" + (timestamp*1);
  request.open("GET", url, true);
  request.onreadystatechange = showQuestion;
  request.send(null);
  document.getElementById("loginArea").style.display = "none";
  document.getElementById("chooser").style.display = "none";

  //  make sound button visible and set plaｙSound 
  //  function for onclick
  //  スピーカのボタンをvisibleにして、playSoundの関数を
  //  onclickにする。
  var soundBtnDiv = 
	  document.getElementById("sndButton");
  soundBtnDiv.style.display = "";
  soundBtnDiv.onclick = playSound;
}

//テストの問題（絵、サウンドファイル、インデクス）のコールバック関数
function showQuestion() {
  var testDiv = document.getElementById("test"); 
  if(request.readyState == 4) {
	if (request.status == 200) {
	// JSONレスポンスを構文解析する
	// JSONレスポンスのアレイ０から３は問題のイメージ
	  JSONResponse = eval('(' + request.responseText + ')');
//	  alert("in showQuestion\nnumber of questions: " + JSONResponse.length);
	  for(var i=0; i<JSONResponse.length; i++) {
		var JSONQuestion = JSONResponse[i];
		var qnDivName = "qnDiv" + i;
		var currentQnDiv = document.createElement("div");
		var qnDivId = document.createAttribute("id");
		qnDivId.nodeValue = qnDivName;
		currentQnDiv.setAttributeNode(qnDivId);
		
 	  // create and place first image
	  // imgエレメントを作ってページに設置する
	    var pic0 = document.createElement("img");
	    pic0.setAttribute("src", "img/" + JSONQuestion[0] + ".png");
	    pic0.setAttribute("title", '0');
	    currentQnDiv.appendChild(pic0);
	  
	  // create and place second image
	    var pic1 = document.createElement("img");
	    pic1.setAttribute("src", "img/" + JSONQuestion[1] + ".png");
	    pic1.setAttribute("title", '1');
	    currentQnDiv.appendChild(pic1);
 
	  // create and place third image
	    var pic2 = document.createElement("img");
	    pic2.setAttribute("src", "img/" + JSONQuestion[2] + ".png");
	    pic2.setAttribute("title", '2');
	    currentQnDiv.appendChild(pic2);

	  // create and place fourth image
	    var pic3 = document.createElement("img");
	    pic3.setAttribute("src", "img/" + JSONQuestion[3] + ".png");
	    pic3.setAttribute("title", '3');
	    currentQnDiv.appendChild(pic3);

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
  	    currentPic.onclick = checkMe;
  	    currentPic.className = "spacing";
	  }
      
	  // create soundManager sounds for correct answers.
	  // soundManagerの正しい答えのオブジェクトを作成する
      var soundNames = new Array();
      for(var i=0; i<JSONResponse.length; i++){
    	soundNames[i] = getWord(JSONResponse[i][4]) + i;
        var questionSound = soundManager.createSound({
          id: soundNames[i],
          url: 'sound/' + JSONResponse[i][4] + '.mp3',
          autoload: true,
          autoplay: false,
          volume: 170
        });
        questionSounds[i] = questionSound;
      }
            
      //  create sounds for correct and incorrect
      //  正解と不正解の音のオブジェクトを作成する。
      testSounds[0] = soundManager.createSound({
    	id: 'correct',
    	url: 'sound/correct.mp3',
    	autoload: true,
    	autoplay: false
      });
      testSounds[1] = soundManager.createSound({
      	id: 'incorrect',
      	url: 'sound/incorrect.mp3',
      	autoload: true,
      	autoplay: false,
      	volume: 70
        });
                       
	  delete request;
	}
  }
}


//次の問題を示すAjax関数。
//最後の問題だったら、フォームをサブミットする。
function nextQn(e) {
//  alert("in nextQn");
  if(currentIndex==4){ //最後の問題だから、サブミットする。
//	alert("answers\n" + answers.toString());
//	alert("nextQn.\nin nextQn if clause.\ncurrentIndex: " + currentIndex);
	JSONResponse = null;
	soundNames = null;
	var JSONAnswers = JSON.stringify(answers);
	var timestamp = new Date();
	document.forms['testForm'].action = "Manage.do?answered=" +
	  escape(JSONAnswers) + "&timestamp=" + (timestamp*1);
	document.forms['testForm'].submit();
  } else { 
	var testDiv = document.getElementById("test");
	testDiv.removeChild(testDiv.firstChild);
	var newQn = testDiv.firstChild;
	newQn.style.display = "";
    var images = newQn.getElementsByTagName("img");
    for(var i=0; i<images.length; i++){
  	  var currentPic = images[i];
  	  currentPic.onclick = checkMe;
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
  var soundWord = getWord(JSONResponse[currentIndex][4]);
//  alert("**checkMe**. \nclickedWord: " + clickedWord + "\nsoundWord: " + soundWord);
  setTimeout(function(){;nextQn(my); my = null; // サウンドファイルを再生するのに、1秒待つ
  }, 1000);
  if (soundWord==clickedWord){
	testSounds[0].play(); //正解サウンドファイル
  } else {
	testSounds[1].play();  // 不正解サウンドファイル
  }
}

function validateForm() {
//  alert("validateForm called");
  if (validateFlag){	
    if (nameEmpty(userName,"メールアドレスを入力してください")==false){
	  userName.focus();
	  return false;
    }
    if (nameEmpty(password,"パースワードを入力してください")==false){
  	  password.focus();
  	  return false;
    }
  }
}

//ログイン入力を構文解析、ヌルや空のフィールドだったら、エラーが発生する
function nameEmpty(field, alertText) {
	  var check = field;
	  if ((check.value.length==0)||
				(check.value==null)){
		alert(alertText);
	    return false;
	  }
    return true;
}