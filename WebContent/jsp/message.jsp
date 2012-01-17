<%@ page language="java" import="java.util.*"
	contentType="text/html; charset=UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>Message page</title>
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
function receiveMsg(){	
		$.post('<%=request.getContextPath()%>/pintuapi', {
			'method'  : 'getUserMsg',
			'userId'	: 'b8931b314c24dca4'
		}, 
		//回调函数
		function (result) {
			if(result.length==0){
				$('#prompt').show().html('暂无任何消息');
				$("tr.table_header").html("");
			}else{
				$("tr.table_line").html("");
				for( key in result ){
					$("table").append("<tr class='table_line'><td>result[key].msgType</td>"
					+	"<td>"+result[key].senderName+"</td>"+"<td>"+result[key].writeTime+"</td>"
					+	"<td>"+result[key].content+"</td>"+"<td>"+result[key].read+"</td></tr>");
				}
			}
		},"json");
}
</script>
<%@include file="adminHead.jsp"%>
<body onload="receiveMsg()">
<table class='table' >
	<tr class="table_title">
		<td colspan='4' align="center">
			未读消息列表 <span style="display: none;" id="prompt"></span>
		</td>
		<td >
			<img src="<%=request.getContextPath()%>/jsp/img/refresh.png" onclick="receiveMsg()" title="点击刷新"/>
		</td>
	</tr>
	<tr class="table_header">
		<td>消息类型</td>
		<td>发送人</td>
		<td>发送时间</td>
		<td>内容</td>
		<td>阅读状态</td>
	</tr>
</table>
	
</body>
</html>