<%@ page language="java" import="java.util.*"
	contentType="text/html; charset=utf-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>获取社区长廊</title>
<link rel="stylesheet" type="text/css"
	href="<%=request.getContextPath()%>/jsp/css/style.css" />
</head>

<body>
<div class="main">
	<form action="<%=request.getContextPath()%>/pintuapi" method="post">
		<input type="hidden" name="method" value="getGalleryByTime" /> <input
			type="hidden" name="userId" value="b05a847f81fc593e" /><br /> 开始时间：<input
			type="text" name="startTime"
			value="<%=new Date().getTime() - 1000 * 60 * 60 * 60%>" /> 结束时间：<input
			type="text" name="endTime" value="<%=new Date().getTime()%>" /> <input
			type="submit" name="submit" value="测试">
	</form>
	</div>
</html>