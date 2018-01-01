window.console = window.console || {log:function(){}};

var status = "";
var statusDiv;
var uploaderDiv;

$(document).ready(function() {
	//alert("ready");
	var statusBtn = document.getElementById("status-submit");
	uploaderDiv = document.getElementById("pic-upload");
	uploaderDiv.style.display = "none";
	statusDiv = document.getElementById("status_text_div");
	statusBtn.onclick = updateStatus;
	var text = document.getElementById("statustext");
	addEventHandler(text, "keydown", checkEnter);
	addEventHandler(text, "keyup", sz);
	addEventHandler(text, "click", selector);
	loadStatus();
});

function loadStatus(){
    //alert("loadStatus");
	$.ajax({
		cache: false,
		type: "POST",
		url: 'StatusLoader.do',
		contentType: "application/json; charset=utf-8",
		success: function(data){
		$('#doNews').empty();
		$('#doNews').append($('<ul/>', {"class": "newsList", id: "theNews"}));
		$("#statustext").attr('rows',1);
		showStatus(data);
	}
	});
}

function updateStatus(){
	status = $("#statustext").val();
	//alert("status: " + status);
	if (checkWhiteSpace(status)){
		$.ajax({
			cache: false,
			type: "POST",
			url: 'StatusUpdate.do?status=' + status,
			contentType: "application/json; charset=utf-8",
			success: function(data){
			$('#doNews').empty();
			$('#doNews').append($('<ul/>', {"class": "newsList", id: "theNews"}));
			$("#statustext").attr('rows',1);
			showStatus(data);
			}
		});
	}else {
		document.getElementById("statustext").value = "";
		document.getElementById("statustext").rows = 1;
	}
}

function showStatus(data){
	var jsondata = $.parseJSON(data);
    console.log('   JSON[0]: ', jsondata[0]);
	
	$.each(jsondata, function(i, item){
		//format status according to content
		var statusText = "";
		if(item[4]=="comment"){  // normal comment
			var statusTextTemp = item[5];
			statusText = replaceURLWithHTMLLinks(statusTextTemp);
		}
		else if(item[4]=="test"){  // status generated after test
			statusText = item[2] + " took a test: " + item[5];
		}
		else {  //unknown status type
			statusText = "unknown status type: " + item[5];
		}
		
		var statusimg = $('<img src="' + showpic(item[3]) + '" class="newsImage">');
        var newsli = statusimg.wrap($('<li>', {"class": "newsItem", "style": "display:none;"}))
            .closest('li')
            .prepend($('<div>', {"class": "st-col1"})
              .append($('<div>', {"class": "st-status"})
            	.append(statusText))
        	  .append($('<div>', {"class": "st-name"})
        		.append(item[2]))
	          .append($('<div>', {"class": "st-date"})
	  	        .append(item[1])))
	  	    .append($('<div>', {"class": "clear"})
            );
            newsli.find('img')
            .wrap($('<div>', {"class": "st-col2"}))
            .wrap($('<a>', {href: "FView.do?friendId=" + item[6]}));
            

            newsli.children().wrapAll($('<div>', {"class": "newsWrapper"}));
	        newsli.appendTo($('#theNews'));

        statusimg.load(function(){
        	console.log('  loaded', $(this).attr('src'));
        	$(this).closest('li').show();
        });	
        
        newsli.delay(5000).show(0);
   	});
	

	$("#statustext").val('');
}

// --------------------------------------
function moreStatus(){	
	$.ajax({
		cache: false,
		type: "POST",
		url: 'GetMore.do?timestamp=1',
		contentType: "application/json; charset=utf-8",
		success: function(data){
			showStatus(data);
		}
	});	
}

function hideStatus(divid, stid){
	if(confirm("delete divId: " + divid + "\nstid: " + stid))
	$("#" + divid).hide(400);
}

function showUploader(){
	uploaderDiv.style.display = "";
}

function hideUploader(){
	uploaderDiv.style.display = "none";
}

function myFormSubmitter(param) {
    var method = "post"; 
    var action = "ReviewTest.do";
    var form = document.createElement("form");
    form.setAttribute("method", method);
    form.setAttribute("action", action);

    var hiddenField = document.createElement("input");
    hiddenField.setAttribute("type", "hidden");
    hiddenField.setAttribute("name", "testid");
    hiddenField.setAttribute("value", param);
    
    form.appendChild(hiddenField);

    document.body.appendChild(form);
    form.submit();
}

function checkEnter(evt){
//	alert("testviewer-->checkEnter");
	var statusArea = getActivatedObject(evt);
	var keyCode = evt ? (evt.which ? evt.which : evt.keyCode) : event.keyCode;
	if (keyCode == 13){
		document.getElementById('status-submit').click();
		statusArea.rows=1;
	}
}
	
// to resize the status textarea 
function sz(e) {
		var t = getActivatedObject(e);
//		alert("testviewer-->sz(t)");
		a = t.value.split('\n');
		b=1;
		for (var x=0;x < a.length; x++) { 
			if (a[x].length >= t.cols) {
				b+= Math.floor(a[x].length/t.cols);
			}
		}
		b+= a.length;
		if (b > t.rows) t.rows = b;
}

function selector(e){
//	alert("in selector");
	var sel = getActivatedObject(e);
	sel.style.color = "#000066";
	sel.focus();
	sel.select();
}

function showpic(picstring){
//  utility to return path to small pic from 
//	either url or file path
//  ** the http part hasn't really been tested yet **
	if (picstring.charAt(0) == "h"){
		//alert("showpic is url" + "\nurl: " + picstring);
		var sizeless = picstring.substring(0, picstring.length -7);
		var smallpic = sizeless + "-small";
		return smallpic;
	}
	else {
		var patharray = picstring.split('/');
		patharray.splice(3,0,"thumb");
		var thumbpath = patharray.join('/');
		return thumbpath;
		//alert("showpic is path" + "\npath: " + picstring +
		//		"\n\nto thumb: " + thumbpath);
	}	
}