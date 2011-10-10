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
	var account = document.getElementById("account").value;
	var password = document.getElementById("password").value;
	var inviteCode = document.getElementById("inviteCode").value;
	if(account == null || password == null || inviteCode==null || account == "" || password == "" || inviteCode=="" ){
		return false;
	}
	return true;
}

function checkEmail(){
	var emailStr=document.getElementById("account").value;
	var emailPat = new RegExp(/^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/g);
	if (!emailPat.test(emailStr)) {
		 $('#prompt1').show();
		  $('#prompt1').html('*邮箱格式不正确');
		  document.getElementById("account").value=null;
	}else{
		$('#prompt1').hide();
	}
	return true;
}

function checkLength(){
	var pwd = document.getElementById("password").value;
	if(pwd.length <6 || pwd.length>8){
		  $('#prompt2').show();
		  $('#prompt2').html('*长度6~8位');
		  document.getElementById("password").value=null;
	}else{
		 $('#prompt2').hide();
	}
}
</script>
<body>
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
	<div class="main">
		<form action="<%=request.getContextPath()%>/pintuapi" method="post"
			name="registerForm" onsubmit="return checkNull()">
			<div class="pagetop">
				<span class="topfont">用户注册</span>
			</div>
			<div class="pagemid">
				<div>
					<input type="hidden" name="method" value="register" />
							<input type="hidden" name="id" value="<%=id%>" /> 
					
				</div>
				<div class="pageju">
					账&nbsp;&nbsp;号：<input type="text" name="account"  id="account" value ="<%=account %>"  onblur="checkEmail()"/>
				<div class = "prompt"><span style="display: none;" id=prompt1></span></div>
				</div>
				<div class="pageju">
					密&nbsp;&nbsp;码：<input type="password" name = "password"  id="password" onblur="checkLength()"/>
				<div class = "prompt"><span style="display: none;" id="prompt2"></span></div>
				</div>
				<div class="pageju">
				    邀请码：<input type="text" name = "inviteCode"  id="inviteCode"  value ="<%=inviteCode %>"/>
				 </div>
				<div class="pageju pagecenter pagebuttom">
					<input type="submit" value="注册"/>&nbsp; 
					<input type="reset" value="重置"/>&nbsp; 
				</div>
			</div>
		</form>
	</div>
</html>