<%@ page language="java" import="java.util.*"
	contentType="text/html; charset=UTF-8"%>
<%@ page import="com.pintu.beans.Applicant"%>
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
function loadApplicant(){
		$.post('<%=request.getContextPath()%>/pintuapi', {
			'method'  : 'getApplicant',
			'userId'	: '<%=request.getParameter("userId")%>'
		}, 
		//回调函数
		function (result) {
			if(result.length == 0){
				$('#prompt').show().html('当前申请者的数目为0');
				$("tr.table_header").html("");
			}else{
				$("tr.table_line").html("");
				for( key in result ){
					$("table").append("<tr class='table_line'><td>"+result[key].account+"</td><td>"+result[key].applyReason+"</td>"+
					"<td><form action='<%=request.getContextPath()%>/pintuapi?userId=<%=request.getParameter("userId")%>' method='post'>"
					+"<input type='hidden' name='method' value='accept' />"
					+"<input type='hidden' name='opt' value='approve' />"
					+"<input type='submit' value='同意'/>"
					+"<input type='hidden' name='account' id='account' value='"+result[key].account+
					"' /></form></td>"+
					"<td><form action='<%=request.getContextPath()%>/pintuapi?userId=<%=request.getParameter("userId")%>' method='post'>"
					+ "<input type='hidden' name='method' value='accept' />"
					+ "<input type='hidden' name='opt' value='refuse' />"
					+ "<input type='submit' value='拒绝'/>"
					+ "<input type='hidden' name='account' id='account' value='"+result[key].account+
					"' /></form></td></tr>");
				}
			}
		}, "json");
}
</script>

<body onload="loadApplicant()">
	<div class="xft">
<div class="xq" id="xit">
<a href="../index.jsp" class="xdt" id="zh-top-link-logo"></a>
</div>
</div>

	<table class='table' >
		<tr class="table_title">
			<td colspan='3' align="center">
				申请列表 <span style="display: none;" id="prompt"></span>
			</td>
			<td colspan='1'>
				<img src="<%=request.getContextPath()%>/jsp/img/refresh.png" onclick="loadApplicant()" title="点击刷新"/>
			</td>
		</tr>
		<tr class="table_header">
			<td>申请邮箱</td>
			<td>理由</td>
			<td colspan='2'>授理意见</td>
		</tr>
	</table>
</body>
</html>