<%@ page language="java" import="java.util.*"
	contentType="text/html; charset=utf-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>上传图片页面</title>
<link rel="stylesheet" type="text/css"
	href="<%=request.getContextPath()%>/jsp/css/style.css" />
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
			var objMSG = document.getElementById('msg');//显示提示信息用的DIV
			objMSG.innerHTML="No file selected!";
			alert("is blank!");
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

	<div class="main">
		<form name="uploadForm" id="uploadForm"
			onsubmit="return checkBlank();" onreset="clearMsg();"
			action="<%=request.getContextPath()%>/pintuapi" method="post"
			enctype="multipart/form-data">
			<div id="msg"></div>
			<input type="hidden" name="method" value="upload" /><br /> <input
				type="hidden" name="user" value="a053beae20125b5b" /><br /> <input
				type="hidden" name="tags" value="123" /><br /> <input
				type="hidden" name="description" value="1" /><br />
			<div class="pagetop">
				<span class="topfont">上传图片</span>
			</div>
			<div class="pagemid">
				<input type="file" name="file1" id="file1" size="20"
					onchange="CheckFileType();" /> <br />
				<br />
				<div class="pageju pagecenter pagebuttom">
					<input type="submit" name="submit" value="上传"> <input
						type="reset" name="reset" value="重置">
				</div>
			</div>
		</form>
	</div>
</html>