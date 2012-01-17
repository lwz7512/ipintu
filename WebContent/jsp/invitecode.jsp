<%@ page language="java" import="java.util.*"
	contentType="text/html; charset=UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>Accept page</title>
<link rel="icon" type="image/png" href="http://ipintu.com/favicon.png">
<link rel="stylesheet" type="text/css"
	href="<%=request.getContextPath()%>/jsp/css/style.css" />
<link rel="stylesheet" type="text/css"
	href="<%=request.getContextPath()%>/jsp/css/header.css" />
<script language=javascript
	src="<%=request.getContextPath()%>/jsp/js/jquery.js"
	type=text/javascript></script>
</head>
<script type="text/javascript">
function createInviteCode(){
		$.post('<%=request.getContextPath()%>/pintuapi', {
			'method'  : 'createInviteCode',
			'userId'	: '<%=request.getParameter("userId")%>'
		}, 
		//回调函数
		function (result) {
			if(result.length==0){
				$('#prompt').show().html('生成邀请码失败');
			}else{
				$('#prompt').show().html(result);
			}
		});
}
</script>
<%@include file="adminHead.jsp"%>
<body>
	<input type="button" name="applicant" class="button" value="生成邀请码"
		onclick="createInviteCode()" /><br/>
	<span style="display: none;" id="prompt"></span>
</body>
</html>