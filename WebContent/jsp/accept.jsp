<%@ page language="java" import="java.util.*"
	contentType="text/html; charset=UTF-8"%>
<%@ page import="com.pintu.beans.User"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>Accept page</title>
<link rel="stylesheet" type="text/css"
	href="<%=request.getContextPath()%>/jsp/css/style.css" />
</head>

<body>
	<div class="main">
		<table cellspacing="1" class="table1">
			<tbody>
				<tr>
					<td colspan="3">需要处理的申请列表：</td>
					<td align="center">刷新列表&nbsp;<a
						href="<%=request.getContextPath()%>/pintuapi?method=getApplicant&userId=a053beae20125b5b">
							<img src="<%=request.getContextPath()%>/jsp/img/ref.png"> </a>
					</td>
				</tr>
				<tr>
					<td align="center">邮箱</td>
					<td align="center">理由</td>
					<td colspan="2" align="center">授理意见</td>
					<%
						@SuppressWarnings("unchecked")
						List<User> list = (List<User>) request.getAttribute("tempUser");
						if (list != null && list.size() > 0) {
							for (int i = 0; i < list.size(); i++) {
								User user = (User) list.get(i);
					%>
				
				<tr>
					<td><%=user.getAccount()%></td>
					<td><%=user.getApplyReason()%></td>
					<td>
						<form action="<%=request.getContextPath()%>/pintuapi"
							method="post" name="acceptForm">
							<input type="hidden" name="method" value="accept" /> <input
								type="hidden" name="opt" value="approve" /> <input
								type="hidden" name="id" value="<%=user.getId()%>" /> <input
								type="hidden" name="account" value="<%=user.getAccount()%>" />
							<input type="submit" value="同意">
						</form></td>
					<td>
						<form action="<%=request.getContextPath()%>/pintuapi"
							method="post" name="acceptForm">
							<input type="hidden" name="method" value="accept" /> <input
								type="hidden" name="opt" value="refuse" /> <input type="hidden"
								name="id" value="<%=user.getId()%>" /> <input type="hidden"
								name="account" value="<%=user.getAccount()%>" /> <input
								type="submit" value="拒绝">
						</form></td>
				</tr>
				<%
						}
					}
				%>
			</tbody>
		</table>
	</div>
</body>
</html>