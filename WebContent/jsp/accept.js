function approveAction(id, account){
	$.get('http://192.168.1.110:8080/ipintu/pintuapi', {
		'method' : 'accept',
		'userId' : 'a053beae20125b5b',
		'opt' : 'approve',
		'id' : id,
		'account' : account
	}, 
	//回调函数
	function (result) {
		$("#prompt").html(result);
	});
}

function refuseAction(id, account){
	$.get('http://192.168.1.110:8080/ipintu/pintuapi', {
		'method' : 'accept',
		'userId' : 'a053beae20125b5b',
		'opt' : 'refuse',
		'id' : id,
		'account' : account
	}, 
	//回调函数
	function (result) {
		$("#prompt").html(result);
	});
}

function loadApplicant(){
		$.get('http://192.168.1.110:8080/ipintu/pintuapi', {
			'method' : 'getApplicant',
			'userId' : 'a053beae20125b5b'
		}, 
		//回调函数
		function (result) {
			if(result.length == 0){
				$("#applicant").html("当前申请者的数目为0");
			}else{
				var strHtml = "<li class='li_h'>";
				strHtml +="<span class='email'>邮箱</span>";
				strHtml +="<span class='reason'>理由</span>";
				strHtml +="<span class='accept'>授理意见</span>";
				strHtml +="</li>";
				for( key in result ){
					var id = result[key].id;
					var account = result[key].account;
					var reason = result[key].applyReason;
					var approveStr = 'approveAction(\"'+id+'\", \"'+account+'\")';
					var refuseStr = 'refuseAction(\"'+id+'\", \"'+account+'\")';
					strHtml +="<li class='li_c'>";
					strHtml +="<span class='email'>"+account+"</span>";
					strHtml +="<span class='reason'>"+reason+"</span>";
					strHtml +="<span class='approve'><input type='button' value='同意' onclick='"+approveStr+"'/></span>";
					strHtml +="<span class='refuse'><input type='button' value='拒绝' onclick='"+refuseStr+"'/></span>";
					strHtml +="<span id='prompt'></span>";
					strHtml +="</li>";
				}
				$("#applicant").html(strHtml);
			}
		}, "json");
}
