window.console = window.console || {log:function(){}};

$(document).ready(function() {
    $(".picContainer .cbox").hide();
    $(".picContainer img").click(function(){
      var $checkbox = $(this).parent().find(".cbox");
      $checkbox.attr('checked', !$checkbox.attr('checked'));
      if($checkbox.attr('checked')) {
    	$(this).css('border', 'solid 3px grey');      	
      } else {
    	$(this).css('border', 'none');      	
      }
//       var $checkbox = $(this).parent().find(".cbox");
    });
    /*
*/
});