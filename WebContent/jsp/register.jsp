<%@ page language="java" import="java.util.*"
	contentType="text/html; charset=UTF-8"%>
<%@include file="header.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>Register page</title>
<link rel="stylesheet" type="text/css"
	href="<%=request.getContextPath()%>/jsp/css/style.css" />
<script language=javascript
	src="<%=request.getContextPath()%>/jsp/js/jquery.js"
	type=text/javascript></script>
</head>

<script language=javascript  type=text/javascript>
function checkNull(){
	var account = $("#account").attr("value");
	var password =  $("#password").attr("value");
	var inviteCode = $("inviteCode").attr("value");
	if(account == null || password == null || inviteCode==null || account == "" || password == "" || inviteCode=="" ){
		return false;
	}
	return true;
}

function checkEmail(){
	var emailStr = $("#account").attr("value");
	var emailPat = new RegExp(/^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/g);
	if (!emailPat.test(emailStr)) {
		 $('#prompt1').show().html('*邮箱格式不正确');
		 $("account").val("");
	}else{
		$('#prompt1').hide();
	}
	return true;
}

function checkLength(){
	var pwd =  $("#password").attr("value");
	if(pwd.length <6 || pwd.length>8){
		  $('#prompt2').show().html('*长度6~8位');
		  $("#password").val("");
	}else{
		 $('#prompt2').hide();
	}
}
</script>
<body>
	<div id="contact-form"> 
	<%
		String id = request.getParameter("id");
		String account = request.getParameter("account");
		String inviteCode = request.getParameter("inviteCode");
		if(id==null || id.equals("null")){
			id="";
		}
		if(account==null || account.equals("null")){
			account="";
		}
		if(inviteCode==null || inviteCode.equals("null")){
			inviteCode="";
		}
		%>

<form   id="contact"  action="<%=request.getContextPath()%>/pintuapi" method="post"
			name="registerForm" onsubmit="return checkNull()">
	<fieldset>  
		<label for="header" class="header">我要注册</label>

		<input type="hidden" name="method" value="register" />
		<input type="hidden" name="id" value="<%=id%>" /> 

		<label for="email">账户</label>
			<input type="text" name="account"  id="account" value ="<%=account %>"  onblur="checkEmail()"/>
			<div class = "prompt"><span style="display: none;" id=prompt1></span></div>
			
		<label for="pwd">密码</label>
			<input type="password" name = "password"  id="password" onblur="checkLength()"/>
			<div class = "prompt"><span style="display: none;" id="prompt2"></span></div>
	
		<label for="pwd">邀请码</label>
	   		 <input type="text" name = "inviteCode"  id="inviteCode"  value ="<%=inviteCode %>"/>
		 <p class="twoBtn">
			<input type="submit" value="注册" name="submit" class="button" id="submit"/>
			<input type="reset" value="重置" name="reset" class="button" id="reset"/> 
		<p>
	</fieldset>
</form>
</div>
</body>
<%@include file="footer.jsp"%>
</html>