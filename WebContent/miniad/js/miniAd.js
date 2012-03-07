document.onkeydown = function (e) { 
	var theEvent = window.event || e; 
	var code = theEvent.keyCode || theEvent.which; 
	if (code == 13) { 
		$("#submit").click(); 
	} 
} 

function bindSelect(){
	var type = $("#type").attr("value");
	 $('#adImg').attr("style","display:none");
	 $('#adTxt').attr("style","visibility:hidden");
	 $('#imgPrompt').attr("style","display:none");
	 $('#adPrompt').attr("style","display:none");
	if(type == "image"){
		 $('#adImg').attr("style","display:block");
	}else{
		 $('#adTxt').attr("style","visibility:visible");
	}
}

function getAllAds(){
	$("#adList").html("");
	$.post('/ipintu/pintuapi', {
		'method'  : 'searchAds',
		'keys' : "",
		'time' : ""
	}, 
	//回调函数
	function (result) {
		if(result.length > 0){
			for( key in result ){
				
				generateNewTr(key);
			
				generateNewTd(parseInt(key)+1,key);
				generateNewTd(result[key].vender,key);
				generateNewTd(result[key].type,key);
				generateContentTd(result[key].type,result[key].content,result[key].imgPath,result[key].link,key);
				generateNewTd(result[key].createTime,key);
				generateNewTd(result[key].startTime,key);
				generateNewTd(result[key].endTime,key);
				generateNewTd(result[key].priority,key);
				
				generateOperate(result[key].id,key);
			}
		}
	}, "json");
}

