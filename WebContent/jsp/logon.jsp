<%@ page language="java" import="java.util.*"
	contentType="text/html; charset=UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>Logon page</title>
<link rel="stylesheet" type="text/css"
	href="<%=request.getContextPath()%>/jsp/css/style.css" />
<script language=javascript
	src="<%=request.getContextPath()%>/jsp/js/jquery.js"
	type=text/javascript></script>
</head>

<script language=javascript type=text/javascript>
function checkNull(){
	var account = document.getElementById("account").value;
	var password = document.getElementById("password").value;
	if(account == null || password == null || account == "" || password == ""){
		return false;
	}
	return true;
}

function check(){
	var flag = checkNull();
	if(flag){
		var account = document.getElementById("account").value;
		var password = document.getElementById("password").value;
		$.post('<%=request.getContextPath()%>/pintuapi', {
		'method'  : 'logon',
		'account'	: account,
		'password' : password 
		}, 
		//回调函数
		function (result) {
			var res =result.trim();
			var userId="";
			if( res.indexOf('admin') > -1){//若result为管理员
				//转到管理员页面admin.jsp
				userId = res.substring(6);
				window.location.replace("<%=request.getContextPath()%>/jsp/admin.jsp?userId="+userId);
			}else if( res == "0" ){
				 $('#prompt').show();
				$('#prompt').html('密码不正确');
				document.getElementById("password").value=null;
			}else if( res == "-1"){
				 $('#prompt').show();
				$('#prompt').html('用户不存在，请申请注册');
				document.getElementById("account").value=null;
				document.getElementById("password").value=null;
			}else{
				//转到普通用户页面normal.jsp
				userId = res.substring(8);
				window.location.replace("<%=request.getContextPath()%>/jsp/normal.jsp?userId="+userId);
			}
		});
	}
}
</script>
<body>

	<div class="main">
		<form method="post"  action="<%=request.getContextPath()%>/pintuapi"
			name="pageForm" >
			<div class="pagetop">
				<span class="topfont">用户登录</span>
			</div>
			<div class="pagemid">
				<div>
					<input type="hidden" name="method" value="logon" />
				</div>
				<div class="pageju">
					账号：<input type="text" name="account" id="account" />
				</div>
				<div class="pageju">
					密码：<input type="password" name="password" id="password"  />
					<div class = "prompt"><span style="display: none;" id="prompt"></span></div>
				</div>
				<div class="pageju pagecenter pagebuttom">
					<input type="button"  value="登录"  onclick = "check()"/>&nbsp; <a href="register.jsp">注册</a>
				</div>
			</div>
		</form>
	</div>
</html>