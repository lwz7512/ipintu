<%@ page language="java" import="java.util.*"
	contentType="text/html; charset=UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>Logon page</title>
<link rel="stylesheet" type="text/css"
	href="<%=request.getContextPath()%>/jsp/css/style.css" />
</head>

<script language=javascript  type=text/javascript>
function checkNull(){
	var account = document.getElementById("account").value;
	var password = document.getElementById("password").value;
	if(account == null || password == null || account == "" || password == ""){
		return false;
	}
	return true;
}
</script>
<body>

	<div class="main">
		<form action="<%=request.getContextPath()%>/pintuapi" method="post"
			name="pageForm" onsubmit="return checkNull()">
			<div class="pagetop">
				<span class="topfont">用户登录</span>
			</div>
			<div class="pagemid">
				<div>
					<input type="hidden" name="method" value="logon" />
				</div>
				<div class="pageju">
					账号：<input type="text" name="account"  id="account"/>
				</div>
				<div class="pageju">
					密码：<input type="password" name="password"  id="password"/>
				</div>
				<div class="pageju pagecenter pagebuttom">
					<input type="submit" value="登录"/>&nbsp; 
					<a href="register.jsp">注册</a>
				</div>
			</div>
		</form>
	</div>
</html>