//按条件查询
function searchAds(){
	var keys = $("#keys").attr("value");
	var time =  $("#time").attr("value");
	if((keys ==null || keys =="")&&(time ==null || time=="")){
		return false;
	}
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
				generateNewTd(result[key].vender,key);
				generateNewTd(result[key].type,key);
				generateContentTd(result[key].type,result[key].content,result[key].imgPath,result[key].link,key);
				generateNewTd(result[key].createTime,key);
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

function generateContentTd(type,content,imgPath,link,key){
	var tr = document.getElementById("line"+key);
	var para = document.createElement("td");
	var a = document.createElement("a")
	para.appendChild(a);
	if(type == "text"){
		var node = document.createTextNode(content);
		a.appendChild(node);
		a.setAttribute("href",link);
	}else{
		var img = document.createElement("img");
		var local = '/ipintu/pintuapi?method=getImgByRelativePath&relativePath=';
		img.setAttribute("src",local+imgPath);
		img.setAttribute("height",36);
		a.appendChild(img);
		a.setAttribute("href",link);
	}
	tr.appendChild(para);
}

//生成 编辑和删除 选项
function generateOperate(id,key){

	var tr = document.getElementById("line"+key);
	
	var para = document.createElement("input");
	para.setAttribute("id","adId"+key);
	para.setAttribute("value",id);
	para.setAttribute("type","hidden");
	
	var para1 = document.createElement("td");
	var edit = document.createElement("img");
	edit.setAttribute("src","imgs/edit.png");
	var aEdit = document.createElement("a");
	aEdit.setAttribute("href","javascript:getAdsById("+key+");");
	aEdit.appendChild(edit);
	para1.appendChild(aEdit);
	para1.appendChild(para);
	tr.appendChild(para1);
	
	var para2 = document.createElement("td");
	var del = document.createElement("img");
	del.setAttribute("src","imgs/del.png");
	var aDel = document.createElement("a");
	aDel.setAttribute("href","javascript:deleteAdsById("+key+");");
	aDel.appendChild(del);
	para2.appendChild(aDel);
	aDel.appendChild(del);
	para2.appendChild(para);
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
			 getAllAds();
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
	$('#floatBoxBg').attr("style","display: block");
	msgBox('editAd', '内容编辑');
	$("#adId").val(result.id);
	$("#vender").val(result.vender);
	$("#type").val(result.type);
	//这里加上根据type来判断是否图片框可用
	bindSelect();
	$("#priority").val(result.priority);
	$("#startTime").val(result.startTime);
	$("#endTime").val(result.endTime);
	$("#content").val(result.content);
	$("#link").val(result.link);
	$("#imgPath").val("");
}

function newAdWindow(){
	$('#floatBoxBg').attr("style","display: block");
	$("#adId").val("");
	$("#vender").val("");
	$("#type").val("");
	$("#priority").val("");
	$("#startTime").val("");
	$("#endTime").val("");
	$("#content").val("");
	$("#uploadify").val("");
	$("#imgPath").val("");
	$("#link").val("");
	bindSelect();
	msgBox('editAd', '新建广告');
}


function operateAd(){
	var id = $("#adId").attr("value");
	var type = $("#type").attr("value");
	if(id == null || id==""){
		createAd();
	}else{
		updateAd(id);
	}
}

//编辑广告
function updateAd(adId){
	var flag = check();
	var vender = $("#vender").attr("value");
	var type =  $("#type").attr("value");
	var priority = $("#priority").attr("value");
	var startTime = $("#startTime").attr("value");
	var endTime =  $("#endTime").attr("value");
	var content = $("#content").attr("value");
	var link = $("#link").attr("value");
	if(type == "image"){
		content = "";
	}
	if(flag){
		$.post('/ipintu/pintuapi', {
			'method'  : 'updateAdsById',
			'adId' : adId,
			'vender' : vender,
			'type' : type,
			'priority' : priority,
			'startTime' : startTime,
			'endTime' : endTime,
			'content' : content,
			'link' : link
		}, 
	//回调函数
		function (result) {
			if(result.trim() == 'Operate Failed!'){
				 $('#adPrompt').attr("style","visibility:visible");
	   			 $("#info").html("<font color='red'>提示：更新广告有误，请重试！</font>");
//				 alert("操作有误，请重试！");
		     }
		     //操作后刷新列表
			getAllAds();
			 //关闭弹出窗口
			 msgBox_close();
		});
	}else{
		 $('#adPrompt').attr("style","visibility:visible");
	   	 $("#info").html("<font color='red'>提示：填写广告有误，请重试！</font>");
	}
}

//新建广告
function createAd(){
	var flag = check();
	var vender = $("#vender").attr("value");
	var type =  $("#type").attr("value");
	var priority = $("#priority").attr("value");
	var startTime = $("#startTime").attr("value");
	var endTime =  $("#endTime").attr("value");
	var content = $("#content").attr("value");
	var link = $("#link").attr("value");
	if(type == "image"){
		content = "";
	}
	if(flag){
		$.post('/ipintu/pintuapi', {
			'method'  : 'createAds',
			'vender' : vender,
			'type' : type,
			'priority' : priority,
			'startTime' : startTime,
			'endTime' : endTime,
			'content' : content,
			'link' : link
		}, 
		//回调函数
		function (result) {
			if(result.trim() == 'Operate Failed!'){
				 $('#adPrompt').attr("style","visibility:visible");
	   			 $("#info").html("<font color='red'>提示：创建广告有误，请重试！</font>");
			 }
			 //操作后刷新列表
			getAllAds();
			 //关闭弹出窗口
			 msgBox_close();
		});
	}else{
		 $('#adPrompt').attr("style","visibility:visible");
	   	$("#info").html("<font color='red'>提示：填写广告有误，请重试！</font>");
	}
}


//检查新广告字段是否全部填写
function check(){
	var vender = $("#vender").attr("value");
	var type =  $("#type").attr("value");
	var priority = $("#priority").attr("value");
	var startTime = $("#startTime").attr("value");
	var endTime =  $("#endTime").attr("value");
	var content = $("#content").attr("value");
	var imgPath = $("#imgPath").attr("value");
	var link = $("#link").attr("value");
	if(type == "text"){
		if(vender == null || vender == "" || type == null || type == ""  || priority == null || priority == ""
			|| content == ""|| content ==null || startTime == null || startTime == "" || endTime == null || endTime == ""){
				return false;
		}
	}else{
		if(imgPath == null || imgPath == ""){
			  $('#imgPrompt').attr("style","display:block");
			  $("#imgPrompt").html("<font color='red'>提示：请选择图片并上传</font>");
			  return false;
		}
		if(vender == null || vender == "" || type == null || type == ""  || priority == null || priority == ""
			 || startTime == null || startTime == "" || endTime == null || endTime == ""){
				return false;
		}
	}
	return true;
}

 $(document).ready(function() {
	  $("#uploadify").uploadify({
	   'uploader'       : 'js/uploadify.swf',
	   'script'         : '/ipintu/pintuapi',//servlet的路径,这是访问servlet 'scripts/uploadif' 
	   'method'         :'POST',  //如果要传参数，就必须改为GET
	   'cancelImg'      : 'imgs/cancel.png',
       'buttonImg'      : 'imgs/select.png',
	   'folder'         : 'WEB-INF/uploadFile/adsImg', //要上传到的服务器路径，
	   'queueID'        : 'fileQueue',
	   'auto'           : false, //选定文件后是否自动上传，默认false
	   'multi'          : false, //是否允许同时上传多文件，默认false
	   'simUploadLimit' : 1, //一次同步上传的文件数目  
	   'sizeLimit'      : 19871202, //设置单个文件大小限制，单位为byte  
	   'queueSizeLimit' : 3, //限制在一次队列中的次数（可选定几个文件）。默认值= 999，而一次可传几个文件有 simUploadLimit属性决定。
	   'fileDesc'       : '支持格式:jpg,png或gif', //如果配置了以下的'fileExt'属性，那么这个属性是必须的  
	   'fileExt'        : '*.jpg;*.gif;*.png',//允许的格式
	   'scriptData'     :{'userId':$('#userId').val()}, // 多个参数用逗号隔开 'name':$('#name').val(),'num':$('#num').val(),'ttl':$('#ttl').val()
	   　onComplete: function (event, queueID, fileObj, response, data) {
	   			 var value = response ;//返回的图片路径 
	   			 $('#imgPrompt').attr("style","display:block");
	   			 $("#imgPrompt").html("<font color='red'>提示：图片上传成功</font>");
	   			 $("#imgPath").attr("value",value);
	   			 $('#adPrompt').attr("style","visibility:hidden");
	   　  //  alert("文件:" + fileObj.name + "上传成功");
	   　},  
	   　onError: function(event, queueID, fileObj, errorObj) {  
	   			  $('#imgPrompt').attr("style","display:block");
	  			  $("#imgPrompt").html("<font color='red'>提示：图片上传失败</font>");
	   　 //	  alert("文件:" + fileObj.name + "上传失败");  
	   　},  
	   　onCancel: function(event, queueID, fileObj){  
	   			 $('#imgPrompt').attr("style","display:block");
	   			 $("#imgPrompt").html("<font color='red'>提示：图片上传取消了</font>");
	   　 	//	alert("取消了" + fileObj.name);  
	   　} 
	  });
 });
		 
		 
 function uploadsFile(){ 
 	  //校验
 	  var userId=document.getElementById("userId").value; 
	  if(userId.replace(/\s/g,'') == ''){
			return false;
	  }  
      //上传
 	  jQuery('#uploadify').uploadifyUpload() 	 		 
 }


