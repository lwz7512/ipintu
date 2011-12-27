<%@ page language="java" import="java.util.*"
	contentType="text/html; charset=UTF-8"%>
<%@include file="header.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>申请页面</title>
<link rel="icon" type="image/png" href="http://ipintu.com/favicon.png">
<link rel="stylesheet" type="text/css"
	href="<%=request.getContextPath()%>/jsp/css/style.css" />
<script language=javascript
	src="<%=request.getContextPath()%>/jsp/js/jquery.js"
	type=text/javascript></script>
</head>

<script language=javascript type=text/javascript>

	function checkNull(){
		var account = $("#account").attr("value");
		var reason =$("#reason").attr("value");
		if(account == null || reason == null || account == "" || reason == ""){
			return false;
		}
		return true;
	}

	
	function checkEmail(){
		var emailStr = $("#account").attr("value");
		var emailPat = new RegExp(/^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/g);
		if (!emailPat.test(emailStr)) {
			return false;
		}
		return true;
	}
	
	function check(){
		var flag = checkEmail();
		if(flag){
			$('#prompt').show().html('<img src="<%=request.getContextPath()%>/jsp/img/loading.gif">');
			var account = $("#account").attr("value");
			$.post('<%=request.getContextPath()%>/pintuapi', {
				'method'  : 'validate',
				'account'	: account
			}, 
			//回调函数
			function (result) {
				if(result == 1){//result为1，用户已存在
				    $('#prompt').html('<img src="<%=request.getContextPath()%>/jsp/img/no.png">');
				    $("#account").val("");
				}else{
					 $('#prompt').html('<img src="<%=request.getContextPath()%>/jsp/img/ok.png">');
				}
			});
		}else{
			$('#prompt').html('<font  color="red">*</font>');
		}
	}

</script>

<body>
	<div id="contact-form"> 
		<form id="contact"  action="<%=request.getContextPath()%>/pintuapi" method="post"
			name="applyForm" onsubmit="return checkNull()">
		<fieldset>  
			<label for="header" class="header">申请</label>
				<input type="hidden" name="method" value="apply" />
			<label for="email">注册邮箱</label>
					<input type="text" name="account" id="account" onblur="check()" />
					<span id="prompt"><font  color="red">*</font></span>
			<label for="message">申请理由</label>
				<textarea name="reason" id="reason" ></textarea>
				<span><font  color="red">*</font></span>
			<p class="applyBtn">
				<input type="submit"  name="submit" class="button" id="submit"  value="我要申请" />
			</p>
		</fieldset>
		</form>
	</div>
	</body>
<%@include file="footer.jsp"%>
</html>