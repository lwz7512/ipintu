<%@ page language="java" import="java.util.*"
	contentType="text/html; charset=UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>Review page</title>
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
// btn.attributes.add( "onclick ", "this.style.disabled=true ");
function loadPicture(){
	var flag = true;
	if(flag){
		$.post('<%=request.getContextPath()%>/pintuapi', {
			'method'  : 'getLatestPic',
			'userId'	: '<%=request.getParameter("userId")%>'
		}, 
		//回调函数
		function (result) {
			if(result.length == 0){
				$('#prompt').show().html('当前待审核的图片数目为0');
				$("tr.table_header").html("");
			}else{
				$("tr.table_line").html("");
				for( key in result ){
					$("table").append("<tr class='table_line'><td>"+result[key].tpId+"</td><td colspan='2' align='center'><img src='<%=request.getContextPath()%>/pintuapi?method=getImageFile&tpId="+result[key].thumbnailId+"'/></td>"
					+	"<td>"+result[key].tags+"</td>"+"<td>"+result[key].description+"</td>"
					+	"<td>"+result[key].author+"</td>"+"<td>"+result[key].publishTime+"</td>"
					+"<td><form id='form1' action='<%=request.getContextPath()%>/pintuapi?userId=<%=request.getParameter("userId")%>' method='post'>"
					+ "<input type='hidden' name='method' value='reviewPicture' />"
					+ "<input type='submit' value='审核不通过' onclick='loadPicture()'/>"
					+ "<input type='hidden' name='picId' id='picId' value='"+result[key].tpId+
				    "' /><input type='hidden' name='thumbnailId' id='thumbnailId' value='"+result[key].thumbnailId+
					"' /><input type='hidden' name='creationTime' id='creationTime' value='"+result[key].creationTime+
					"' /></form></td></tr>");
				}
			}
		}, "json");
	}
}
</script>
<%@include file="adminHead.jsp"%>
<body onload="loadPicture()">
	<table class='table' >
		<tr class="table_title">
			<td colspan='5' align="center">
				待审核新图列表 <span style="display: none;" id="prompt"></span>
			</td>
			<td >
				<img src="<%=request.getContextPath()%>/jsp/img/refresh.png" onclick="loadPicture()" title="点击刷新"/>
			</td>
		</tr>
		<tr class="table_header">
			<td>ID</td>
			<td colspan='2' align="center">图片</td>
			<td>标签</td>
			<td>描述</td>
			<td>作者</td>
			<td>上传时间</td>
			<td>授理意见</td>
		</tr>
	</table>
</body>
</html>