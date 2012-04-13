//初始化广告数据
function initAdData(venderId){
	$("#adList").html("");
	var venderId=$('#venderId').val();
	$.post('/ipintu/pintuapi', {
		'method'  : 'searchAds',
		'venderId' : venderId,
		'keys' : "",
		'time' : ""
	}, 
	//回调函数
	function (result) {
		if(result.length > 0){
			for( key in result ){
				
				generateAdTr(key);
			
				generateNewTd(parseInt(key)+1,key);
				generateNewTd(result[key].vender,key);
				var type;
				if(result[key].type == "image"){
					type = "纯图片广告"
				}else{
					type = "纯文字广告";
				}
				generateNewTd(type,key);
				generateContentTd(result[key].type,result[key].content,result[key].imgPath,result[key].link,key);
				generateNewTd(result[key].createTime,key);
				generateNewTd(result[key].startTime,key);
				generateNewTd(result[key].endTime,key);
				generateNewTd(result[key].priority,key);
				
				generateAdOperate(result[key].id,key,venderId);
			}
		}
	}, "json");

}

//生成各tr
function generateAdTr(key){
	$('<tr></tr>').appendTo($('#adList'))
	.attr("id","line"+key);
}

//生成各td
function generateNewTd(txt,key){
	$('<td></td>').appendTo($('#line'+key))
	.text(txt);
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
function generateAdOperate(id,key,venderId){
	$('<td></td').appendTo($('#line'+key))
	.append($('<a></a>')
		.append($('<img>').attr("src","imgs/edit.png"))
		.attr("href","javascript:getAdsById("+key+",'"+venderId+"');"))
	.append($('<input></input')
		.attr("id","adId"+key)
		.attr("value",id)
		.attr("type","hidden"));
	
	$('<td></td').appendTo($('#line'+key))
	.append($('<a></a>')
		.append($('<img>').attr("src","imgs/del.png"))
		.attr("href","javascript:deleteAdsById("+key+",'"+venderId+"');"))
	.append($('<input></input')
		.attr("id","adId"+key)
		.attr("value",id)
		.attr("type","hidden"));
}

//按条件查询
function searchAds(){
	var keys = $("#keys").attr("value");
	var time =  $("#time").attr("value");
	var venderId = $("#venderId").attr("value");
	if((keys ==null || keys =="")&&(time ==null || time=="")){
		return false;
	}
	$("#adList").html("");
	$.post('/ipintu/pintuapi', {
		'method'  : 'searchAds',
		'venderId' : venderId,
		'keys' : keys,
		'time' : time
	}, 
	//回调函数
	function (result) {
		if(result.length > 0){
			for( key in result ){
				
				generateAdTr(key);
			
				generateNewTd(parseInt(key)+1,key);
				generateNewTd(result[key].vender,key);
				generateNewTd(result[key].type,key);
				generateContentTd(result[key].type,result[key].content,result[key].imgPath,result[key].link,key);
				generateNewTd(result[key].createTime,key);
				generateNewTd(result[key].startTime,key);
				generateNewTd(result[key].endTime,key);
				generateNewTd(result[key].priority,key);
				
				generateAdOperate(result[key].id,key,venderId);
			}
		}
	}, "json");
}

function confirmDel(){
	    if(confirm("确定要删除吗？删除将不能恢复！"))
	   		 return true;
	    else
	   		 return false;
}

//删除
function deleteAdsById(key,venderId){
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
			initAdData(venderId);
		});
	}
}

function newAdWindow(){
	$('#floatBoxBg').attr("style","display: block");
	$("#adId").val("");
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

//编辑
function getAdsById(key,venderId){
	var adId=$("#adId"+key).attr("value");
	//根据id查出广告详情并填充newAd用于修改
	$.post('/ipintu/pintuapi', {
		'method'  : 'getAdsById',
		'adId' : adId
	}, 
	//回调函数
	function (result) {
		if(result != "[]"){
			createEditWindow(result,venderId);
		}else{
			alert("获取广告详情有误，请重试！");
		}
	}, "json");
}

function createEditWindow(result,venderId){
	$('#floatBoxBg').attr("style","display: block");
	msgBox('editAd', '内容编辑');
	$("#adId").val(result.id);
	$("#venderId").val(venderId);
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

function operateAd(){
	var id = $("#adId").attr("value");
	var venderId = $("#vender").attr("value");
	var type = $("#type").attr("value");
	if(id == null || id==""){
		createAd();
	}else{
		updateAd(id);
	}
}

//编辑广告
function updateAd(adId){
	var flag = checkAd();
	var type =  $("#type").attr("value");
	var priority = $("#priority").attr("value");
	var startTime = $("#startTime").attr("value");
	var endTime =  $("#endTime").attr("value");
	var content = $("#content").attr("value");
	var link = $("#link").attr("value");
	var venderId = $("#venderId").attr("value");
	if(type == "image"){
		content = "";
	}
	if(flag){
		$.post('/ipintu/pintuapi', {
			'method'  : 'updateAdsById',
			'venderId' : venderId,
			'adId' : adId,
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
			initAdData(venderId);
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
	var flag = checkAd();
	var type =  $("#type").attr("value");
	var priority = $("#priority").attr("value");
	var startTime = $("#startTime").attr("value");
	var endTime =  $("#endTime").attr("value");
	var content = $("#content").attr("value");
	var link = $("#link").attr("value");
	var venderId = $("#venderId").attr("value");
	if(type == "image"){
		content = "";
	}
	if(flag){
		$.post('/ipintu/pintuapi', {
			'method'  : 'createAds',
			'venderId' : venderId,
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
			initAdData(venderId);
			 //关闭弹出窗口
			 msgBox_close();
		});
	}else{
		 $('#adPrompt').attr("style","visibility:visible");
	   	$("#info").html("<font color='red'>提示：填写广告有误，请重试！</font>");
	}
}


//检查新广告字段是否全部填写
function checkAd(){
	var type =  $("#type").attr("value");
	var priority = $("#priority").attr("value");
	var startTime = $("#startTime").attr("value");
	var endTime =  $("#endTime").attr("value");
	var content = $("#content").attr("value");
	var imgPath = $("#imgPath").attr("value");
	var venderId = $("#venderId").attr("value");
	var link = $("#link").attr("value");
	if(type == "text"){

		if(venderId==null || venderId=="" || type == null || type == ""  || priority == null || priority == ""
			|| content == ""|| content ==null || startTime == null || startTime == "" || endTime == null || endTime == ""){
				return false;
		}
	}else{
		if(imgPath == null || imgPath == ""){
			  $('#imgPrompt').attr("style","display:block");
			  $("#imgPrompt").html("<font color='red'>提示：请选择图片并上传</font>");
			  return false;
		}
		if(venderId==null || venderId=="" || type == null || type == ""  || priority == null || priority == ""
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
 	  var userId=document.getElementById("venderId").value; 
	  if(userId.replace(/\s/g,'') == ''){
			return false;
	  }  
      //上传
 	  jQuery('#uploadify').uploadifyUpload() 	 		 
 }





