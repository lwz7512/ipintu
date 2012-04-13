//生成各tr
function generateVenderTr(key){
	$('<tr></tr>').appendTo($('#venderList'))
	.attr("id","line"+key);
}

//生成各td
function generateNewTd(txt,key){
	$('<td></td>').appendTo($('#line'+key))
	.text(txt);
}

function generateVenderOperate(id,key){
	$('<td></td').appendTo($('#line'+key))
	.append($('<a></a>')
		.append($('<img>').attr("src","imgs/edit.png"))
		.attr("href","javascript:getVendersById("+key+");"))
	.append($('<input></input')
		.attr("id","venderId"+key)
		.attr("value",id)
		.attr("type","hidden"));
}

function initVenderData(){
	$("#venderList").html("");
	$.post('/ipintu/pintuapi', {
		'method'  : 'searchVenders',
		'keys' : ''
	}, 
	//回调函数
	function (result) {
		if(result.length > 0){
			for( key in result ){
				
				generateVenderTr(key);
			
				generateNewTd(parseInt(key)+1,key);
				generateNewTd(result[key].name,key);
				generateNewTd(result[key].email,key);
				generateNewTd(result[key].deployDNS,key);
				generateNewTd(result[key].createTime,key);
				generateNewTd(result[key].effectiveTime,key);
				generateNewTd(result[key].deadTime,key);
				
				var serviceLevel;
				if(result[key].serviceLevel == "free"){
					serviceLevel="免费版";
				}else	if(result[key].serviceLevel == "standard"){
					serviceLevel="标准版";
				}else	if(result[key].serviceLevel == "upgrade"){
					serviceLevel="升级版";
				}else	if(result[key].serviceLevel == "advanced"){
					serviceLevel="高级版";
				}
				generateNewTd(serviceLevel,key);
				var enable;
				if(result[key].enable==1){
					enable = "可用";
				}else{
					enable = "不可用";
				}
				generateNewTd(enable,key);
				generateVenderOperate(result[key].id,key);
			}
		}
	}, "json");
}

//按条件查询
function searchVenders(){
	var keys = $("#keys").attr("value");
	if((keys ==null || keys =="")){
		return false;
	}
	$("#venderList").html("");
	$.post('/ipintu/pintuapi', {
		'method'  : 'searchVenders',
		'keys' : keys
	}, 
	//回调函数
	function (result) {
		if(result.length > 0){
			for( key in result ){
				
				generateVenderTr(key);
			
				generateNewTd(parseInt(key)+1,key);
				generateNewTd(result[key].name,key);
				generateNewTd(result[key].email,key);
				generateNewTd(result[key].deployDNS,key);
				generateNewTd(result[key].createTime,key);
				generateNewTd(result[key].effectiveTime,key);
				generateNewTd(result[key].deadTime,key);
				generateNewTd(result[key].serviceLevel,key);
				generateNewTd(result[key].enable,key);
				
				generateVenderOperate(result[key].id,key);
			}
		}
	}, "json");
}

//编辑
function getVendersById(key){
	var venderId=$("#venderId"+key).attr("value");
	$.post('/ipintu/pintuapi', {
		'method'  : 'getVendersById',
		'venderId' : venderId
	}, 
	//回调函数
	function (result) {
		if(result != "[]"){
			createVenderEditWindow(result);
		}else{
			alert("获取厂商详情有误，请重试！");
		}
	}, "json");
}

function createVenderEditWindow(result){
	$('#floatBoxBg').attr("style","display: block");
	msgBox('editVender', '内容编辑');
	$("#venderId").val(result.id);
	$("#name").val(result.name);
	$("#email").val(result.email);
	$("#effectiveTime").val(result.effectiveTime);
	$("#deadTime").val(result.deadTime);
	$("#serviceLevel").val(result.serviceLevel);
	$("#deployDNS").val(result.deployDNS);
	$("#enable").val(result.enable);
}

function newVenderWindow(){
	$('#floatBoxBg').attr("style","display: block");
	$("#venderId").val('');
	$("#name").val('');
	$("#email").val('');
	$("#effectiveTime").val('');
	$("#deadTime").val('');
	$("#serviceLevel").val('');
	$("#deployDNS").val('');
	$("#enable").val('');
	msgBox('editVender', '新建厂商');
}

function operateVender(){
	var id = $("#venderId").attr("value");
	if(id == null || id==""){
		createVender();
	}else{
		updateVender(id);
	}
}

