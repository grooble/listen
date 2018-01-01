window.onload = initPage;

var validateFlag = true;
var password = null;

function initPage() {
  //alert("initPage");
  document.getElementById("loginArea").style.display = "";
  password = document.getElementById("userPwd");
}


function validateForm() {
  alert("validateForm called");
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
  return true;
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