document.onkeydown = function (e) { 
	var theEvent = window.event || e; 
	var code = theEvent.keyCode || theEvent.which; 
	if (code == 13) { 
		$("#submit").click(); 
	} 
} 

function bindSelect(){
	var type = $("#type").attr("value");
	document.getElementById("imgPath").removeAttribute("disabled");
	if(type == "wenzi"){
		$('#imgPath').attr("disabled","true");
	}
}

//按条件查询
function searchAds(){
	var keys = $("#keys").attr("value");
	var time =  $("#time").attr("value");
	$("#adList").html("");
	$.post('/ipintu/pintuapi', {
		'method'  : 'searchAds',
		'keys' : keys,
		'time' : time
	}, 
	//回调函数
	function (result) {
		if(result.length > 0){
			for( key in result ){
				
				generateNewTr(key);
			
				generateNewTd(parseInt(key)+1,key);
				generateNewTd(result[key].vendor,key);
				generateNewTd(result[key].content,key);
				generateNewTd(result[key].startTime,key);
				generateNewTd(result[key].endTime,key);
				generateNewTd(result[key].priority,key);
				
				generateOperate(result[key].id,key);
			}
		}
	}, "json");
}

//生成各tr
function generateNewTr(key){
	var adList = document.getElementById("adList");
	var para = document.createElement("tr");
	para.setAttribute("id","line"+key);
	adList.appendChild(para);
}

//生成各td
function generateNewTd(txt,key){
	var tr = document.getElementById("line"+key);
	var para = document.createElement("td");
	var node = document.createTextNode(txt);
	para.appendChild(node);
	tr.appendChild(para);
}

//生成 编辑和删除 选项
function generateOperate(id,key){

	var tr = document.getElementById("line"+key);
	
	var editPara = document.createElement("input");
	editPara.setAttribute("id","adId"+key);
	editPara.setAttribute("value",id);
	editPara.setAttribute("type","hidden");
	
	var para1 = document.createElement("td");
	var edit = document.createElement("img");
	edit.setAttribute("src","imgs/edit.png");
	edit.setAttribute("onclick","getAdsById("+key+");");
	para1.appendChild(edit);
	para1.appendChild(editPara);
	tr.appendChild(para1);
	
	var delPara = document.createElement("input");
	delPara.setAttribute("id","adId"+key);
	delPara.setAttribute("value",id);
	delPara.setAttribute("type","hidden");
	
	var para2 = document.createElement("td");
	var del = document.createElement("img");
	del.setAttribute("src","imgs/del.png");
	del.setAttribute("onclick","deleteAdsById("+key+");");
	para2.appendChild(del);
	para2.appendChild(delPara);
	tr.appendChild(para2);
}

//删除
function deleteAdsById(key){
	if(confirmDel()){
		var adId=$("#adId"+key).attr("value");
		$.post('/ipintu/pintuapi', {
			'method'  : 'deleteAdsById',
			'adId' : adId
		}, 
		//回调函数
		function (result) {
			if(result.trim() == 'Operate Failed!'){
				 alert("操作有误，请重试！");
		     }
		     //操作后刷新列表
			 searchAds();
		});
	}
}

function confirmDel(){
	    if(confirm("确定要删除吗？删除将不能恢复！"))
	   		 return true;
	    else
	   		 return false;
}


//编辑
function getAdsById(key){
	var adId=$("#adId"+key).attr("value");
	//根据id查出广告详情并填充newAd用于修改
	$.post('/ipintu/pintuapi', {
		'method'  : 'getAdsById',
		'adId' : adId
	}, 
	//回调函数
	function (result) {
		if(result != "[]"){
			createEditWindow(result);
		}else{
			alert("获取广告详情有误，请重试！");
		}
	}, "json");
}


function createEditWindow(result){
	msgBox('editAd', '广告内容编辑');
	$("#adId").val(result.id);
	$("#vendor").val(result.vendor);
	$("#type").val(result.type);
	//这里加上根据type来判断是否图片框可用
	bindSelect();
	$("#priority").val(result.priority);
	$("#startTime").val(result.startTime);
	$("#endTime").val(result.endTime);
	$("#content").val(result.content);
	$("#imgPath").val(result.imgPath);
	$("#link").val(result.link);
	//这里差了图片没有处理
}

function newAdWindow(){
	$("#adId").val("");
	$("#vendor").val("");
	$("#type").val("");
	$("#priority").val("");
	$("#startTime").val("");
	$("#endTime").val("");
	$("#content").val("");
	$("#imgPath").val("");
	$("#link").val("");
	msgBox('editAd', '新广告创建');
}


function operateAd(){
	var id = $("#adId").attr("value");
	if(id == null || id==""){
		createAd();
	}else{
		updateAd(id);
	}
}

//编辑广告
function updateAd(adId){
	var flag = check();
	alert("update"+adId);
	//-------------------------------------------------
	//这里考虑能不能传数组，内容太多了啊
	if(flag){
		$.post('/ipintu/pintuapi', {
			'method'  : 'updateAdsById',
			'adId' : adId
		}, 
	//回调函数
		function (result) {
			if(result.trim() == 'Operate Failed!'){
				 alert("操作有误，请重试！");
		     }
		     //操作后刷新列表
			 searchAds();
		});
	}else{
		alert("更新广告信息有误，请重试！");
	}
}

//新建广告
function createAd(){
	var flag = check();
	if(flag){
		alert(flag);
		//----------------------------------
		$.post('/ipintu/pintuapi', {
			'method'  : 'createAds',
			'vendor' : "爱品图" 
		}, 
		//回调函数
		function (result) {
			if(result.trim() == 'Operate Failed!'){
				 alert("新建广告操作有误，请重试！");
			 }
			 //操作后刷新列表
			 searchAds();
		});
	}else{
		alert("新建广告填写有误，请重试！");
	}
}


//检查新广告字段是否全部填写
function check(){
	var vendor = $("#vendor").attr("value");
	var type =  $("#type").attr("value");
	var priority = $("#priority").attr("value");
	var startTime = $("#startTime").attr("value");
	var endTime =  $("#endTime").attr("value");
	var content = $("#content").attr("value");
	var link = $("#link").attr("value");
	if(type == "tupian"){
		var imgPath = $("#imgPath").attr("value"); 
		if(vendor == null || vendor == "" || type == null || type == ""  || priority == null || priority == ""
			 || startTime == null || startTime == "" || endTime == null || endTime == "" || imgPath == null || imgPath == ""
			 || content == null || content == "" || link == null || link == ""){
		
				return false;
		}
	}else{
		if(vendor == null || vendor == "" || type == null || type == ""  || priority == null || priority == ""
			 || startTime == null || startTime == "" || endTime == null || endTime == "" 
			 || content == null || content == "" || link == null || link == ""){
		
				return false;
		}
	}
	return true;
}


