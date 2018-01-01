$(function() {
  //alert("document rready");
  $('#chooser li').on('click', function(e){
	var img_title = $('img', this).attr('title');
    //alert("image title: " + img_title);
	getTest("difficulty=" + img_title + "&category=random");
  });
  $('#selectTestLink a')
    .on('mouseover', function(e){
    	var link_title = $(this).attr('title');
    	//alert("link: " + link_title);
    	$('#titleimage').empty();
	    $('<img />')
	    .attr({ 
	    	'src': 'img/title-pics/index-' + link_title + '.jpg', 
	    	'title':link_title,
	    	'width': '180' 
	    })
	    .appendTo($('#titleimage'));
    })
    .on('mouseout', function(e){
    	$('#titleimage').empty();
    	$('<img />')
	    .attr({ 'src': 'img/title-pics/index-default.jpg', 'title':'default'})
	    .appendTo($('#titleimage'));
    });
  $('#helper img')
    .on('mouseover', function(e){
		var img_title = $(this).attr('title');
	    $('<img />')
	    .attr({ 'src': 'img/titles/help/help-' + img_title + '.gif', 'title':img_title})
	    .appendTo($('#helpText'));
    })
    .on('mouseout', function(e){
	    $('#helpText').empty();
  });
  $('#selectTestLink a').on('click', function(e){
		var img_title = $(this).attr('title');
	    //alert("link title: " + img_title);
		getTest("category=" + img_title +
			"&difficulty=any");
		});  
});

function getTest(t){
  //alert("getTest:" + t);
  document.forms['testForm'].action = "Manage.do?" + t;
  document.testForm.submit();
}