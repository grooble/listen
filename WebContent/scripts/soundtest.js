var track;
var tracks = ["cat"];
var audioarray = [2];

function addHTMLAudio() {
    track = new Audio("sound/ogg/animal/" + tracks[0] + ".ogg");
    track.id = 'cat';
    track.autoplay = false;
    track.preload = false;
    audioarray[0] = track;
    //document.getElementById('sound').appendChild(track);
    $('#test').prepend($('<img>',{src: 'img/animal/' + tracks[0] + '.gif', id: tracks[0]}));
    //alert(audioarray[0].id);
    audioarray[0].play();
    setTimeout(function(){ audioarray[0].pause();},100);
}

$(function() {
  $('#sndButtonDiv').on('click', function(e){
	addHTMLAudio();
  });
});
  
$(function() {
  $('#test').on('click', function(e){
    audioarray[0].play();	  
  });
});