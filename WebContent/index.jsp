<%@ page language="java" import="java.util.*"
	contentType="text/html; charset=utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Pintu Home Page</title>
<link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/jsp/css/style.css" />
</head>
<body>
	<div class="indexmain">
		<div class="indexcontent">
			<div class="indextop  indexfont">Welcome to Pintu community!</div>
			<div class="indextop">
				<a href="jsp/normal.jsp">普通用户页面</a>
			</div>
			<div class="indextop">
				<a href="jsp/admin.jsp">客服管理页面</a>
			</div>
		</div>
	</div>
</body>
</html>