<%@ page language="java" import="java.util.*"
	contentType="text/html; charset=UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>上传图片页面</title>
<script language=javascript
	src="<%=request.getContextPath()%>/jsp/js/jquery.js"
	type=text/javascript></script>
<link rel="stylesheet" type="text/css"
	href="<%=request.getContextPath()%>/jsp/css/style.css" />
<link rel="stylesheet" type="text/css"
	href="<%=request.getContextPath()%>/jsp/css/header.css" />
</head>

<script type="text/javascript" language="javascript">
	function CheckFileType() {		 
		var objFileUpload = document.getElementById('file1');//FileUpload 
		var objMSG = document.getElementById('msg');//显示提示信息用的DIV 
		var FileName = new String(objFileUpload.value);//文件名 
		var extension = new String(FileName.substring(FileName.lastIndexOf(".") + 1, FileName.length));//文件扩展名
		extension = extension.toLowerCase();
		if (extension == "jpg" || extension == "png")//你可以添加扩展名 
		{
			objMSG.innerHTML="it's ok, now go to Upload!";
		} else {
			objMSG.innerHTML="oops! the file you selected not jpg or png file!";
			clearFileInput(objFileUpload);
		}
	}

	function clearFileInput(file){
	    var form=document.createElement('form');
	    document.body.appendChild(form);
	   // 记住file在旧表单中的的位置
	    var pos=file.nextSibling;
	    form.appendChild(file);
	    form.reset();
	    pos.parentNode.insertBefore(file,pos);
	    document.body.removeChild(form);
	}

	function checkBlank(){
		var objFileUpload = document.getElementById('file1');//FileUpload
		var selectedFile = new String(objFileUpload.value);//文件名
		var nickName = document.getElem
		if(selectedFile==""){
			var objMSG = document.getElementById('msg');//显示提示信息用的DIV
			objMSG.innerHTML="No file selected!";
		}
		if(nickName==""){
			var objMSG = document.getElementById('msg');//显示提示信息用的DIV
			objMSG.innerHTML="No nickName named!";
		}
		
		if(selectedFile=="" || nickName==""){
			return false;			
		}else{
			return true;
		}

	} 

	function clearMsg(){
		var objMSG = document.getElementById('msg');//显示提示信息用的DIV
		objMSG.innerHTML="";
	}
	
	function checkNickname(){
		var nick = document.getElementById('nickName');
		if(nick==null || nick==""){
			return false;
		}
		return true;
	}

	function check(){
		var flag = checkNickname();
		if(flag){
			$('#prompt').show().html('<img src="<%=request.getContextPath()%>/jsp/img/loading.gif">');
			var nickName = $("#nickName").attr("value");
			alert(nickName);
			$.post('<%=request.getContextPath()%>/pintuapi', {
				'method'  : 'examine',
				'nickName'	: nickName
			}, 
			//回调函数
			function (result) {
				if(result == 1){//result为1，用户已存在
				    $('#prompt').html('<img src="<%=request.getContextPath()%>/jsp/img/no.png">');
				}else if(result == 0){
					$('#prompt').html('<img src="<%=request.getContextPath()%>/jsp/img/ok.png">');
				}
			});
		}else{
			$('#prompt').html('<font  color="red">*</font>');
		}
	}
</script>
<body>
<div class="xft">
	<div class="xq" id="xit">
		<a href="../index.jsp" class="xdt" id="zh-top-link-logo"></a>
	</div>
</div>
<form name="uploadForm"  id="contact" 
	onsubmit="return checkBlank()" onreset="clearMsg();"
	action="<%=request.getContextPath()%>/pintuapi" method="post"
	enctype="multipart/form-data">
		<fieldset>
			<label for="header" class="header">用户基本信息修改</label>
			<input type="hidden" name="method" value="uploadAvatar" />
			<input type="hidden" name="userId" value="<%= request.getParameter("userId")%>" />
			
			<label for="pwd">新昵称</label>
			<input type="text" name= "nickName"  id="nickName" oninput="check()"/>
			<span id="prompt"><font  color="red">*</font></span>
			<label for = "pwd">上传头像
			<input type="file" name="file1" id="file1" size="20" onchange="CheckFileType();" />
			</label>
			<p class="twoBtn">
			<input type="submit" value="上传" name="submit" class="button" id="submit"/>
			<input type="reset" value="重置" name="reset" class="button" id="reset"/> 
			</p>
			<label for="prompt" class="error">
					<span id="msg"></span>
			</label>
		</fieldset>
</form>
</body>
</html>