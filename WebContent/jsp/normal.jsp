<%@ page language="java" import="java.util.*"
	contentType="text/html; charset=utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>普通用户操作页面</title>
<link rel="stylesheet" type="text/css"
	href="<%=request.getContextPath()%>/jsp/css/style.css" />
</head>
<body>
	<div class="main">
	<%String userId = request.getParameter("userId"); %>
		<div class="indextop">
			<a href="upload.jsp?userId=<%=userId %>">上传图片</a>
		</div>
		<div class="indextop">
			<a href="gallery.jsp?userId=<%=userId %>">社区长廊</a>
		</div>
	</div>
</body>
</html>