window.onload = startup;

function startup() {
  //alert("startup2");
  $("#startBtn").click(getImage);
  $('#spinner').hide();
  $('#spinOnBtn').click(spinnerOn);
  $('#spinOffBtn').click(spinnerOff);
  $('#spinTimedBtn').click(spinnerTimer);
}
function getImage(){
//alert("in getImage")
$('#loadHere').hide();
$('#spinner').show();
$.getJSON("http://api.flickr.com/services/feeds/photos_public.gne?jsoncallback=?",{tags: "wild",tagmode: "any", format: "json" },
  function(data) {
    $.each(data.items, function(i,item){
         $("<img/>").attr({src : item.media.m.replace('_m.','.')}).appendTo("#loadHere");
         if ( i == 10 ) {
        	 $('#spinner').hide(); 
        	 $('#loadHere').show();
            return false;
        }
        return true;
    });
  });
}

function spinnerOn(){
	$('#spinner').show();
}

function spinnerOff(){
	$('#spinner').hide();
}

function spinnerTimer(){
	$('#spinner').show();
	setTimeout("$('#spinner').hide()", 3000);
}
  