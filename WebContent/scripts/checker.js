window.onload = readyPage;

var currentIndex = 0;
var JSONResponse = new Array();
var testSounds = new Array(2);
var questionSounds = new Array();
var audioHtmlTag;
var navIcons;
var soundNames = new Array();
var oggUser = ["sound/ogg/", ".ogg"];
var mp3User = ["sound/mp3/", ".mp3"];

function readyPage() {	
	  //alert("readyPage");
	audioHtmlTag = "uninitialized";
	 
	var audio = new Audio();
	var canPlayOgg = !!audio.canPlayType && audio.canPlayType('audio/ogg; codecs="vorbis"') != "";
	if(canPlayOgg){
		audioHtmlTag = oggUser;
	} else {
		audioHtmlTag = mp3User;
	}

	//alert("can play: " + canPlayOgg);
	
	navIcons = $(':button').show();
	navIcons.click(function(){
		navigate(this.id);
	});
	
	$('#spinner').hide();
	
	doRequest();
}


function doRequest() {
	$.ajax({
		dataType: "json",
		cache: false,
		url: 'GetRevJSON.do',
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
}

//テストの問題（絵、サウンドファイル、インデクス）のコールバック関数
function showQuestion(data) {
	JSONResponse = data;
	var testDiv = document.getElementById("test"); 

	$.each(JSONResponse, function(i,item){

		//create div for four pics and set id
		//alert("i: " + i);
		var currentQnJQ = $("<div/>", {"class": "questionContainer row-fluid", id: "qnDiv" + i});
		var currentQnDiv = currentQnJQ[0];

		//add 'questionContainer' class to all question divs for layout purposes
		
		// create and place first image
		// create sound and set to play on clicked image
		// imgエレメントを作ってページに設置する
		var pic0DivJQ = $("<div/>", {"class": "col-md-3 col-sm-6 col-xs-12 questionOneDiv",	id: "Qn" + i + "Sel0"});
		var pic0Div = pic0DivJQ[0];
		
		var pic0JQ = $('<img/>',{'src': "img/" + item[0] + ".png", 'title': '0',
			"class": "img-responsive center-block"});
		var pic0 = pic0JQ[0];			
		currentQnDiv.appendChild(pic0Div);
		pic0Div.appendChild(pic0);
		
		var sound0 = new Audio(audioHtmlTag[0] + item[0] + audioHtmlTag[1]);
		pic0JQ.click(function (){
			sound0.play();
		});
		pic0Div.appendChild(sound0);
		
		// create and place second image
		var pic1DivJQ = $("<div/>", {"class": "col-md-3 col-sm-6 col-xs-12 questionTwoDiv",	id: "Qn" + i + "Sel1"});
		var pic1Div = pic1DivJQ[0];
		
		var pic1JQ = $('<img/>',{'src': "img/" + item[1] + ".png", 'title': '1',
			"class": "img-responsive center-block"});
		var pic1 = pic1JQ[0];
		currentQnDiv.appendChild(pic1Div);
		pic1Div.appendChild(pic1);
		
		var sound1 = new Audio(audioHtmlTag[0] + item[1] + audioHtmlTag[1]);
		pic1JQ.click(function (){
			sound1.play();
		});
		pic0Div.appendChild(sound1);

		// create and place third image
		var pic2DivJQ = $("<div/>", {"class": "col-md-3 col-sm-6 col-xs-12 questionThreeDiv", id: "Qn" + i + "Sel2"});
		var pic2Div = pic2DivJQ[0];
		
		var pic2JQ = $('<img/>',{'src': "img/" + item[2] + ".png", 'title': '2',
			"class": "img-responsive center-block"});
		var pic2 = pic2JQ[0];			
		currentQnDiv.appendChild(pic2Div);
		pic2Div.appendChild(pic2);
		
		var sound2 = new Audio(audioHtmlTag[0] + item[2] + audioHtmlTag[1]);
		pic2JQ.click(function (){
			sound2.play();
		});
		pic2Div.appendChild(sound2);
		
		// create and place fourth image
		var pic3DivJQ = $("<div/>", {"class": "col-md-3 col-sm-6 col-xs-12 questionFourDiv", id: "Qn" + i + "Sel3"});
		var pic3Div = pic3DivJQ[0];
		
		var pic3JQ = $('<img/>',{'src': "img/" + item[3] + ".png", 'title': '3',
			"class": "img-responsive center-block"});
		var pic3 = pic3JQ[0];			
		currentQnDiv.appendChild(pic3Div);
		pic3Div.appendChild(pic3);
		
		var sound3 = new Audio(audioHtmlTag[0] + item[3] + audioHtmlTag[1]);
		pic3JQ.click(function (){
			sound3.play();
		});
		pic3Div.appendChild(sound3);
		
		
		currentQnDiv.style.display = "none"; //don't display created qn
		testDiv.appendChild(currentQnDiv);	   
		if(i==JSONResponse.length-1){
//			alert("activated if");
			$('#spinner').hide();
			$('#test').show();
		}

	});

	//  set onclick property for new pics
	//  前設置したimgにonclick関数をつける
	//  locate and display 1st question which is called "qnDiv0" 
	
	$("#qnDiv0").show();
}

function nextQn(e) {
    //alert("in nextQn, currentIndex: " + currentIndex);	
	if (currentIndex > 0){
		navIcons.show();
	}
	if (currentIndex < JSONResponse.length-1){
		navIcons.show();
	}
	if(currentIndex==JSONResponse.length-1){
        navIcons[1].style.display = "none";
	}
	if(currentIndex==0){
        navIcons[0].style.display = "none";
	}
	
	$("#qnDiv" + currentIndex).show();
}

function navigate(id){
	//alert("navigate: " + id + "\ncurrent index: " + currentIndex + "\nJSON length: " + JSONResponse.length);
	  if ((id=="btn-back")&&(currentIndex>0)){
		  var qnName = "qnDiv" + currentIndex;
		  var oldQuestion = document.getElementById(qnName);
		  oldQuestion.style.display = "none";
		  currentIndex--;
	  }
	  if ((id=="btn-next")&&(currentIndex<JSONResponse.length-1)){
		  var qnName = "qnDiv" + currentIndex;
		  var oldQuestion = document.getElementById(qnName);
		  oldQuestion.style.display = "none";
		  currentIndex++;
	  }
	  nextQn();
}