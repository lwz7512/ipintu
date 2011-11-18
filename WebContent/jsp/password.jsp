<%@ page language="java" import="java.util.*"
	contentType="text/html; charset=UTF-8"%>
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
	var pwd = $("#oldpwd").attr("value");
	if(pwd == null || pwd=="" ){
		return false;
	}
	return true;
}

function check(){
	var flag = checkNull();
	if(flag){
		$('#prompt').show().html('<img src="<%=request.getContextPath()%>/jsp/img/loading.gif">');
		var oldpwd = $("#oldpwd").attr("value");
		$.post('<%=request.getContextPath()%>/pintuapi', {
			'method'  : 'confirm',
			'userId'	: '<%=request.getParameter("userId")%>',
			'password' : oldpwd
			
		}, 
		//回调函数
		function (result) {
			if(result == 1){//result为1，原密码正确
			    $('#prompt').html('<img src="<%=request.getContextPath()%>/jsp/img/no.png">');
			}else if(result == 0){
				$('#prompt').html('<img src="<%=request.getContextPath()%>/jsp/img/ok.png">');
			}
		});
	}else{
		$('#prompt').html('<font  color="red">*</font>');
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


</script>
<body>
<div class="xft">
	<div class="xq" id="xit">
		<a href="../index.jsp" class="xdt" id="zh-top-link-logo"></a>
	</div>
</div>
<div id="contact-form"> 
<form   id="contact"  action="<%=request.getContextPath()%>/pintuapi" method="post">
	<fieldset>  
		<label for="header" class="header">修改密码</label>

		<input type="hidden" name="method" value="modifyPassword" />
		<input type="hidden" name="userId" value="<%= request.getParameter("userId")%>" />

		<label for="pwd">原密码</label>
			<input type="password" name= "oldpwd"  id="oldpwd"  onblur="check()"/>
			<span id="prompt"><font  color="red">*</font></span>
			
		<label for="pwd">新密码</label>
			<input type="password" name= "pwd1"  id="pwd1" onblur="checkLength()"/>
			<span><font  color="red">*</font></span>
			
		<label for="pwd">确认新密码</label>
	   		 <input type="password" name= "pwd2"  id="pwd2" onblur="compare()"/>
	   		<span><font  color="red">*</font></span>
	   		 
   		 <label for="prompt" class="error">
			<span style="display: none;" id=prompt1></span>
			<span style="display: none;" id="prompt2"></span>
		</label>
			
		 <p class="twoBtn">
			<input type="submit" value="提交" name="submit" class="button" id="submit"/>
			<input type="reset" value="重置" name="reset" class="button" id="reset"/> 
		<p>
		
	</fieldset>
</form>
</div>
</body>
</html>