<%@ page language="java" import="java.util.*"
	contentType="text/html; charset=utf-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>获取社区长廊</title>
<link rel="stylesheet" type="text/css"
	href="<%=request.getContextPath()%>/jsp/css/style.css" />
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
		alert(typeof(end)+end+typeof(start)+start);
		$.post('<%=request.getContextPath()%>/pintuapi', {
		'method'  : 'getGalleryByTime',
		'userId'	: '<%= request.getParameter("userId")%>',
		'startTime' : start ,
		'endTime' : end
		}, 
		//回调函数
		function (result) {
			var res =result.trim();
			alert(res);
			if( res=='[]'){//若result为管理员
				$('#prompt').show();
				$('#prompt').html('当前画廊贴图数目为0');
			}else{
				$('#prompt').show();
				$('#prompt').html(res);
			}
		});
	}
}
</script>

<body onload="loadGallery()">
<div class="main">
	<input type="button"  value="刷新画廊"  onclick = "loadGallery()"/><br/>
	<div class = "prompt"><span style="display: none;" id="prompt"></span></div>
	</div>
</html>