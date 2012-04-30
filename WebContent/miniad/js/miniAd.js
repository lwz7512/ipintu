document.onkeydown = function (e) { 
	var theEvent = window.event || e; 
	var code = theEvent.keyCode || theEvent.which; 
	if (code == 13) { 
		$("#submit").click(); 
	} 
} 

//加载页面的时候去检查cookie
window.onload = function(){

	var action =request("do"); 
	if(action == "regist"){
		showRegisterWindow();
		return;
	}

    //分析cookie值，看是否存在
//    hideLoginWindow();
    var idValue = getCookieValue("venderId");
    var nameValue = getCookieValue("venderName");
	var roleValue = getCookieValue("role");
    //验证用户存在就自动显示所需页面，否则要重新登录
    if(idValue != "" && nameValue !="" && roleValue != ""){
    	generateOperatePage(nameValue,idValue,roleValue);
    }else{
	    //登录
	    showLoginWindow();
	}
    
}

function request(paras)
{ 
    var url = location.href; 
    var paraString = url.substring(url.indexOf("?")+1,url.length).split("&"); 
    var paraObj = {};
    for (var i=0; j=paraString[i]; i++){ 
    	paraObj[j.substring(0,j.indexOf("=")).toLowerCase()] = j.substring(j.indexOf("=")+1,j.length); 
    } 
    var returnValue = paraObj[paras.toLowerCase()]; 
    if(typeof(returnValue)=="undefined"){ 
    	return ""; 
    }else{ 
    	return returnValue; 
    } 
}


//验证邮箱
function checkEmail(){
	var emailStr = $("#email").attr("value");
	var emailPat = new RegExp(/^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/g);
	if (!emailPat.test(emailStr)) {
		$('#prompt').html('邮箱格式不正确');
		return false;
	}
	return true;
}

function validateRegister(){
	var flag = checkEmail();
	if(flag){
		$('#prompt').attr("style","visibility:visible");
		var email = $("#email").attr("value");
		$.post('/ipintu/pintuapi', {
			'method'  : 'checkoutRegister',
			'email'	: email
		}, 
		//回调函数
		function (result) {
			if(result == 1){//result为1，用户已存在
			    $('#prompt').html('<img src="imgs/no.png">');
			    $('#registPrompt').html("*此邮箱已注册");
			}else if(result ==0){
				 $('#prompt').html('<img src="imgs/ok.png">');
				 $('#registPrompt').html('');
			}
		});
	}
}


function checkPwdLength(){
	var pwd =  $("#pwd").attr("value");
	if(pwd.length <6 || pwd.length>8){
		 errorClass();
		 $('#loginPrompt').html('<font color="red">*密码长度6~8位</font>');
	}
}

function checkLoginNull(){
	var email = $("#email").attr("value");
	var pwd =  $("#pwd").attr("value");
	if(email == null || pwd == null || email == "" || pwd == ""){
		return false;
	}
	return true;
}


function checkLogin(){
	var flag = checkLoginNull();
	if(flag){
		var email = $("#email").attr("value");
		var pwd =  $("#pwd").attr("value");
		$.post('/ipintu/pintuapi', {
			'method'  : 'loginAd',
			'email'	: email,
			'pwd' : pwd 
		}, 
		//回调函数
		function (result) {
			if(result.indexOf('admin')>-1 ){
				//转到管理员页面
				var res = result.trim().split("@");
				var role = res[0];
				var venderId = res[1];
				var venderName = res[2];
				rememberMe(venderId,venderName,role);
				generateOperatePage(venderName,venderId,role);
				
			}else if(result.indexOf('vender')>-1){
				//转到普通用户页面
				var res = result.trim().split("@");
				var role = res[0];
				var venderId = res[1];
				var venderName = res[2];
				var state = res[3];
//				if(state == "dead"){
					//如果已到期给出提醒并不予跳转
//					showExpiredWindow();
//				}else{
				 	rememberMe(venderId,venderName,role);
				 	generateOperatePage(venderName,venderId,role);
//				}
				
			}else if( result.trim() == "0"){
				errorClass();
				$('#loginPrompt').html('<font color="red">*密码错误</font>');
			}else if( result.trim() == "-1"){
				errorClass();
				$('#loginPrompt').html('<font color="red">*账户不存在，点击右上角导航注册吧</font>');
			}
		});
	}else{
		//输入为空 将输入框变红出错，并有提示
		errorClass();
		$('#loginPrompt').html('账户和密码不能为空');
	}
}

