function apply(){
	var flag = checkNull();
	if(flag){
		var account = $("#account").attr("value");
		var reason = $("#reason").attr("value");
		$.post('/ipintu/pintuapi', {
			'method'  : 'apply',
			'account'	: account,
			'reason' : reason
		}, 
		//回调函数
		function (result) {
			  $('#report').html(result);
		});
	}else{
		$('#prompt').html('<font  color="red">*</font>');
	}
}

function validate(){
	var flag = checkEmail();
	if(flag){
		$('#prompt').show().html('<img src="img/loading.gif">');
		var account = $("#account").attr("value");
		$.post('/ipintu/pintuapi', {
			'method'  : 'validate',
			'account'	: account
		}, 
		//回调函数
		function (result) {
			if(result == 1){//result为1，用户已存在
			    $('#prompt').html('<img src="img/no.png">');
			    $("#account").val("");
			}else{
				 $('#prompt').html('<img src="img/ok.png">');
			}
		});
	}else{
		$('#prompt').html('<font  color="red">*</font>');
	}
}

function checkNull(){
		var account = $("#account").attr("value");
		var reason =$("#reason").attr("value");
		if(account == null || reason == null || account == "" || reason == ""){
			return false;
		}
		return true;
	}

	
function checkEmail(){
	var emailStr = $("#account").attr("value");
	var emailPat = new RegExp(/^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/g);
	if (!emailPat.test(emailStr)) {
		return false;
	}
	return true;
}


function checkBlank(){
	var account = $("#account").attr("value");
	var password =  $("#password").attr("value");
	var inviteCode = $("#inviteCode").attr("value");
	var nickName = $("#nickName").attr("value");
	if(account == null || password == null || inviteCode==null ||nickName == null ||
			nickName ==""|| account == "" || password == "" || inviteCode=="" ){
		return false;
	}
	return true;
}



function register(){
	var flag = checkBlank();
	if(flag){
		var account = $("#account").attr("value");
		var password =  $("#password").attr("value");
		var nickName = $("#nickName").attr("value");
		var inviteCode = $("#inviteCode").attr("value");
	
		$.post('/ipintu/pintuapi', {
			'method'  : 'register',
			'account'	: account,
			'password' : password,
			'nickName' : nickName,
			'inviteCode' : inviteCode
			
		}, 
		//回调函数
		function (result) {
			  $('#report').html(result);
		});
	}
}

function checkNickname(){
	var nick = $("#nickName").attr("value");
	if(nick==null || nick==""){
		return false;
	}
	return true;
}

function examine(){
	var flag = checkNickname();
	if(flag){
		$('#prompt').show().html('<img src="img/loading.gif">');
		var nickName = $("#nickName").attr("value");
		$.post('/ipintu/pintuapi', {
			'method'  : 'examine',
			'nickName'	: nickName
		}, 
		//回调函数
		function (result) {
			if(result == 1){//result为1，用户已存在
			    $('#prompt').html('<img src="img/no.png">');
			}else if(result == 0){
				$('#prompt').html('<img src="img/ok.png">');
			}
		});
	}else{
		$('#prompt').html('<font  color="red">*</font>');
	}
}

function checkLength(){
	var pwd =  $("#password").attr("value");
	if(pwd.length <6 || pwd.length>8){
		  $('#prompt2').show().html('*密码长度6~8位');
		  $("#password").val("");
	}else{
		 $('#prompt2').hide();
	}
}

function checkAccount(){
	var emailStr = $("#account").attr("value");
	var emailPat = new RegExp(/^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/g);
	if (!emailPat.test(emailStr)) {
		 $('#prompt1').show().html('*邮箱格式不正确');
		 $("#account").val("");
		 return false;
	}else{
		$('#prompt1').hide();
		return true;
	}
}

function checkReg(){
	var flag = checkAccount();
	if(flag){
		$('#prompt').show().html('<img src="img/loading.gif">');
		var account = $("#account").attr("value");
		$.post('/ipintu/pintuapi', {
			'method'  : 'validate',
			'account'	: account
		}, 
		//回调函数
		function (result) {
			if(result == 1){//result为1，用户已存在
			    $('#prpt').html('<img src="img/no.png">');
			    $("#account").val("");
			}else{
				 $('#prpt').html('<img src="img/ok.png">');
			}
		});
	}else{
		$('#prpt').html('<font  color="red">*</font>');
	}
}


function request(paras)
{ 
    var url = location.href; 
    var paraString = url.substring(url.indexOf("?")+1,url.length).split("&"); 
    var paraObj = {};
    for (var i=0; j=paraString[i]; i++){ 
    	paraObj[j.substring(0,j.indexOf("=")).toLowerCase()] = j.substring(j.indexOf("=")+1,j.length); 
    } 
    var returnValue = paraObj[paras.toLowerCase()]; 
    if(typeof(returnValue)=="undefined"){ 
    	return ""; 
    }else{ 
    	return returnValue; 
    } 
}

function init(){
	var account =request("account"); 
	var inviteCode = request("inviteCode");
	$("#account").val(account);
	$("#inviteCode").val(inviteCode);
}

function back(){
    history.go(-1);
 }

