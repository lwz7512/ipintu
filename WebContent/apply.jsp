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
	function checkEmail(){
			var emailStr=document.all.applyForm.account.value;
			var emailPat=/^(. )@(. )$/;
			var matchArray=emailStr.match(emailPat);
			if (matchArray==null) {
			return false;
			}
			return true;
	}
</script>

<body>

	<form action="pintuapi" method="post" name="applyForm">
		<input type="hidden" name="method" value="apply" /> 
		注册邮箱：<input type="text" name="account" onblur="checkEmail(); "/><br/>
		申请理由：<input type="text" name = "reason" />
		<input type="submit"  value="提交申请">
	</form>
</html>