//cookie存30天
function rememberMe(venderId,venderName,role){
	//选择了记住的存30天，否则存两小时
 	 if( $("#saveCookie").attr("checked")){  
        setCookie("venderId",venderId,24*30,"/");
        setCookie("venderName",venderName,24*30,"/");
        setCookie("role",role,24*30,"/");
     }else{
       setCookie("venderId",venderId,2,"/");
        setCookie("venderName",venderName,2,"/");
        setCookie("role",role,2,"/");
     }
}

function comparePwd(){
	var pwd1 =  $("#pwd1").attr("value");
	var pwd2 =  $("#pwd2").attr("value");
	if((pwd1.length <6 || pwd1.length>8) && (pwd2.length <6 || pwd2.length>8)){
		 errorClass();
		 $('#pwdPrompt').html('<font color="red">*密码长度6~8位</font>');
	}else if(pwd1 != pwd2){
	  	errorClass();
		$('#pwdPrompt').html('<font color="red">*两次输入的密码不一致</font>');
	}
}

function changePwd(){
	var flag = checkPwdNull();
	if(flag){
		var venderId = $("#venderId").attr("value");
		var password =  $("#pwd1").attr("value");
		$.post('/ipintu/pintuapi', {
			'method'  : 'changePwd',
			'venderId'	: venderId,
			'newPwd' : password
		}, 
		//回调函数
		function (result) {
			if(result==1){
				  $('#pwdPrompt').html('<font color="red">*密码修改成功</font>');
			  }else{
			  	  errorClass();
				  $('#pwdPrompt').html('<font color="red">*密码修改失败</font>');
			  }
		});
	}else{
		$('#pwdPrompt').html('<font color="red">*请按要求设置新密码</font>');
	}
}

function checkPwdNull(){
	var pwd1 =  $("#pwd1").attr("value");
	var pwd2 =  $("#pwd2").attr("value");
	if(pwd1==null || pwd1=="" || pwd2==null || pwd2==""){
		return false;
	}else{
		return true;
	}
}

//退出
function exit(){
	//删除cookie
	deleteCookie("venderId","/");
	
	//删除显示区域的内容
	$('#displayArea').children().remove();
	
	//删掉导航栏的内容
	$('#func').remove();
	$('#userInfo').remove();
	
	//显示登录框 
	showLoginWindow();
	
}


//弹出过期提醒
function showExpiredWindow(){
	$('#floatBoxBg').attr("style","display: block");
	msgBox('expiredPrompt', '到期提醒');
}

//错误样式提示
function errorClass(){
 	$("#normalField").addClass("control-group error");
}
//错误样式恢复
function rightClass(){
 	$("#normalField").removeClass("control-group error");
}

//显示登录窗口
function showLoginWindow(){
	$('#func').remove();
	$('<ul class="nav pull-right" id="func"></ul>').insertAfter('#brand');
	$('<li><a href="javascript:showRegisterWindow();"><b>我要注册</b></a></li>').appendTo("#func");
	$('#displayArea').children().remove();
	var html = '<div id="loginDiv">';
    html += '<div class="row"><div class="span3 offset6"><div class="row-fluid">';
	html += '<form class="well">';
	html += '<fieldset id="normalField"><legend>登录</legend>';
	html += '<label class="control-label" for="input01">账户：</label><input type="text" name="email" id="email" onfocus="rightClass();" onblur="checkEmail();"> <span class="help-block">可用邮箱</span>';
	html += '<label class="control-label" for="input01">密码：</label><input type="password" name="pwd" id="pwd" onblur="checkPwdLength()"> <span class="help-block">6~8位密码</span>';									
	html += '<div ><span style="color: blue;">记住我<input id="saveCookie" type="checkbox" value="" /></span>';
	html += '	<span style="visibility: hidden"><input type="text" size="5" /></span>';
	html += ' <button type="button" class="btn" id="submit" onclick="checkLogin();">登录</button></div>';  
	html += '<div id="loginPrompt"></div>';
	html += '</fieldset></form></div></div></div></div>';
	$('#displayArea').append(html);
}