//编辑厂商
function updateVender(venderId){
	var flag = check();
	var name = $("#name").attr("value");
	var email =  $("#email").attr("value");
	var serviceLevel = $("#serviceLevel").attr("value");
	var enable = $("#enable").attr("value");
	var effectiveTime =  $("#effectiveTime").attr("value");
	var deadTime = $("#deadTime").attr("value");
	var deployDNS = $("#deployDNS").attr("value");
	if(flag){
		$.post('/ipintu/pintuapi', {
			'method'  : 'updateVendersById',
			'venderId' : venderId,
			'name' : name,
			'email' : email,
			'serviceLevel' : serviceLevel,
			'effectiveTime' : effectiveTime,
			'deadTime' : deadTime,
			'serviceLevel' : serviceLevel,
			'deployDNS' : deployDNS,
			'enable' : enable
		}, 
	//回调函数
		function (result) {
			if(result.trim() == 'Operate Failed!'){
				 $('#venderPrompt').attr("style","visibility:visible");
	   			 $("#info").html("<font color='red'>提示：更新厂商有误，请重试！</font>");
//				 alert("操作有误，请重试！");
		     }
		     //操作后刷新列表
			initVenderData();
			 //关闭弹出窗口
			 msgBox_close();
		});
	}else{
		 $('#venderPrompt').attr("style","visibility:visible");
	   	 $("#info").html("<font color='red'>提示：填写厂商有误，请重试！</font>");
	}
}

//新建厂商
function venderRegist(){
	var flag = checkRegister();
	var name = $("#name").attr("value");
	var email =  $("#email").attr("value");
	var pwd = $("#pwd").attr("value");
	var deployDNS = $("#deployDNS").attr("value");
	if(flag){
		$.post('/ipintu/pintuapi', {
			'method'  : 'registVenders',
			'name' : name,
			'email' : email,
			'pwd' : pwd,
			'deployDNS' : deployDNS
		}, 
		//回调函数
		function (result) {
			if(result.trim() == 'Operate Success!'){
				  $('#registPrompt').html('<font color="red">*注册成功，点击右上角导航登录吧！</font>');
			}else{
			  	  errorClass();
				  $('#registPrompt').html('<font color="red">*注册客户失败</font>');
			}
			$('#submit').attr("disabled","true");
		});
	}else{
		$('#registPrompt').html('<font color="red">*注册信息填写有误</font>');
	}
}

function checkRegister(){
	var name = $("#name").attr("value");
	var email =  $("#email").attr("value");
	var pwd = $("#pwd").attr("value");
	var deployDNS = $("#deployDNS").attr("value");
	if(name == null || name =="" || email == null || email =="" || pwd == null || pwd =="" || deployDNS == null || deployDNS =="")
	{
		return false;
	}
	return true;
}

//新建厂商
function createVender(){
	var flag = check();
	var name = $("#name").attr("value");
	var email =  $("#email").attr("value");
	var serviceLevel = $("#serviceLevel").attr("value");
	var enable = $("#enable").attr("value");
	var effectiveTime =  $("#effectiveTime").attr("value");
	var deadTime = $("#deadTime").attr("value");
	var deployDNS = $("#deployDNS").attr("value");
	if(flag){
		$.post('/ipintu/pintuapi', {
			'method'  : 'createVenders',
			'name' : name,
			'email' : email,
			'serviceLevel' : serviceLevel,
			'effectiveTime' : effectiveTime,
			'deadTime' : deadTime,
			'serviceLevel' : serviceLevel,
			'deployDNS' : deployDNS,
			'enable' : enable
		}, 
		//回调函数
		function (result) {
			if(result.trim() == 'Operate Failed!'){
				 $('#venderPrompt').attr("style","visibility:visible");
	   			 $("#info").html("<font color='red'>提示：创建厂商有误，请重试！</font>");
			 }
			 //操作后刷新列表
			initVenderData();
			 //关闭弹出窗口
			 msgBox_close();
		});
	}else{
		 $('#venderPrompt').attr("style","visibility:visible");
	   	$("#info").html("<font color='red'>提示：填写厂商有误，请重试！</font>");
	}
}


//检查新厂商字段是否全部填写
function check(){
	var name = $("#name").attr("value");
	var email =  $("#email").attr("value");
	var serviceLevel = $("#serviceLevel").attr("value");
	var enable = $("#enable").attr("value");
	var effectiveTime =  $("#effectiveTime").attr("value");
	var deadTime = $("#deadTime").attr("value");
	var deployDNS = $("#deployDNS").attr("value");
	if(name == null || name == "" || email == null || email == ""  || serviceLevel == null || serviceLevel == ""
			|| enable == ""|| enable ==null || effectiveTime == null || effectiveTime == ""
			|| deadTime == null || deadTime == "" || deployDNS == null || deployDNS == ""){
				return false;
	}else{
		return true;
	}
}


