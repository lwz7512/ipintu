package com.pintu.facade;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.pintu.beans.Comment;
import com.pintu.beans.Event;
import com.pintu.beans.GTStatics;
import com.pintu.beans.Gift;
import com.pintu.beans.Message;
import com.pintu.beans.News;
import com.pintu.beans.Note;
import com.pintu.beans.Story;
import com.pintu.beans.TPicDesc;
import com.pintu.beans.TPicDetails;
import com.pintu.beans.TastePic;
import com.pintu.beans.User;
import com.pintu.beans.Vote;
import com.pintu.beans.Wealth;

public interface PintuServiceInterface {
	
	//设置图片文件保存路径，由ApiAdaptor赋值
	public void saveImagePathToProcessor(String filePath);

	
	/**
	 * 该方法用于用户申请注册，系统生成邀请码发送到邮箱中完成注册；
	 * 看起来需要另外建立一个申请用户表t_applyuser
	 * 
	 * @param realname 真实姓名
	 * @param email 电邮
	 * @param intro 自我介绍加入理由
	 * @return
	 */
	public Boolean applyForUser(String realname,String email, String intro);
	
	/**
	 * 该方法在web注册页面中调用
	 * @param user 用户名，为邮箱地址
	 * @param pswd 密码
	 * @param inviteCode 邀请码
	 * @return
	 */
	//用户注册
	public Boolean registerUser(String user,String pswd,String inviteCode);
	
	//使用品图账号登录系统
	public Boolean loginSys(String user,String pswd);
	
	//使用微博账号登录系统
	public Boolean loginByWeibo(String user,String pswd);
	
	//发送一个贴图到系统中
	public Boolean createTastePic(TastePic pic,String user);
	
	//查看自己的贴图列表
	public List<TPicDesc> getTpicsByUser(String user,String pageNum);
	
	//查看自己的贝壳种类和数量
	public List<Wealth> getShellDetails(String user);
	
	//浏览社区画廊
	public List<TPicDesc> getCommunityTpics();
	

	/**
	 * 根据传入的开始时间和结束时间查询这一时间段内的缩略图返回，做为社区画廊数据源
	 * @param stratTime
	 * @param endTime
	 * @return
	 */
	public String getTpicsByTime(String startTime,String endTime);
	
	/**
	 * 根据图片id返回图片流到OutputStream中
	 * @param picId
	 * @param res
	 */
	public void getImageFile(String picId,HttpServletResponse res);

	//浏览一个品图详情
	public TPicDetails getTPicDetailsByID(String tpID);
	
	//查看一个品图的故事
	public List<Story> getStoriesOfPic(String tpID);
	
	//查看一个品图的评论
	public List<Comment> getCommentsOfPic(String tpID);
	
	//查看今日热图（点击量、故事数、评论数）
	//系统定时统计出来并保持，然后返回三类图排名10以内的30张图；
	public List<TPicDesc> getHotTpics();
	
	//查看今日填词邀请
	// 这个功能界面上没有设计，暂时先不实现；
	public List<TPicDesc> getInviteTpicsToday();
	
	//查看经典图文故事
	public List<TPicDesc> getClassicTpics();
	
	//查看用户基本信息（微博账号）
	public User getUsrBasInfo(String user);
	
	//查看用户等级和拥有贝壳数
	public Wealth getUsrEstate(String user);
	
	//展示今日的可兑换礼物信息
	public List<Gift> getGiftsToday();
	
	//对其他用户贴图进行评价
	public Boolean commentPintu(Comment cmt);
	
	//为社区中的品图论足邀请添加品评
	public Boolean addStoryToTpic(Story story);
	
	//为社区中的品评投票（包括经典标识）
	public Boolean addPollToTpic(Vote vote);
	
	//查看今日社区事件
	public List<Event> getCommunityEvents();
	
	//查看自己的收藏图片
	public List<TPicDesc> getFavoriteTpics(String user,String pageNum);
	
	//获取一个缩略图
	public Byte[] getTPicThumbnail(String thumbnailId);
	
	//获取原始大图，这个图用于web版浏览
	public Byte[] getTPicBig(String tpID);
	
	//获取一张小尺寸图，用于手机浏览
	public Byte[] getTPicMoile(String tpID);
	
	//发送一个消息
	public Boolean sendMessage(Message msg);
	
	//查看自己的消息
	public List<Message> getUserMessages(String user);
	
	
	
	//ANYMORE NECESSARY???
	
	
	//************************  以下为客户端2.0版本功能  *********************************
	
	//浏览社区达人(达人，Get Talent)
	public GTStatics getCommunityGTs();
	
	//在夜市兑换礼物
	public Boolean exchangeGifts(String user,String giftIds);
	
	//赠送邻居礼物
	public Boolean giveGifts(String user,String giftIds);
	
	//在夜市贴条子
	public Boolean pasteNote(String user,String content);
	
	//看夜市条子
	public List<Note> getMarketNotes();
	
	//按图片关键字搜索
	public List<TPicDesc> searchTpicByTags(String tags);
	
	//查看行业动态
	public List<News> getIndustryNews();
	

	//********************  后台管理用方法  **************************************
	
	//查看最近一段时间的贴图（自动刷新）
	public List<TPicDesc> getLatestTpics(String timeLength);
	
	//删除垃圾贴图
	public Boolean deleteTasetPic(String tpID);
	
	//删除灌水故事
	public Boolean deleteStoryOfPic(String storyId,String tpicID);
	
	//发布可选礼物信息
	public Boolean publishAvailableGift(Gift gift);
	
	
	//********************* 后台管理2.0功能 *************************************
	
	//发布社区事件
	public Boolean publishTpEvent(Event tpEvent);
	
	//发布行业动态
	public Boolean publishIndustryEvent(Event tpEvent);


	
	
	
	//TO BE CONTINUED...
	
}
