window.console = window.console || {log:function(){}};

var status = "";
var statusDiv;
var uploaderDiv;

$(document).ready(function() {
	//alert("testviewer-02-4 ready");
	var statusBtn = document.getElementById("status-submit");
	statusBtn.onclick = updateStatus;
	/* this block detects the enter key in the statustext */
	/* input field and calls updateStatus.                */
	 $('#statustext').live('keypress',function(e){
	     var p = e.which;
	     if(p==13){
	         updateStatus();
	     }
	 });
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

function showStatus(data){
	var jsondata = $.parseJSON(data);
    console.log('   JSON[0]: ', jsondata[0]);
	
	$.each(jsondata, function(i, item){
		//format status according to content
		var statusText = "";
		
		//prepare date format
		var d = item[1].split(/[- :]/);
		var t = new Date();
		var display_date;
		var item_date = new Date(d[0], d[1]-1, d[2], d[3], d[4], d[5]);
		if ((t.getFullYear() == item_date.getFullYear()) && 
				(t.getMonth() == item_date.getMonth()) &&
				(t.getDate() == item_date.getDate()))
		  {
			display_date = "at " + item_date.getHours() + 
			    ":" + item_date.getMinutes();
		  }
		else{
			display_date = "on " + item_date.toDateString();
		}
		
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
        var newsli = statusimg.wrap($('<li>', {"class": "row", "style": "display:none;"}))
            .closest('li')
            .prepend($('<div>', {"class": "col-md-9"})
              .prepend($('<div>', {"class": "row"})
                .append($('<div>', {"class": "col-md-12 st-status"})
            	  .append(statusText)
            	)
              )
              .prepend($('<div>', {"class": "row"})
                .append($('<div>', {"class": "col-md-12 filler"}))
              )
              .append($('<div>', {"class": "row"})
        	    .append($('<div>', {"class": "col-md-6 smaller"})
        		  .append(display_date)
        		)
	            .append($('<div>', {"class": "col-md-6 small"})
	  	          .append("TO DO")
				)
			  )
			)
			.append($('<div>', {"class": "clear"})
            );
            newsli.find('img')
            .wrap($('<div>', {"class": "col-md-3"}))
            .wrap($('<div>', {"class": "row", id: "one"})
              .append($('<div>', {"class": "col-md-12 st-status"})
            	.append($('<a>', {href: "FView.do?friendId=" + item[6]}))
              )
            )
            .closest(".col-md-3")
            .append($('<div>', {"class": "row", id: "two"})
              .append($('<div>', {"class": "col-md-12 small"})
                .append(item[2])
              )
            )
            ;
            
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

function updateStatus(){
	status = $("#statustext").val();
	//alert("status: " + status);
	if (checkWhiteSpace(status)){
		$.ajax({
			cache: false,
			type: "POST",
			url: 'StatusUpdate.do?status=' + status,
			contentType: "application/json; charset=UTF-8",
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
	//alert("myFormSubmitter");
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

$(function(){
    $(window).resize(function() {    // Optional: if you want to detect when the window is resized;
        processBodies($(window).width());
    });
    function processBodies(width) {
    	//alert("ww: " + $(window).width());
        if(width > 768) {
            $('.panel-collapse').collapse('show');
        }
        else {
            $('.panel-collapse').collapse('hide');
        }
    }
    processBodies($(window).width());
});