/**
 * 
 */
package com.pintu.facade;

/**
 * 这里定义外部访问接口需要的常量字符串
 * @author lwz
 *
 */
public interface ExtVisitorInterface {

	public static final String UPLOADPICTURE = "upload";
	
	public static final String APPLYFORUSER = "applyForUser";
	
	public static final String GETGALLERYBYTIME = "getGalleryByTime";
	
	public static final String GETIMAGEFILE = "getImageFile";
	
	public static final String GETPICDETAIL = "getPicDetail";
	
	public static final String GETIMAGEBYPATH = "getImageByPath";
	
	public static final String ADDSTORY = "addStory";
	
	public static final String ADDCOMMENT ="addComment";
	
	public static final String GETSTORIESOFPIC ="getStoriesOfPic";
	
	public static final String GETCOMMENTSOFPIC ="getCommentsOfPic";
	
	public static final String  ADDVOTE= "addVote";
	
	public static final String GETUSERDETAIL = "getUserDetail";
	
	public static final String SENDMSG = "sendMsg";
	
	public static final String GETUSERMSG  = "getUserMsg";
	
	public static final String CHANGEMSGSTATE = "changeMsgState";
	
	public static final String GETHOTPICTURE = "getHotPicture";
	
	public static final String GETClASSICALPINTU= "getClassicalPintu";
	
	
	public static final String GETUSERESTATE = "getUserEstate";
	
	
	public static final String MARKTHEPIC = "markThePic";
	
	public static final String DELETEONEFAVOR = "deleteOneFavor";
	
	public static final String GETFAVORITEPICS = "getFavoriteTpics";
	
	public static final String GETTPICSBYUSER = "getTpicsByUser";
	
	public static final String GETSTORIESBYUSER  = "getStoriesByUser";

	public static final String GETGIFTS ="getExchangeableGifts";
	
	public static final String GETEVENTS = "getCommunityEvents";
	
	
	//后台管理员角色
	//发布可选礼物
	public static final String ADDGIFT = "publishGift";
	//发布社区事件
	public static final String ADDEVENT = "publishEvent";
	//查看最近一段时间的贴图
	public static final String GETLATESTPIC = "getLatestPic";
	
	//TODO, TO ADD MORE METHOD DEFINITION...

	
}