//显示修改密码窗口
function showPwdWindow(id){
	// 删除显示区域的内容
	$('#displayArea').children().remove();
	var html = '<div id="pwdDiv">';
    html += '<div class="row"><div class="span3 offset6"><div class="row-fluid">';
	html += '<form class="well"><input type="hidden" id="venderId" value="'+id+'" />';
	html += '<fieldset id="normalField"><legend>修改密码</legend>';
	html += '<label class="control-label" for="input01">新密码：</label>	<input type="password" name="newPwd" id="pwd1" onclick="rightClass();">';
	html += '<label class="control-label" for="input01">密码：</label><input type="password" name="rePwd" id="pwd2" onblur="comparePwd()"> <span class="help-block">6~8位密码</span>';									
	html += '<div align="center"><button type="button" class="btn" id="submit" onclick="changePwd();">确认修改</button></div>';
	html += '<div id="pwdPrompt"></div>';
	html += '</fieldset></form></div></div></div></div>';
	$('#displayArea').append(html);
}


//显示注册页面
function showRegisterWindow(){
	// 删除显示区域的内容
	$('#func').remove();
	$('#displayArea').children().remove();
	$('<ul class="nav pull-right" id="func"></ul>').insertAfter('#brand');
	$('<li><a href="javascript:showLoginWindow();"><b>我要登录</b></a></li>').appendTo("#func");
	var html = '<div id="registerDiv">';
	html += '<div class="row"><div class="span3 offset6"><div class="row-fluid">';
	html += '<form class="well"><fieldset id="normalField"><legend>注册</legend>';
	html += '<label class="control-label" for="input01">邮箱：</label><input type="text" name="email" id="email" onfocus="rightClass();" onblur="validateRegister();">';
	html += '<span id="prompt" style="visibility:hidden"><img src="imgs/loading.gif" style="vertical-align: middle;"></span>';
	html += '<label class="control-label" for="input01">密码：</label><input type="password" name="pwd" id="pwd" onblur="checkPwdLength()"> <span class="help-block">6~8位密码</span>';									
	html += '<label class="control-label" for="input01">客户：</label><input type="text" name="name" id="name">';
	html += '<label class="control-label" for="input01">域名：</label><input type="text" name="deployDNS" id="deployDNS"> <span class="help-block">http://开头</span>';
	html += '<div align="center"><button type="button" class="btn" id="submit" onclick="venderRegist();">确定</button></div>';
	html += '<div id="registPrompt"></div>';
	html += '</fieldset></form></div></div></div></div>';
	$('#displayArea').append(html);
}

//动态创建用户界面
function generateOperatePage(venderName,venderId,role){
	//导航栏
	$('#displayArea').children().remove();
	generateNavigate(venderName,venderId,role);
	//加载进来之后就要看到广告列表
	generateAdManagement(venderName,venderId);
}

//根据角色来自动生成导航
function generateNavigate(venderName,venderId,role){
	$('#brand').children().remove();
	$('#func').remove();
	$('<ul class="nav pull-left" id="func"></ul>').insertAfter('#brand');
	var para1 = "('"+venderName+"','"+venderId+"')";
		$('<li><a href = "javascript:generateAdManagement'+para1+'">广告管理</a></li>').appendTo('#func');
		$('<li><a href = "javascript:generateAdDownload'+para1+'">安装包下载</a></li>').appendTo('#func');
	if(role == "admin"){
		$('<li><a href = "javascript:generateVenderManagement'+para1+'">厂商管理</a></li>').appendTo('#func');
	}
	
	$('<ul class="nav pull-right" id="userInfo">').insertAfter('#brand');
	var para2 ="'"+venderId+"'";
	$('<li><a href="#">当前登录用户：'+venderName+'</a></li>').appendTo("#userInfo");
	$('<li><a href = "javascript:showPwdWindow('+para2+')">修改密码</a></li>').appendTo('#userInfo');
	$('<li><a href = "javascript:exit()">退出</a></li>').appendTo('#userInfo');
}

//厂商管理
function generateVenderManagement(venderName,venderId){
	$('#displayArea').children().remove();
	generateVenderTabbar();
	generateVenderContent();
}

