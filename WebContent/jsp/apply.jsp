<%@ page language="java" import="java.util.*"
	contentType="text/html; charset=UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>申请页面</title>
<link rel="stylesheet" type="text/css"
	href="<%=request.getContextPath()%>/jsp/css/style.css" />
<script language=javascript
	src="<%=request.getContextPath()%>/jsp/js/jquery.js"
	type=text/javascript></script>
</head>

<script language=javascript type=text/javascript>

	function checkNull(){
		var account = document.getElementById("account").value;
		var reason = document.getElementById("reason").value;
		if(account == null || reason == null || account == "" || reason == ""){
			return false;
		}
		return true;
	}

	
	function checkEmail(){
		var emailStr=document.getElementById("account").value;
		var emailPat = new RegExp(/^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/g);
		if (!emailPat.test(emailStr)) {
			return false;
		}
		return true;
	}
	
	function check(){
		var flag = checkEmail();
		if(flag){
			var account = document.getElementById("account").value;
			$.post('<%=request.getContextPath()%>/pintuapi', {
			'method'  : 'validate',
			'account'	: account
			}, 
			//回调函数
			function (result) {
				if(result == 1){//result为1，用户已存在
				    $('#prompt').show();
					$('#prompt').html('<img src="<%=request.getContextPath()%>/jsp/img/no.png">');
					document.getElementById("account").value=null;
				}else{
					 $('#prompt').show();
					$('#prompt').html('<img src="<%=request.getContextPath()%>/jsp/img/ok.png">');
				}
			});
		}
	}

</script>

<body>

	<div class="main">
		<form action="<%=request.getContextPath()%>/pintuapi" method="post"
			name="applyForm" onsubmit="return checkNull()">
			<div class="pagetop">
				<span class="topfont">用户申请</span>
			</div>
			<div class="pagemid">
				<div>
					<input type="hidden" name="method" value="apply" />
				</div>
				<div class="pageju">
					注册邮箱：<input type="text" name="account" id="account"
						onblur="check()" /> <span style="display: none;" id="prompt"></span>
				</div>
				<div class="pageju">
					申请理由：<input type="text" name="reason" id="reason" />
				</div>
				<div class="pageju pagecenter pagebuttom">
					<input type="submit" value="提交申请" />
				</div>
			</div>
		</form>
	</div>
</html>