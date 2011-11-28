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
	
	public static final String LOGON ="logon";
	
	public static final String APPLY ="apply";
	
	public static final String REGISTER ="register";
	
	//验证账户
	public static final String VALIDATE = "validate";
	
	//检查昵称
	public static final String EXAMINE = "examine";
	
	//检查密码(当前输入的旧密码是否与库中一致)
	public static final String CONFIRM = "confirm";
	
	public static final String MODIFYPASSWORD = "modifyPassword";
	
//	public static final String UPLOADAVATAR= "uploadAvatar";
//	public static final String UPLOADPICTURE = "upload";
	
	public static final String GETGALLERYBYTIME = "getGalleryByTime";
	
	public static final String GETIMAGEFILE = "getImageFile";
	
	public static final String GETPICDETAIL = "getPicDetail";
	
	public static final String GETIMAGEBYPATH = "getImageByPath";
	
	public static final String ADDSTORY = "addStory";
	
//	public static final String ADDCOMMENT ="addComment";
	
	public static final String GETSTORIESOFPIC ="getStoriesOfPic";
	
//	public static final String GETCOMMENTSOFPIC ="getCommentsOfPic";
	
	public static final String GETUSERDETAIL = "getUserDetail";
	
	public static final String SENDMSG = "sendMsg";
	
	public static final String GETUSERMSG  = "getUserMsg";
	
	public static final String CHANGEMSGSTATE = "changeMsgState";
	
	public static final String GETHOTPICTURE = "getHotPicture";
	
	public static final String  ADDVOTE= "addVote";
	
	public static final String GETPICCOOLCOUNT= "getPicCoolCount";
	
	public static final String GETUSERESTATE = "getUserEstate";
	
	public static final String MARKTHEPIC = "markThePic";
	
	public static final String DELETEONEFAVOR = "deleteOneFavor";
	
	public static final String GETFAVORITEPICS = "getFavoriteTpics";
	
	public static final String GETTPICSBYUSER = "getTpicsByUser";
	
//	public static final String GETSTORIESBYUSER  = "getStoriesByUser";
	
	
	public static final String GETGIFTS ="getExchangeableGifts";
	
	public static final String GETEVENTS = "getCommunityEvents";
	
	//----------后台管理员角色
	//查看用户申请列表
	public static final String GETAPPLICANT = "getApplicant";
	//授理申请
	public static final String ACCEPT = "accept";
	
	//发布可选礼物
	public static final String PUBLISHGIFT = "publishGift";
	//发布社区事件
	public static final String PUBLISHEVENT = "publishEvent";
	//查看最近一段时间的贴图
	public static final String GETLATESTPIC = "getLatestPic";
	
	//TODO, TO ADD MORE METHOD DEFINITION...
	
	//统计最近被收藏的图片top12
	public static final String COLLECTSTATISTICS = "collectStatistics";

	//按浏览数查询经典top12
	public static final String CLASSICALSTATISTICS = "classicalStatistics";

	//按被收藏的多少查询（这个相当于先用and后用or）
	public static final String SEARCHBYTAG="searchByTag";
	
	//查询手机图作为web版画廊，pageSize待定
    public static final String GETGALLERYFORWEB ="getGalleryForWeb";
    
    //最热标签
    public static final String GETHOTTAGS = "getHotTags";
    
    //列出所有系统标签
    public static final String GETSYSTEMTAGS = "getSystemTags";
    
    //删除评论
    public static final String DELETEONECMT = "deleteOneCmt";
    
    //删除发过的图
    public static final String DELETEONEPIC ="deleteOnePic";
	
    //查看某tag的所有图片
    public static final String GETTHUMBNAILSBYTAG = "getThumbnailsByTag";
    
    //贴图达人
    public static final String PICDARENSTATISTICS = "pictureDarenStatistics";
    
    //评论达人
    public static final String CMTDARENSTATISTICS = "commentDarenStatistics";
    
    //随机获取系统32个图~
    public static final String GETRANDGALLERY = "getRandGallery";
    
}