function generateVenderTabbar(){
	var html='<div class="container innerWell">';
	html+='<span class="label label-info">广告商名称：</span>';
	html+='<input id="keys" type="text" class="search-query" placeholder="关键字" size="15" />';
	html+=' <button id="submit" class="btn" onclick="searchVenders();">查询</button>';
	html+=' <button id="" class="btn" onclick="newVenderWindow();">新建厂商</button>';
	html+=' <button id="" class="btn" onclick="initVenderData();">全部厂商</button>';
	html+='</div>';
	$('#displayArea').append(html);
}

function generateVenderContent(){
	var html='<div class="container blankWell">';
	html+='<table class="table table-bordered  table-striped">';
	html+='<thead><tr><th>序号</th><th>厂商</th><th>邮箱</th><th>部署域名</th><th>创建时间</th>';
	html+='<th>生效时间</th><th>失效时间</th><th>等级</th><th>状态</th><th>编辑</th></tr></thead>';
	html+='<tbody id="venderList"></tbody></table></div>';
	$('#displayArea').append(html);
	//初始化厂商数据
	initVenderData();
}

//广告管理
function generateAdManagement(venderName,venderId){
	$('#displayArea').children().remove();
	generateAdTabbar(venderName,venderId);
	generateAdContent(venderName,venderId);
}

function generateAdTabbar(venderName,venderId){
	var html='<div class="container innerWell">';
	html+='<div class="search"> <input id="keys" type="text"  placeholder="输入广告关键字" size="15" />';
	html+='<button id="submit" class="btn" onclick="searchAds();">查询</button></div>';
	html+='<div class="done"><a href="javascript:newAdWindow()"  title="新建广告"><img src="imgs/cloud_add1.png" alt="新建广告"></a>';
	var para = "('"+venderId+"')";
	html+='<a href="javascript:newPreviewWindow'+para+'"  title="预览广告"><img src="imgs/cloud_preview1.png" alt="预览广告"></a></div>';
	html+='<div class="tip">提示：为保证显示效果，发布广告时请选择尺寸大小相同的图片！</div></div>';
	$('#displayArea').append(html)
		.append($('<input></input')
		.attr("id","venderId")
		.attr("value",venderId)
		.attr("type","hidden"));
}


function generateAdContent(venderName,venderId){
	var html='<div class="container blankWell">';
	html+='<table class="table table-bordered  table-striped">';
	html+='<thead><tr><th>序号</th><th>厂商</th><th>广告分类</th><th width="200" height="30">广告内容</th><th>创建时间</th>';
	html+='<th>生效时间</th><th>失效时间</th><th>优先级</th><th>编辑</th><th>删除</th></tr></thead>';
	html+='<tbody id="adList"></tbody></table></div>';
	$('#displayArea').append(html);
	//初始化数据
	initAdData(venderId);
}


function generateAdDownload(venderName,venderId){
	    $('#displayArea').children().remove();
	    var html='<div class="caption">报雨鸟微广告系统安装包下载</div>';
		html+='<table class="dataintable">';
		html+='<thead><tr><th>版本</th><th>本地版</th><th>网络版</th></tr></thead>';
		var free='free';
		var standard = 'standard';
		var upgrade = 'upgrade';
		var advanced = 'advanced';
		var local='local';
		var web='network';
		
		var para = "('"+venderId+"','"+free+"','"+local+"')";
		html+='<tbody><tr><td>免费版</td><td><a href="javascript:downloadZip'+para+'">点击下载</a></td>';
		
		para = "('"+venderId+"','"+free+"','"+web+"')";
		html+='<td><a href="javascript:downloadZip'+para+'">点击下载</a></td></tr>';
		
		para = "('"+venderId+"','"+standard+"','"+local+"')";
		html+='<tr><td>标准版</td><td><a href="javascript:downloadZip'+para+'">点我下载</a></td>';
		
		para = "('"+venderId+"','"+standard+"','"+web+"')";
		html+='<td><a href="javascript:downloadZip'+para+'">点击下载</a></td></tr>';
		
		para = "('"+venderId+"','"+upgrade+"','"+local+"')";
		html+='<tr><td>升级版</td><td><a href="javascript:downloadZip'+para+'">点击下载</a></td>';
		
		para = "('"+venderId+"','"+upgrade+"','"+web+"')";
		html+='<td><a href="javascript:downloadZip'+para+'">点击下载</a></td></tr>';
		
		para = "('"+venderId+"','"+advanced+"','"+local+"')";
		html+='<tr><td>高级版</td><td><a href="javascript:downloadZip'+para+'">点击下载</a></td>';
		
	    para = "('"+venderId+"','"+advanced+"','"+web+"')";
		html+='<td><a href="javascript:downloadZip'+para+'">点击下载</a></td></tr>';
		
		html+='</tbody></table>';
		html+='<div class="note">说明：本地版与网络版的主要区别是数据来源不同，新注册的用户默认是免费版，请选择对应版本下载，以保证显示效果！</div>';
		$('#displayArea').append(html);
}

