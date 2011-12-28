<%@ page language="java" import="java.util.*"
	contentType="text/html; charset=UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>获取社区长廊</title>
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

function loadGallery(){
	var flag = true;
	if(flag){
		var end =<%=new Date().getTime()%>;
		var start = end - 1000 * 60 * 60;
		$.post('<%=request.getContextPath()%>/pintuapi', {
			'method'  : 'getGalleryByTime',
			'userId'	: '<%= request.getParameter("userId")%>',
			'startTime' : start ,
			'endTime' : end
		}, 
		//回调函数
		function (result) {
			var res =result.trim();
			if( res=='[]'){//若result为管理员
				$('#prompt').show().html('当前画廊贴图数目为0');
			}else{
				$('#gallery').show().html(res);
			}
		});
	}
}
</script>

<body onload="loadGallery()">
<div class="xft">
	<div class="xq" id="xit">
		<a href="../index.jsp" class="xdt" id="zh-top-link-logo"></a>
	</div>
</div>
	<table class='table' >
		<tr class="table_title">
			<td colspan='3' align="center">
				画廊列表 <span style="display: none;" id="prompt"></span>
			</td>
			<td colspan='1'>
				<img src="<%=request.getContextPath()%>/jsp/img/refresh.png" onclick="loadGallery()" title="点击刷新"/>
			</td>
		</tr>
		<tr>
			<td colspan='4'><span style="display: none;" id="gallery"></span></td>
		</tr>
	</table>
</body>
</html>