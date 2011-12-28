<%@ page language="java" import="java.util.*"
	contentType="text/html; charset=UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>普通用户操作页面</title>
<link rel="icon" type="image/png" href="http://ipintu.com/favicon.png">
<link rel="stylesheet" type="text/css"
	href="<%=request.getContextPath()%>/jsp/css/header.css" />
<link rel="stylesheet" type="text/css"
	href="<%=request.getContextPath()%>/jsp/css/style.css" />
</head>
<body>
	<%String userId = request.getParameter("userId"); %>
<div class="xft">
<div class="xq" id="xit">
<a href="../index.jsp" class="xdt" id="zh-top-link-logo"></a>

 <div id="zg-top-nav" class="xls">
<ul class="xjs">
<li class="xis" id="zh-top-home-link">
<a class="xhs" href="../index.jsp">首页</a>
</li>
<li class="xis">
<a class="xhs" href="accept.jsp?userId=<%=userId %>">处理申请</a>
</li>
<li class="xis">
<a class="xhs" href="review.jsp?userId=<%=userId %>">审核贴图</a>
</li>
<li class="xis">
<a class="xhs" href="#">发布可换礼物</a>
</li>
<li class="xis">
<a class="xhs" href="#">发布社区新闻</a>
</li>
</ul>
</div>
</div>
</div>

<div id="contact-form">
</div>
</body>
<%@include file="footer.jsp"%>
</body>
</html>