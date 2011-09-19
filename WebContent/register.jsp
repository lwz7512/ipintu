<%@ page language="java" import="java.util.*"
	contentType="text/html; charset=UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>My JSP 'login_sys.jsp' starting page</title>

<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">
<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
<meta http-equiv="description" content="This is my page">
<meta http-equiv="content-type" content="text/html; charset=UTF-8">
</head>

<script>

	function checkNull(){
		
	}
	
	var xmlHttp = null;
	
	function init() {
		xmlHttp = new ActiveXObject("Microsoft.XMLHTTP");
	}
	
	function check() {
		if (xmlHttp == null) {
			init();
		}
		var account = document.getElementById("account").value;
		var url = "validate?account=" + account;
		xmlHttp.open("GET", url, true);
		xmlHttp.onreadystatechange = checkCallback;
		xmlHttp.send(null);
	}
	
	function checkCallback() {
		if (xmlHttp.readyState == 4) {
			var flag = xmlHttp.responseText;
			if (flag == "false") {
				alert("账户可以使用");
			} else {
				alert("该账户已被占用，请重新输入！");
			}
		}
	}
</script>

<body>

	<form action="pintuapi" method="post">
		<input type="hidden" name="method" value="register" /> 
		账户：<input type="text" name="account" onblur=check(); />
		密码：<input type="password" name = "password" />
		邀请码：<input type="text" name = "inviteCode" />
		<input type="submit" name="submit" value="注册"/>
		<input type="button" name="apply" value="申请" onclick=""/>
	</form>
</html>