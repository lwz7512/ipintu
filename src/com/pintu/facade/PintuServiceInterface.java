package com.pintu.facade;

import java.io.File;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.pintu.beans.Comment;
import com.pintu.beans.Event;
import com.pintu.beans.Favorite;
import com.pintu.beans.Gift;
import com.pintu.beans.Message;
import com.pintu.beans.Story;
import com.pintu.beans.StoryDetails;
import com.pintu.beans.TPicDesc;
import com.pintu.beans.TPicDetails;
import com.pintu.beans.TPicItem;
import com.pintu.beans.TastePic;
import com.pintu.beans.User;
import com.pintu.beans.UserDetail;
import com.pintu.beans.Vote;
import com.pintu.beans.Wealth;

public interface PintuServiceInterface {

	// 设置图片文件保存路径，由ApiAdaptor赋值
	public void saveImagePathToProcessor(String filePath);

	/**
	 * 该方法用于用户申请注册，系统生成邀请码发送到邮箱中完成注册； 看起来需要另外建立一个申请用户表t_applyuser
	 * 
	 * @param realname
	 *            真实姓名
	 * @param email
	 *            电邮
	 * @param intro
	 *            自我介绍加入理由
	 * @return
	 */
//	public Boolean applyForUser(String realname, String email, String intro);

	/**
	 * 该方法在web注册页面中调用
	 * 
	 * @param user
	 *            用户名，为邮箱地址
	 * @param pswd
	 *            密码
	 * @param inviteCode
	 *            邀请码
	 * @return
	 */
//	// 用户注册
//	public Boolean registerUser(String user, String pswd, String inviteCode);
//
//	// 使用品图账号登录系统
//	public Boolean loginSys(String user, String pswd);
//
//	// 使用微博账号登录系统
//	public Boolean loginByWeibo(String user, String pswd);
//
//	// 查看用户基本信息（微博账号）
//	public User getUsrBasInfo(String user);

	// -------------------------------下面是已做的
	// 查看用户息
	public User getUserInfo(String userId);

	// 发送一个贴图到系统中
	public void createTastePic(TastePic pic, String user);

	// 浏览社区画廊（预留）
	public List<TPicDesc> getCommunityTpics();

	/**
	 * 根据传入的开始时间和结束时间查询这一时间段内的缩略图返回，做为社区画廊数据源
	 * 
	 * @param stratTime
	 * @param endTime
	 * @return
	 */
	public List<TPicDesc> getTpicsByTime(String startTime, String endTime);

	/**
	 * 根据图片id返回图片流到OutputStream中
	 * 
	 * @param picId
	 * @param res
	 */
	public void getImageFile(String picId, HttpServletResponse res);

	public void getImageByPath(String path, HttpServletResponse res);

	/**
	 * 根据缩略图的名字找到相应的缩略图文件
	 * 
	 * @param thumbnailName
	 * @return
	 */
	public File getThumbnail(String thumbnailName);

	// 浏览一个品图详情
	public TPicDetails getTPicDetailsById(String tpId);

	// 为社区中的品图论足邀请添加品评
	public void addStoryToPintu(Story story);

	// 对其他用户贴图进行评价
	public void addCommentToPintu(Comment cmt);

	// 查看一个品图的故事
	public List<StoryDetails> getStroyDetailsOfPic(String tpId);

	// 查看一个品图的评论
	public List<Comment> getCommentsOfPic(String tpId);

	// 为故事投票
	public void addVoteToStory(Vote vote);

	// 返回一个故事的所有各类的投票
	public List<Vote> getVotesOfStory(String storyId);

	// 查看今日热图（点击量、故事数、评论数）
	// 系统定时统计出来并保持，然后返回三类图排名10以内的30张图；
	// public List<TPicDesc> getHotTpics();

	// 取到今日热图
	public List<TPicDetails> getHotPicture();

	// 查看经典品图（包括经典故事及其所属的图片）
	public List<StoryDetails> getClassicalPintu();

	// 查看今日填词邀请
	// 这个功能界面上没有设计，暂时先不实现；
	public List<TPicDesc> getInviteTpicsToday();

	// 查看用户等级和拥有贝壳数
	public UserDetail getUserEstate(String userId);

	// 查看自己的贝壳种类和数量
	public List<Wealth> getWealthDetails(String userId);

	// 发送一个消息
	public Boolean sendMessage(Message msg);

	// 查看自己的消息
	public List<Message> getUserMessages(String userId);

	// 改变消息的状态
	public boolean changeMsgState(List<String> msgIdList);

	// 在标记为收藏时来检查是否已收藏
	public boolean checkExistFavorite(String userId, String picId);

	// 标记收藏
	public boolean markFavoritePic(Favorite fav);

	// 删除收藏的某一图片
	public boolean deleteOnesFavorite(String picId);

	// 查看自己的收藏图片(要用到分页)
	public List<TPicItem> getFavoriteTpics(String userId, int pageNum);

	// 查看自己的故事列表
	public List<StoryDetails> getStroiesByUser(String userId, int pageNum);

	// 查看自己的贴图列表
	public List<TPicItem> getTpicsByUser(String userId, int pageNum);
	
	// 展示今日的可兑换礼物信息
	public List<Gift> getExchangeableGifts();

	// 查看今日社区事件
	public List<Event> getCommunityEvents();
	
	// 发布可选礼物信息
	public Boolean publishExchangeableGift(Gift gift);
	
	// 发布社区事件
	public Boolean publishCommunityEvent(Event event);

	// 获取最近一段时间的贴图
	public List<TPicDesc> getLatestPic();

	//验证登录的用户是否存在
	public String getExistUser(String account, String pwd);

	//验证账户名称是否已被占用
	public String validateAccount(String account);

	//比较邀请码是否正确，正确即注册，否则返回错误信息
	public String registerUser(String account, String pwd, String code);

	//这里由管理员授理请求，并发带注册码的链接邮件给申请者邮箱
	public String sendApply(String account, String reason);

	public String  acceptApply(String id, String account,String url);

	public List<User> getApplicant();

	// ANYMORE NECESSARY???

	// ************************ 以下为客户端2.0版本功能 *********************************

	// 浏览社区达人(达人，Get Talent)
//	public GTStatics getCommunityGTs();
//
//	// 在夜市兑换礼物
//	public Boolean exchangeGifts(String user, String giftIds);
//
//	// 赠送邻居礼物
//	public Boolean giveGifts(String user, String giftIds);
//
//	// 在夜市贴条子
//	public Boolean pasteNote(String user, String content);
//
//	// 看夜市条子
//	public List<Note> getMarketNotes();
//
//	// 按图片关键字搜索
//	public List<TPicDesc> searchTpicByTags(String tags);
//	
//	// 发布行业动态
//	public Boolean publishIndustryEvent(Event tpEvent);
//
//	// 查看行业动态
//	public List<News> getIndustryNews();
//
//	// ******************** 后台管理用方法 **************************************
//
//	// 查看最近一段时间的贴图（自动刷新）
//	public List<TPicDesc> getLatestTpics(String timeLength);
//
//	// 删除垃圾贴图
//	public Boolean deleteTasetPic(String tpID);
//
//	// 删除灌水故事
//	public Boolean deleteStoryOfPic(String storyId, String tpicID);


	// TO BE CONTINUED...


}
