window.onload = pageinit;

var btn;
var pg1;
var exp1;
var mailField;
var jPwd1;
var jPwd2;

function pageinit(){
	//alert("verify.js 1pageinit");
	btn = document.getElementById("joinSubmit");
	btn.disabled = true;
	mailField = document.getElementById("email");
	jPwd1 = document.getElementById("pwd");
	jPwd2 = document.getElementById("pwd-check");
	mailField.focus();
	jPwd1.onfocus = checkerMail;
	jPwd2.onfocus = pwdChecker;
	jPwd2.onkeyup = pwd2Checker;
}

function checkerMail(){
	if((mailField.value == "")||(mailField.length==0)){
		alert("メールアトレスをいれてください");
		setTimeout(mailField.focus(), 10);
	}
//	return false;
}

function pwdChecker(e) {
	var my = getActivatedObject(e);
	//alert("my value: " + mailField.value);
	if((mailField.value == "")||(mailField.length==0)){
		alert("メールアトレスをいれてください");
		setTimeout(mailField.focus(), 10);
	}	
	if(my.name == "pwd2"){
		//alert("pwd2 check in pwdChecker");
		if ((jPwd1.length == 0)||(jPwd1.value.length < 6 || jPwd1.value.length > 8)) {
			alert("アルファベット６文字から８文字のパスワードをいれてください");
			setTimeout(function(){jPwd1.focus();jPwd1.select();}, 10);
		}
		else{
			setTimeout(jPwd2.focus(),10);
		}
	}
}

function pwd2Checker(){
/*	alert("pwd2Checker\njPwd.value = " + jPwd1.value +
			"\njPwd2.value = " + jPwd2.value); */
	if((mailField.value == "")||(mailField.length==0)){
		alert("メールアトレスをいれてください");
		setTimeout(mailField.focus(), 10);
	}
	if(jPwd1.value==jPwd2.value){
		btn.disabled = false;
		setTimeout(btn.focus(), 10);
	} else {
/*		alert("in jPwd2 else clause\njPwd1.value.length = " + jPwd1.value.length
				 + "\njPwd2.value.length = " + jPwd2.value.length); */
		if(jPwd2.value.length >= jPwd1.value.length){
			alert("passwords don't match");
			setTimeout(function(){
				jPwd2.value = '';
				jPwd1.focus();
				jPwd1.select();});
		}
	}
}