var msgBoxCurDiv = null;
var msgBoxDiv = null;
var msgBoxBackDiv = null;
var msgBoxParentDiv = null;
var msgBoxBackFunction = null;
var msgBoxSelectList = null;
var isIE = !+"\v1";
//主函数 id:内容,strTitle:标题,fn_msgBoxBack:关闭时回调函数,notShowClose:是否隐藏关闭按钮
function msgBox(id, strTitle, fn_msgBoxBack, hideClose)
{
	if(msgBoxDiv && msgBoxDiv.style.display == 'block')
	{
		msgBox_close();
	}
	var div = document.getElementById(id);
	if(!div)
	{
		alert('内容不存在！');
	}
	msgBoxCurDiv = div;
	msgBox_getMsgDiv();
	msgBox_innerHTML(document.getElementById('msgBoxTitle'), strTitle || '无标题');
	document.getElementById('msgBoxBtnClose').style.display = hideClose ? 'none':'inline';
	msgBoxBackFunction = fn_msgBoxBack;
	msgBoxParentDiv = msgBoxCurDiv.parentNode;
	msgBoxParentDiv.removeChild(msgBoxCurDiv);
	msgBoxCurDiv.style.display = 'block';
	msgBoxDiv.appendChild(msgBoxCurDiv);
	msgBoxDiv.onkeyup = function(evt)
		{
			evt = evt ? evt : (window.event ? window.event : null);
			if(evt.keyCode==27 && document.getElementById('msgBoxBtnClose').style.display != 'none')
			msgBox_close();
		}
	
	//IE6将SELECT隐藏
	msgBoxSelectList = new Array();
	if(navigator.appVersion.indexOf('MSIE 6.0') != -1){
		var selectList = document.getElementsByTagName('select');
		var mySelectList = msgBoxCurDiv.getElementsByTagName('select');
		for(var i=0; i<selectList.length; i++){
			var isMy = true;
			for(var j=0; j<mySelectList.length; j++){
				if(selectList[i].uniqueID == mySelectList[j].uniqueID){
					isMy=false;
					break;
				}
			}
			if(isMy && selectList[i].style.visibility != 'hidden')
			{
				msgBoxSelectList.push(selectList[i]);
				selectList[i].style.visibility = 'hidden';
			}
		}
	}
	
	//window.setTimeout(msgBox_showMsg, 100);
	msgBox_showMsg();
}

function msgBox_innerHTML(obj, strHTML)
{
	obj.innerHTML = strHTML;
}

var msgBox_inputFocus = null;
function msgBox_showMsg() {
	var pageSizeObj = getPageSize();
	var top = (pageSizeObj.height - msgBoxDiv.offsetHeight) / 2;
	var left = (pageSizeObj.width - msgBoxDiv.offsetWidth) / 2;
	msgBoxDiv.style.top = top < 0 ? pageSizeObj.top : pageSizeObj.top + top + 'px';
	msgBoxDiv.style.left = left < 0 ? pageSizeObj.left : pageSizeObj.left + left + 'px';
	msgBoxDiv.style.width = msgBoxDiv.clientWidth - parseInt(msgBoxDiv.style.paddingLeft) - parseInt(msgBoxDiv.style.paddingRight) + 'px';
	//让第一个可用的Input获得焦点
	var allElement = msgBoxCurDiv.getElementsByTagName('*');
	for(var i=0; i<allElement.length; i++)
	{
		var element = allElement[i];
		if((element.tagName == 'INPUT' || element.tagName == 'SELECT' || element.tagName == 'TEXTAREA') && !element.disabled)
		{
			element.focus();
			break;
		} 
	}
}

//关闭对话框
function msgBox_close(srcElement)
{
	if(msgBoxBackFunction)
	{
		if(srcElement == document.getElementById('msgBoxBtnClose'))
		{
			msgBoxBackFunction(true);
		}
		else
		{
			msgBoxBackFunction();
		}
	}
	msgBoxDiv.style.width = 'auto';
	msgBoxDiv.style.height = 'auto';
	msgBoxDiv.style.display = 'none';
	msgBoxBackDiv.style.display = 'none';
	msgBoxDiv.removeChild(msgBoxCurDiv);
	msgBoxParentDiv.appendChild(msgBoxCurDiv);
	msgBoxCurDiv.style.display = 'none';
	for(var i=0; i<msgBoxSelectList.length; i++)
	{
		msgBoxSelectList[i].style.visibility = 'inherit';
	}
	msgBox_moveFinish();
}

//获取弹出层
function msgBox_getMsgDiv()
{
	if(msgBoxDiv)
	{
		msgBoxDiv.style.display = 'block';
		return;
	}
	var div = document.createElement('div');
	div.style.zIndex = 101;
	div.style.position = 'absolute';
	div.style.top = '-10000px';
	div.style.left = '0px';
	div.style.backgroundColor = 'white';
	div.style.border = 'solid 5px blue';
	div.style.padding = '5px';
	var strHTML = '<div style="position:relative; cursor:default; font-size:14px;" onmousedown="msgBox_moveStart(event);">';
	strHTML += '<b id="msgBoxTitle"></b>';
	strHTML += '<a id="msgBoxBtnClose" title="关闭" style="font-size:12px; position:absolute; right:5px; top:1px; font-family:Tahoma; text-decoration:none; color:blue;" href="javascript:msgBox_close(this);">&nbsp;X&nbsp;</a></div>';
	strHTML += '<div style="border-top:solid 1px gray; margin:5px auto;"></div>';
	msgBox_innerHTML(div, strHTML);
	if(document.forms.length == 0)
	{
		msgBoxDiv = document.body.appendChild(div);
	}
	else
	{
		msgBoxDiv = document.forms[0].appendChild(div);
	}
}

function getPageSize() {
	var obj = new Object();
	obj.allWidth = document.body.scrollWidth;
	obj.allHeight = document.body.scrollHeight;
	if (-[1, ]) {	//非IE
		obj.top = document.body.scrollTop;
		obj.left = document.body.scrollLeft;
		if (document.compatMode === 'CSS1Compat') {
			obj.width = document.documentElement.clientWidth;
			obj.height = document.documentElement.clientHeight;
		}
		else {
			obj.width = document.body.clientWidth;
			obj.height = document.body.clientHeight;
		}
	} else {
		if (document.compatMode === 'CSS1Compat') {
			obj.width = document.documentElement.clientWidth;
			obj.height = document.documentElement.clientHeight;
			obj.top = document.documentElement.scrollTop;
			obj.left = document.documentElement.scrollLeft;
		}
		else {
			obj.width = document.body.clientWidth;
			obj.height = document.body.clientHeight;
			obj.top = document.body.scrollTop;
			obj.left = document.body.scrollLeft;
		}
	}
	return obj;
}