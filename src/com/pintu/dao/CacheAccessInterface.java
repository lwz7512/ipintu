package com.pintu.dao;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.pintu.beans.Comment;
import com.pintu.beans.Story;
import com.pintu.beans.TPicDesc;
import com.pintu.beans.TPicItem;
import com.pintu.beans.User;
import com.pintu.beans.Vote;

public interface CacheAccessInterface {

	// 存放要同步入库的对象ID，该对象在缓冲中已存放；
	// 同步入库后要从该列表中清除；
	// type,(id,id...);
	
	//FIXME TODO 要改这个toSavedCacheIds
	public static Map<String, LinkedList<String>> toSavedUserPicIds = new HashMap<String, LinkedList<String>>();
	public static Map<String,Map<String,LinkedList<String>>>  toSavedCacheIds = new HashMap<String,Map<String,LinkedList<String>>>();
	
	public static String USER_TYPE = "user";
	
	public static String PICTURE_TYPE = "picture";

	public static String STORY_TYPE = "story";

	public static String COMMENT_TYPE = "comment";

	public static String VOTE_TYPE = "vote";

	
	//用于存放热图的id和点击量的对应关系（每当查看详情后将被查看的图片id放到这里）
	//在每天零点计算积分等级等时，顺便将这个清空
	public static Map<String, Integer> hotPicCacheIds = new HashMap<String,Integer>();
	
	//清空热图MAP
	public void clearHotPicCacheIds();
	
	// 查看缓存中的对象数目
	public void traceCache();

	// 缓存图片信息
	//顺便把id存到toSavedUserPicIds,picture中
	public void cachePicture(TPicItem pic);

	// 缓存故事
	//顺便把id存到toSavedCacheIds
	public void cacheStory(Story story);
	
	// 缓存评论
	//顺便把id存到toSavedCacheIds
	public void cacheComment(Comment comment);
	
	// 缓存投票
	// 顺便把id存到toSavedCacheIds
	public void cacheVote(Vote vote);
	
	// 获取特定用户
	public User getSpecificUser(String userId);

	// 读取图片信息
	public TPicItem getSpecificPic(String pid);

	// 读取故事
//	public Story getSpecificStory(String sid);

	// 读取评论
//	public Comment getSpecificComment(String cid);

	// 读取投票
//	public Vote getSpecificVote(String vid);

	// 读取未入库的所有某type的对象
	public List<Object> getUnSavedObj(String type);


	// 缓存缩略图
	public void cacheThumbnail(TPicDesc tpicDesc);

	// 读取缓存中的缩略图
	public List<TPicDesc> getCachedThumbnail(String createTime);
	
	public void syncDBPictureToCache(TPicItem tpicItem);
	
	public void syncDBCommnetToCache(Comment comment);
	
	public void syncDBStoryToCache(Story story);
	
	public void syncDBVoteToCache(Vote vote);
	
	//登录，注册的用户都缓存~若有动作，但缓存没有，也要从库里查出放入缓存里
	public void cacheUser(User user);
	
	//更新用户最后操作时间
	public void updateCachedUser(String userId, Long updateTime);
	
	//更新用户
	public void updateCachedUser(User user);
	
	// 读取活动用户
	public List<User> getActiveUser(Long startTime,Long endTime);

	public List<Comment> getCommentsOfPic(String tpId);

	public List<Story> getStoriesOfPic(String tpId);

	public User getUserById(String userId);
	
}