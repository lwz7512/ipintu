<%@ page language="java" import="java.util.*"
	contentType="text/html; charset=utf-8"%>
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
		
				<form action="pintuapi" method="post"  >
				<input type="hidden" name="method" value="sendMsg"/>
				接收者：<input type="text"  name="receiver" value ="a053beae20125b5b"/>
				发送者：<input type="text"  name="userId" value ="b8931b314c24dca4"/>
				消息内容：<input type="text"  name="content"/>
				<input type="submit" name="submit" value="发送">
				</form>
</html>