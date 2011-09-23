<%@ page language="java" import="java.util.*"
	contentType="text/html; charset=UTF-8"%>
	<%@ page import="com.pintu.beans.User"%>
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
</script>

<body>
	<table>
		<%
		List<User> list=(List<User>) request.getAttribute("tempUser");
			for(int i=0;i<list.size();i++){
				User user=(User)list.get(i);
		%>
	 <tr align="center">
	 <td>申请理由：<%= user.getApplyReason()%></td>
	 <td>
	 <form action="pintuapi" method="post" name="acceptForm">
		<input type="hidden" name="method" value="accept" /> 
		<input type= "hidden" name="id" value="<%= user.getId() %>" />
		<input type= "hidden" name="account" value="<%= user.getAccount() %>"/>
		<input type="submit"  value="授理请求">
	</form>
	</td>
	</tr>
	<%} %> 
	</table>
</body>
</html>