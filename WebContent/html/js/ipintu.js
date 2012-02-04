function apply(){
	var flag = checkNull();
	if(flag){
		$('#subm').attr("style","visibility:visible");
		var account = $("#account").attr("value");
		var reason = $("#reason").attr("value");
		$.post('/ipintu/pintuapi', {
			'method'  : 'apply',
			'account'	: account,
			'reason' : reason
		}, 
		//回调函数
		function (result) {
			 $('#subm').attr("style","visibility:hidden");
			  if(result == 'Your apply is processing......'){
				  $('#report').html('*申请正在处理中，请于1小时后查看您的邮箱，现在可以先进入社区，体验一下！');
			  }else{
				  $('#report').html('*申请失败，请重试。');
			  }
			  $('#submit').attr("disabled","true");
		});
	}else{
		$('#prompt').html('<font  color="red">*</font>');
	}
}

function validate(){
	var flag = checkEmail();
	if(flag){
		$('#prompt').attr("style","visibility:visible");
		var account = $("#account").attr("value");
		$.post('/ipintu/pintuapi', {
			'method'  : 'checkout',
			'account'	: account
		}, 
		//回调函数
		function (result) {
			if(result == 1){//result为1，用户已存在
			    $('#prompt').html('<img src="img/no.png">');
			    $("#account").val("");
			    $("#account").focus();
			    $("#account").attr("class","inputError");
			    $('#report').html("*此邮箱已注册");
			}else if(result ==0){
				 $('#prompt').html('<img src="img/ok.png">');
				 $("#account").attr("class","input");
				 $('#report').html('');
			}
		});
	}else{
		$('#prompt').attr("style","visibility:hidden");
		$('#report').html("*邮箱格式不正确");
	}
}

function checkNull(){
		var account = $("#account").attr("value");
		var reason =$("#reason").attr("value");
		if(account == null || reason == null || account == "" || reason == ""){
			$('#report').html("*请填写正确的邮箱和理由！");
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
		$('#report').html("*请正确填写注册信息！");
		return false;
	}
	return true;
}

function register(){
	var flag = checkBlank();
	if(flag){
		$('#subm').attr("style","visibility:visible");
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
			$('#subm').attr("style","visibility:hidden");
			if(result.indexOf('@')>-1){
					res = result.split("@");
					userRole = res[0];
					userId = res[1];
				 	cacheUserBrage(userId, userRole);
				  $('#report').html('*注册成功，现在进入社区体验吧！');
			  }else{
				  $('#report').html('*注册失败，请重试。');
			  }
			$('#submit').attr("disabled","true");
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
		$('#prompt').attr("style","visibility:visible");
		var nickName = $("#nickName").attr("value");
		$.post('/ipintu/pintuapi', {
			'method'  : 'examine',
			'nickName'	: nickName
		}, 
		//回调函数
		function (result) {
			if(result == 1){//result为1，用户已存在
			    $('#prompt').html('<img src="img/no.png">');
			    $("#nickName").focus();
			    $("#nickName").attr("class","inputError");
			}else if(result == 0){
				$('#prompt').html('<img src="img/ok.png">');
				$("#nickName").attr("class","input");
			}
		});
	}else{
		$('#prompt').attr("style","visibility:hidden");
	}
}

function checkLength(){
	var pwd =  $("#password").attr("value");
	if(pwd.length <6 || pwd.length>8){
		  $('#prompt2').show().html('*密码长度6~8位');
		  $("#password").val("");
		  $("#password").focus();
		  $("#password").attr("class","inputError");
	}else{
		 $('#prompt2').hide();
		 $("#password").attr("class","input");
	}
}

function checkAccount(){
	var emailStr = $("#account").attr("value");
	var emailPat = new RegExp(/^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/g);
	if (!emailPat.test(emailStr)) {
		 $('#prompt1').show().html('*邮箱格式不正确');
		 $("#account").val("");
		 $("#account").focus();
		 $("#account").attr("class","inputError");
		 return false;
	}else{
		$('#prompt1').hide();
		return true;
	}
}

function checkReg(){
	var flag = checkAccount();
	if(flag){
		$('#prpt').attr("style","visibility:visible");
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
			    $("#account").focus();
			    $("#account").attr("class","inputError");
			    $('#report').html("*此邮箱已注册");
			}else{
				 $('#prpt').html('<img src="img/ok.png">');
				 $("#account").attr("class","input");
				 $('#report').html('');
			}
		});
	}else{
		$('#prpt').attr("style","visibility:hidden");
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
	$("#inviteCode").val(inviteCode);
	if(account !=null && account!=""){
		$("#account").val(account);
	}
}


