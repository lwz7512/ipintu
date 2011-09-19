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

<body>

	<form action="pintuapi" method="post" name="logonForm">
		<input type="hidden" name="method" value="logon" /> 
		账户：<input type="text" name="account" /><br/>
		密码：<input type="text" name = "password" />
		<input type="submit"  value="登录">
	</form>
</html>