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
				<input type="hidden" name="method" value="getGalleryByTime"/>
				开始时间：<input type="text"  name="startTime" value ="<%=new Date().getTime()-1000*60*60*60 %>"/>
				结束时间：<input type="text"  name="endTime" value ="<%=	new Date().getTime() %>"/>
				<input type="submit" name="submit" value="测试">
				</form>
</html>