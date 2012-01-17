document.onkeydown = function (e) { 
	var theEvent = window.event || e; 
	var code = theEvent.keyCode || theEvent.which; 
	if (code == 13) { 
		$("#submit").click(); 
	} 
} 

function checkNull(){
	var account = $("#account").attr("value");
	var password =  $("#password").attr("value");
	if(account == null || password == null || account == "" || password == ""){
		return false;
	}
	return true;
}

function check(){
	var flag = checkNull();
	if(flag){
		var account = $("#account").attr("value");
		var password =  $("#password").attr("value");
		$.post('/ipintu/pintuapi', {
			'method'  : 'logon',
			'account'	: account,
			'password' : password 
		}, 
		//回调函数
		function (result) {
			var res =result.trim();
			var userId="";
			if(res.indexOf('admin')>-1 || res.indexOf('service')> -1){//若result为管理员
				//转到管理员页面admin.jsp
				res = res.split("@");
				userId = res[1];
				window.location.replace("../jsp/admin.jsp?userId="+userId);
			}else if( res == "0" ){
				 $('#prompt').show().html('*密码不正确');
				 $("#password").val("");
			}else if( res == "-1"){
				 $('#prompt').show().html('*用户不存在，请申请注册');
				 $("#account").val("");
				 $("#password").val("");
			}else{
				//转到普通用户页面normal.jsp
				res = res.split("@");
				userId = res[1];
				var url = "normal.html?userId="+userId;
				eb(url);
			}
		});
	}
}


function checkForm(){
	var pwd = $("#oldpwd").attr("value");
	var pwd1 =  $("#pwd1").attr("value");
	var pwd2 =  $("#pwd2").attr("value");
	if(pwd == null || pwd=="" || pwd1 == null || pwd1=="" || pwd2 == null || pwd2==""){
		return false;
	}else{
		return true;
	}
}

function checkOld(){
	var pwd = $("#oldpwd").attr("value");
	if(pwd == null || pwd==""){
		return false;
	}else{
		return true;
	}
}

function confirm(){
	var flag = checkOld();
	if(flag){
		$('#prompt').attr("style","visibility:visible");
		var oldpwd = $("#oldpwd").attr("value");
		var userId =getPara("userId"); 
		$.post('/ipintu/pintuapi', {
			'method'  : 'confirm',
			'userId'	: userId,
			'password' : oldpwd
		}, 
		//回调函数
		function (result) {
			if(result == 0){//result为0，原密码正确
			     $('#prompt').html('<img src="img/no.png">');
			}else if(result == 1){
				 $('#prompt').html('<img src="img/ok.png">');
			}
		});
	}else{
		$('#prompt').attr("style","visibility:hidden");
	}
}

function checkLength(){
	var pwd =  $("#pwd1").attr("value");
	if(pwd.length <6 || pwd.length>8){
		 $('#prompt2').show().html('*密码长度6~8位');
	}else{
		 $('#prompt2').hide();
	}
}

function compare(){
	var pwd1 =  $("#pwd1").attr("value");
	var pwd2 =  $("#pwd2").attr("value");
	if(pwd1 == pwd2){
		 $('#prompt2').hide();
	}else{
		$('#prompt2').show().html('*两次输入的密码不一致');
		$('#pwd1').val('');
		$('#pwd2').val('');
	}
}

function changePwd(){
	var flag = checkForm();
	if(flag){
		$('#subm').attr("style","visibility:visible");
		var userId = $("#userId").attr("value");
		var password =  $("#pwd1").attr("value");
		$.post('/ipintu/pintuapi', {
			'method'  : 'modifyPassword',
			'userId'	: userId,
			'newPwd' : password
		}, 
		//回调函数
		function (result) {
			$('#subm').attr("style","visibility:hidden");
			if(result.trim() == 'Operate Success!'){
				  $('#report').html('*密码修改成功');
			  }else if(result.trim() == 'Operate Failed!'){
				  $('#report').html('*修改失败');
			  }else{
			  	  $('#report').html('*请先登录，再修改密码');
			  }
			$('#submit').attr("disabled","true");
		});
	}
}

//加密代码
function eb(ting)
  {
	//传入要加密的url
	//获取url参数部分
   var paraString = ting.substring(ting.indexOf("?")+1,ting.length); 
    //将加密后的参数重新组合到url中
   var rul=ting.substring(0,ting.indexOf("?")+1)+escape(paraString);
 	//跳转到rul页面呢 
  	location.href=rul;
  }
  
 //解密代码
//分析参数 
function getPara(paraName){ 
	var urlPara = location.search; //获取参数部分
	urlPara=unescape(urlPara);//对参数解密
	var reg = new RegExp("[&|?]"+paraName+"=([^&$]*)", "gi"); 
	var a = reg.test(urlPara); 
	return a ? RegExp.$1 : ""; 
} 

function init(){
	var userId =getPara("userId"); 
	$("#userId").attr("value",userId);
}

//密码重置
function checkEmail(){
	var emailStr = $("#account").attr("value");
	var emailPat = new RegExp(/^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/g);
	if (!emailPat.test(emailStr)) {
		$('#report').html('*邮箱格式不正确');
	}
}

function retrievePwd(){
		$('#subm').attr("style","visibility:visible");
		var account = $("#account").attr("value");
		$.post('/ipintu/pintuapi', {
			'method'  : 'retrieve',
			'account' : account
		}, 
		//回调函数
		function (result) {
			 $('#subm').attr("style","visibility:hidden");
			  if(result.trim() == 'Operate Success!'){
				  $('#report').html('*重置正在处理中，请于10分钟内查看您的邮箱！');
			  }else{
				  $('#report').html('*密码重置失败，请重试。');
			  }
			  $('#submit').attr("disabled","true");
		});
}