<%@ page language="java" import="java.util.*"
	contentType="text/html; charset=UTF-8"%>

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

function checkNickname(){
	var nick = $("#nickName").attr("value");
	if(nick==null || nick==""){
		return false;
	}
	return true;
}

function check(){
	var flag = checkNickname();
	if(flag){
		$('#prompt').show().html('<img src="<%=request.getContextPath()%>/jsp/img/loading.gif">');
		var nickName = $("#nickName").attr("value");
		$.post('<%=request.getContextPath()%>/pintuapi', {
			'method'  : 'examine',
			'nickName'	: nickName
		}, 
		//回调函数
		function (result) {
			if(result == 1){//result为1，用户已存在
			    $('#prompt').html('<img src="<%=request.getContextPath()%>/jsp/img/no.png">');
			}else if(result == 0){
				$('#prompt').html('<img src="<%=request.getContextPath()%>/jsp/img/ok.png">');
			}
		});
	}else{
		$('#prompt').html('<font  color="red">*</font>');
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
<form id="contact" method="post"
	action="<%=request.getContextPath()%>/pintuapi">
	<fieldset>
		<label for="header" class="header">修改昵称</label>
		<input type="hidden" name="method" value="moidfyNickname" />
		<input type="hidden" name="userId" value="<%= request.getParameter("userId")%>" />
		
		<label for="pwd">新昵称</label>
			<input type="text" name= "nickName"  id="nickName" oninput="check()"/>
			<span id="prompt"><font  color="red">*</font></span>
			
		<p class="loginBtn">
			<input type="button" name="submit" class="submit" id="submit" value="提交"  />
		</p>
	</fieldset>
</form>
</div>
</body>
</html>