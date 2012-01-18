<%@ page language="java" import="java.util.*"
	contentType="text/html; charset=UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>Deal complaint page</title>
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
function getPicDetail(){
	var flag = true;
	if(flag){
		var picId = $('#picId').attr("value");
		$.post('<%=request.getContextPath()%>/pintuapi', {
			'method'  : 'getPicDetail',
			'tpId': picId,
			'userId'	: '<%=request.getParameter("userId")%>'
		}, 
		//回调函数
		function (result) {
			if(result.id == null || result.id==""){
				$('#prompt').show().html('符合条件的贴图不存在');
				$("tr.table_header").html("");
			}else{
				$("tr.table_line").html("");
				var thumbnail = result.id+"_Thumbnail";
				var creationTime = result.publishTime;
				$("table").append("<tr class='table_line'><td>"+result.id+"</td><td colspan='2' align='center'><img src='<%=request.getContextPath()%>/pintuapi?method=getImageFile&tpId="+thumbnail+"'/></td>"
					+	"<td>"+result.tags+"</td>"+"<td>"+result.description+"</td>"
					+	"<td>"+result.author+"</td>"+"<td>"+result.publishTime+"</td>"
					+"<td><form id='form1' action='<%=request.getContextPath()%>/pintuapi?userId=<%=request.getParameter("userId")%>' method='post'>"
					+ "<input type='hidden' name='method' value='reviewPicture' />"
					+ "<input type='submit' value='审核不通过'/>"
					+ "<input type='hidden' name='picId' id='picId' value='"+result.id+
				    "' /><input type='hidden' name='thumbnailId' id='thumbnailId' value='"+thumbnail+
					"' /><input type='hidden' name='creationTime' id='creationTime' value='"+creationTime+
					"' /></form></td></tr>");
			}
		}, "json");
	}
}
</script>
<%@include file="adminHead.jsp"%>
<body>
	<table class='table' >
		<tr class="table_title">
			<td colspan='8' align="center">
				图片ID：<input type="text" id="picId" name="picId"/>
				<input type="button" value="查询" onclick="getPicDetail()"/>
			</td>
		</tr>
		<tr>
			<td colspan='8' align="center">
				查询结果： <span style="display: none;" id="prompt"></span>
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