function downloadZip(venderId,version,dataSource){
	window.location.href="/ipintu/download_center.do?venderId="+venderId+"&version="+version+"&dataSource="+dataSource;
}




//创建广告图片预览窗口
function newPreviewWindow(venderId){
	
	//调用函数居中窗口
	centerPopup();   
	//调用函数加载窗口
	loadPopup();   
	
	//初始化flash
	initFlash(venderId);
	
	//默认显示的标准版
	showSwf('freedemoDiv');
	
}

//初始化：是否开启DIV弹出窗口功能
//0 表示开启; 1 表示不开启;
var popupStatus = 0;

//使用Jquery加载弹窗 
function loadPopup(){   
	//仅在开启标志popupStatus为0的情况下加载  
	if(popupStatus==0){   
	$("#backgroundPopup").css({   
	"opacity": "0.7"  
	});   
	$("#backgroundPopup").fadeIn("slow");   
	$("#popupContact").fadeIn("slow");   
	popupStatus = 1;   
	} 
}  

//使用Jquery去除弹窗效果 
function disablePopup(){   
	//仅在开启标志popupStatus为1的情况下去除
	if(popupStatus==1){   
	$("#backgroundPopup").fadeOut();   
	$("#popupContact").fadeOut();   
	popupStatus = 0;   
	}   
}  

//将弹出窗口定位在屏幕的中央
function centerPopup(){   
	//获取系统变量
	var windowWidth = document.documentElement.clientWidth;   
	var windowHeight = document.documentElement.clientHeight;   
	var popupHeight = $("#popupContact").height();   
	var popupWidth = $("#popupContact").width();   
	//居中设置   
	$("#popupContact").css({   
	"position": "absolute",   
	"top": windowHeight/2-popupHeight/2,   
	"left": windowWidth/2-popupWidth/2   
	});   
	//以下代码仅在IE6下有效
	  
	$("#backgroundPopup").css({   
	"height": windowHeight   
	});   
}

function initFlash(venderId){
	createSWFById('freedemoDiv',venderId,288,48,"free");
	createSWFById('standarddemoDiv',venderId,250,250,"standard");	
	createSWFById('upgradedemoDiv',venderId,500,500,"upgrade");
	createSWFById('advanceddemoDiv',venderId,1000,300,"advanced");
	
//divId,venderId,width,height,free
}


function createSWFById(divId, accountId, width, height, version){
            var flashvars = {};                                                         
            flashvars.runningMode = "debug";                      
            flashvars.visualWidth = width;                                    
            flashvars.visualHeight = height;                                   
            flashvars.accountId = accountId;            
            flashvars.dataType = "network";
            flashvars.versionType = version;                       
            flashvars.effectMode = "random";           	
           	flashvars.stayTime = 3;
          // 	flashvars.host = "localhost:8080";
            var swfVersionStr = "10.2.0";           
            var xiSwfUrlStr = "js/playerProductInstall.swf";             
            var params = {};
            params.quality = "high";
            params.bgcolor = "#ffffff";
            params.allowscriptaccess = "sameDomain";
            params.allowfullscreen = "true";
            var attributes = {};
            attributes.id = divId;
            attributes.name = divId;
            attributes.align = "middle";
            swfobject.embedSWF("js/MiniAds.swf", divId, flashvars.visualWidth, flashvars.visualHeight, swfVersionStr, xiSwfUrlStr,flashvars, params, attributes);            
            swfobject.createCSS("#"+divId, "display:none;text-align:left;");	
}

function showSwf(divId){
  swfobject.createCSS("#"+divId, "display:block;text-align:left;");
}


//键盘按下ESC时关闭窗口!
document.onkeydown=function(e){   
	var theEvent = window.event || e; 
	var code = theEvent.keyCode || theEvent.which; 
	if (code == 27  && popupStatus==1) { 
		disablePopup();   
	} 
}  
