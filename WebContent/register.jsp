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
	<form action="pintuapi" method="post" name="registerForm">
		<input type="hidden" name="method" value="register" /> 
		<input type="hidden" name="id" value="<%=id%>" /> 
		账户：<input type="text" name="account"  value ="<%=account %>" onblur=check(); /><br/>
		密码：<input type="password" name = "password" /><br/>
		邀请码：<input type="text" name = "inviteCode"   value ="<%=inviteCode %>"/><br/>
		<input type="submit" name="submit" value="注册"/><br/><br/>
		<a href="apply.jsp">向客服人员发出申请</a>
	</form>
</html>