<%@ page language="java" import="java.util.*"
	contentType="text/html; charset=UTF-8"%>
<%@ page import="com.pintu.beans.Applicant"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>Accept page</title>
<link rel="stylesheet" type="text/css"
	href="<%=request.getContextPath()%>/jsp/css/style.css" />
<script language=javascript
	src="<%=request.getContextPath()%>/jsp/js/jquery.js"
	type=text/javascript></script>
</head>
<script type="text/javascript">
function loadApplicant(){
	var flag = true;
	if(flag){
		var table ="<table class='acceptTable'><tr><td align='center'>申请邮箱</td>"+
		"<td align='center'>理由</td>"+
		"<td colspan='2' align='center'>授理意见</td></tr>";
		
		$.post('<%=request.getContextPath()%>/pintuapi', {
			'method'  : 'getApplicant',
			'userId'	: '<%= request.getParameter("userId")%>'
		}, 
		//回调函数
		function (result) {
			for( key in result ){
				table+="<tr><td>"+result[key].account+"</td><td>"+result[key].applyReason+
				"</td><td><form action='<%=request.getContextPath()%>/pintuapi?userId=<%= request.getParameter("userId")%>' method='post'>"+
				"<input type='hidden' name='method' value='accept' />"+
				"<input type='hidden' name='opt' value='approve' />"+
				"<input type='submit' value='同意'/>"+
				"<input type='hidden' name='id' id='tempId' value='"+result[key].id+
				"' /><input type='hidden' name='account' id='account' value='"+result[key].account+
				"' /></form></td>"+
				"<td><form action='<%=request.getContextPath()%>pintuapi?userId=<%= request.getParameter("userId")%>' method='post'>"+
				"<input type='hidden' name='method' value='accept' />"+
				"<input type='hidden' name='opt' value='refuse' />"+
				"<input type='submit' value='拒绝'/>"+
				"<input type='hidden' name='id' id='tempId' value='"+result[key].id+
				"' /><input type='hidden' name='account' id='account' value='"+result[key].account+
				"' /></form></td></tr>";
			}
			
			table +="</table>";
			
			 $('#table').show();
			 $('#table').html(table);
			 
		},"json");
	}
}


</script>

<body onload="loadApplicant()">
	<div class="main">
		<div >
			<span class="topfont">需要处理的申请列表 &nbsp;&nbsp;&nbsp;&nbsp;<input type="button"
				value="刷新列表" onclick="loadApplicant()" /></span>
		</div>
		
		<div>
			<span style="display: none;" id="table"></span>
		</div>
		
		<div class="prompt">
			<span style="display: none;" id="prompt"></span>
		</div>
	</div>
</body>
</html>