window.console = window.console || {log:function(){}};

var result = new Array();
var userID = 0;
var friendID = 0;

$(document).ready(function() { 
	//alert("startup36");
	$('#found').empty();
	$('div#log ul').empty();
	$('#find').click(finder);
});

//テストのレベルを選択するAjax コール
function finder() {
	var email = $('input[type=text]').val();
	//alert("finding: " + email);
	if (email=="")
	{
		alert("email is empty");
	}
	else
	{
	$.ajaxSetup({ cache: false });
	$.getJSON(
		'finder.do?emailText=' + email, 
		function(data) {
		    result = data;
		    var status = data[data.length-1];
		    //alert("got JSON data\n" + data[0] + "\nstatus: " + status);
		    if(status==0){ //already friend or pending invite
		    	//alert("status is 0");
		    	doPended();
		    }
		    if(status==1){
		    	//alert("status: " + status);
		    	showResult(data);
		    }
		    if(status==-1){ //email not member - send email invite
		    	//alert("status is -1");
		    	sendInvite();
		    }
		}
	);
	}
}

function showResult(gotdata){
	$('input[type=text]').val("");

//	var foundDiv = $('.found');

	var foundEmail = gotdata[1];
	userID = gotdata[6];
	var foundFirstName = gotdata[3];
	var foundPic = gotdata[5];
	var foundThumb = foundPic.replace("images", "images/thumb");
	friendID = gotdata[0];
	/* 
	alert("in showResult" +
			"\nfoundThumb: " + foundThumb +
			"\nuser ID: " + userID +
			"\nfriend ID: " + friendID);
	 */

	var foundImg = $('<img src="' + foundThumb + '" >');
    var foundli = foundImg.wrap($('<li>', {"style": "display:none;"}))
    .closest('li')
    .prepend($('<div>', {"class": "col-md-8"})
      .prepend($('<div>', {"class": "row"})
        .append($('<div>', {"class": "col-md-12"})
    	  .append(foundEmail)
    	)
      )
      .append($('<div>', {"class": "row"})
	    .append($('<div>', {"class": "col-md-6"})
		  .append(friendID)
		)
        .append($('<div>', {"class": "col-md-6", "id": "add"})
	        .append('<a href="#">追加</a>')
	          .click(function(){
	        	  pender(friendID);
	          })
	        )
		)
	  );
    
    foundli.find('img')
      .wrap($('<div>', {"class": "col-md-4"}));
    foundli.find('img').parent()
      .append($('<div>', {"class": "row"})
	     .append(foundFirstName)
      );
    
    foundli.appendTo($('.loglist'));
 
    foundImg.load(function(){
    	console.log('  loaded', $(this).attr('src'));
    	$(this).closest('li').show();
    });	
/* ----- */

}

function pender(friendID){
  $.ajax({
	  url: "FAdder.do?friendId=" + friendID})
	  .done(function(msg){
		  $( "#add" ).children( "a:first" ).remove();
		  $( "#add" ).append('<div style="display: none">追加しました</div>');
		  $( "#add" ).children( "div:first" ).fadeIn("slow");
			 
	  })
	  .fail(function() {
		  alert( "error" );
	  });
}

function joiner(email){
	//alert("in joiner\nuserId: " + userID + "\nfriendId: " + friendID);
	/*
	 */
	$.ajaxSetup({ cache: false });
	$.post(
		'PendFriend.do',
		{userId: userID, friendId: friendID}, 
		finalSetup
	);
}

function finalSetup(){
	//alert("finalSetup");
	$('input[type=text]').val("");
	var foundDivJQ = $('.found');
	foundDivJQ.children().fadeOut('slow',function(){		
		foundDivJQ.empty();
	});
	$('<li class="listitem">' + result[1] +	"をしょうたいしました。" + '</li>')	.appendTo($('.log ul'));
	/*
	 */
}

function doPended(){
	//alert("doPended: " + result[0]);
	$('input[type=text]').val("");
//	var logDivJQ = $('#log');
//	logDivJQ.append(result[0] + " はもうしょうたいしたみたい。");
	$('<li class="listitem">' + result[0] +	" 現在の友だちか、もう招待しました。" + '</li>').appendTo($('.log ul'));
}

function sendInvite(){
	//alert("sendInvite");
	$('input[type=text]').val("");
	var inviteDivJQ = $('.found');
	
	email = result[1];
	name = result[0];
	//alert("sendInvite->email: " + email + " inviter: " + name);
	
	var resultDivJQ = $("<div/>", {"class": "resultContainer", id: "resultDiv"});
	inviteDivJQ[0].appendChild(resultDivJQ[0]);
	
	resultDivJQ.text(email + " はまだとうろくしていない。しょうたいしましょうか？");

	var inviteLink = $('<a href="#" class="buttonlook">しょうたいする</a>').appendTo(resultDivJQ);
	inviteLink.click(inviter);
}

function inviter(){
	//alert("in joiner\nuser: " + result[0]+ "\nfriend to invite: " + result[1]);
	$.post(
			'InviteFriend.do',
		{email: result[1], inviterName: result[0]}, 
		finalSetup
	)
	.error(function(){alert("There was a problem.");
	});
}