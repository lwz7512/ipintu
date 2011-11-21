<%@ page language="java" import="java.util.*"
	contentType="text/html; charset=UTF-8"%>
<%@include file="header.jsp"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>Logon page</title>
<link rel="stylesheet" type="text/css"
	href="<%=request.getContextPath()%>/jsp/css/style.css" />
<script language=javascript
	src="<%=request.getContextPath()%>/jsp/js/jquery.js"
	type=text/javascript></script>
</head>

<script language=javascript type=text/javascript>

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
		$.post('<%=request.getContextPath()%>/pintuapi', {
			'method'  : 'logon',
			'account'	: account,
			'password' : password 
		}, 
		//回调函数
		function (result) {
			var res =result.trim();
			res = res.split("@");
			var userId= res[1];
			if(res.indexOf('admin') > -1){//若result为管理员
				//转到管理员页面admin.jsp
				window.location.replace("<%=request.getContextPath()%>/jsp/admin.jsp?userId="+userId);
			}else if( res == "0" ){
				 $('#prompt').show().html('*密码不正确');
				 $("#password").val("");
			}else if( res == "-1"){
				 $('#prompt').show().html('*用户不存在，请申请注册');
				 $("#account").val("");
				 $("#password").val("");
			}else{
				//转到普通用户页面normal.jsp
				window.location.replace("<%=request.getContextPath()%>/jsp/normal.jsp?userId="+userId);
			}
		});
	}
}

</script>
<body  onload="document.getElementById('account').focus()">
<div id="contact-form"> 
<form id="contact" method="post"
	action="<%=request.getContextPath()%>/pintuapi">
	<fieldset>
		<label for="header" class="header">登录</label>
		<input type="hidden" name="method" value="logon" />
		<label for="email">账户</label>
			  <input type="text" name="account" id="account" />
			  <span><font  color="red">*</font></span>
		<label for="pwd">密码</label>
			  <input type="password" name="password" id="password" />
			    <span><font  color="red">*</font></span>
		<label for="prompt" class="error">
			<span style="display: none;" id="prompt"></span>
		</label>
		
		<p class="loginBtn">
			<input type="button" name="submit" class="button" id="submit" value="登录" onclick="check()" />
		</p>
	</fieldset>
</form>
</div>
</body>
<%@include file="footer.jsp"%>
</html>