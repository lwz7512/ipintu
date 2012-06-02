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
	
	public static final String RETRIEVE = "retrieve";
	
	public static final String APPLY ="apply";
	
	public static final String REGISTER ="register";
	
	//验证账户
	public static final String VALIDATE = "validate";
	
	//验证账户(申请时用)
	public static final String CHECKOUT= "checkout";
	
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
	
	//----------后台管理员角色
	//发布可选礼物
	public static final String PUBLISHGIFT = "publishGift";
	//发布社区事件
	public static final String PUBLISHEVENT = "publishEvent";
	
	public static final String GETGIFTS ="getExchangeableGifts";
	
	public static final String GETEVENTS = "getCommunityEvents";

	//查看用户申请列表
	public static final String GETAPPLICANT = "getApplicant";
	//授理申请
	public static final String ACCEPT = "accept";
	
	//查看最近小时内的贴图
	public static final String GETLATESTPIC = "getLatestPic";
	//审核发图
	public static final String REVIEWPICTURE = "reviewPicture";
	
	public static final String CREATEINVITECODE="createInviteCode";
	
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
    
    //活跃用户排行榜(根据积分排列)
    public static final String ACTIVEUSERRANKING = "getActiveUserRanking";
    
    
    //-----------------微广告系统接口
    
    //获取当天有效的所有广告
    public static final String GETTODAYADS = "getTodayAds";
    
    //按关键字（包括广告商或者广告语）、时间查询
    public static final String SEARCHADS = "searchAds";
    
    //删除某条广告
    public static final String DELETEADSBYID = "deleteAdsById";
    
    //创建新广告
    public static final String CREATEADS = "createAds";
    
    //编辑广告
    //先根据id获取一个广告内容并填充
    public static final String GETADSBYID = "getAdsById";
    //再更新广告
    public static final String UPDATEADSBYID = "updateAdsById";
    
    //根据相对路径取图片
    public static final String GETIMGBYRELATIVEPATH = "getImgByRelativePath";
    
    //验证厂商
    public static final String LOGINAD = "loginAd";
    //厂商修改密码
    public static final String CHANGEPWD = "changePwd";
    //按条件查询厂商
    public static final String SEARCHVENDERS = "searchVenders";
    //查询某一厂商
    public static final String GETVENDERSBYID = "getVendersById";
    
    //创建新厂商
    public static final String CREATEVENDERS = "createVenders";
    //再更新厂商
    public static final String UPDATEVENDERSBYID = "updateVendersById";
    
    //提供一个用来验证用户是否到期的接口
    public static final String VALIDATEVENDER = "validateVender";
    
    //注册新厂商
    public static final String REGISTVENDERS = "registVenders";
    
    //验证厂商是否已被注册
    public static final String CHECKOUTREGISTER = "checkoutRegister";
    
    
    //--------接微博相关方法
    
    //根据code获取access token
    public static final String GETACCESSTOKENBYCODE = "getAccessTokenByCode";
    
    //转发到微博
    public static final String FORWARDTOWEIBO = "forwardToWeibo";
    
    //完善资料(使用weibo账户登录，完善账号密码)
    public static final String IMPROVEWEIBOUSER = "improveWeiboUser";
    
    
   //---贴条子相关方法
    
    //条子列表
    public static final String GETCOMMUNITYNOTES = "getCommunityNotes";
    
    //添加新条子
    public static final String ADDNOTE = "addNote";
    
    //删除指定条子
    public static final String DELETENOTEBYID = "deleteNoteById";
    
    //修改条子
    public static final String UPDATENOTEBYID = "updateNoteById";
    
    //获取指定条子
    public static final String GETNOTEBYID = "getNoteById";
    
    //增加关注数
    public static final String ADDATTENTIONBYID = "addAttentionById";
    
    //增加感兴趣数
    public static final String ADDINTERESTBYID = "addInterestById";
    
    //显示自己的条子列表
    public static final String GETUSERNOTES = "getUserNotes";
}
