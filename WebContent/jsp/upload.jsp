<%@ page language="java" import="java.util.*"
	contentType="text/html; charset=UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>上传图片页面</title>

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
	    //记住file在旧表单中的的位置
	    var pos=file.nextSibling;
	    form.appendChild(file);
	    form.reset();
	    pos.parentNode.insertBefore(file,pos);
	    document.body.removeChild(form);
	}

	function checkBlank(){
		var objFileUpload = document.getElementById('file1');//FileUpload
		var selectedFile = new String(objFileUpload.value);//文件名
					
		if(selectedFile==""){
			//var account = $("#account").attr("value");
			var objMSG = document.getElementById('msg');//显示提示信息用的DIV
			objMSG.innerHTML="No file selected!";
			return false;			
		}else{
			return true;
		}

	} 

	function clearMsg(){
		var objMSG = document.getElementById('msg');//显示提示信息用的DIV
		objMSG.innerHTML="";
	}
	
</script>
<body>
<div class="xft">
	<div class="xq" id="xit">
		<a href="../index.jsp" class="xdt" id="zh-top-link-logo"></a>
	</div>
</div>
<form name="uploadForm"  id="contact" 
	onsubmit="return checkBlank();" onreset="clearMsg();"
	action="<%=request.getContextPath()%>/pintuapi" method="post"
	enctype="multipart/form-data">
		<fieldset>
			<label for="header" class="header">上传</label>
			<input type="hidden" name="method" value="upload" /><br />
			<input type="hidden" name="userId" value="<%= request.getParameter("userId")%>" />
			<input type="hidden" name="source" value="desktop" />
			<label for="pwd">描述</label><input type="text" name="description" />
			<label for="pwd">标签</label><input type="text" name="tags"  />
			<input type="hidden" name="isOriginal" value="0" />
			<input type="file" name="file1" id="file1" size="20" onchange="CheckFileType();" />
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