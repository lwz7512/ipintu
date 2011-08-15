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
	public static Map<String, LinkedList<String>> toSavedCacheIds = new HashMap<String, LinkedList<String>>();

	public static String PICTURE_TYPE = "picture";

	public static String STORY_TYPE = "story";

	public static String COMMENT_TYPE = "comment";

	public static String VOTE_TYPE = "vote";

	public static String THUMBNAIL_TYPE = "thumbnail";

	// 缓存登录用户
	public void cacheLoggedInUser();

	// 读取活动用户
	public List<User> getActiveUser();

	// 获取特定用户
	public User getSpecificUser(String userAccount);

	// 缓存图片信息
	//顺便把id存到toSavedCacheIds,picture中
	public void cachePicture(TPicItem pic);

	// 读取图片信息
	public TPicItem getSpecificPic(String pid);

	// 缓存故事
	// 存ID到缓存方便同步
	public void cacheStory(Story story);

	// 读取故事
	public Story getSpecificStory(String sid);

	// 缓存评论
	//存ID到缓存方便同步
	public void cacheComment(Comment comment);

	// 读取评论
	public Comment getSpecificComment(String cid);

	// 缓存投票
	//  存ID到缓存方便同步
	public void cacheVote(Vote vote);

	// 读取投票
	public Vote getSpecificVote(String vid);

	// 读取未入库的所有某type的对象
	public List<Object> getUnSavedObj(String type);

	// FIXME (这个好像没用了) 删掉已入库的所有对象ID: LinkedList中的所有ID
	public Boolean deleteSavedObjIDs(String type);
	

	// 查看缓存中的对象数目
	public void traceCache();

	// 缓存缩略图
	public void cacheThumbnail(TPicDesc tpicDesc);

	// 读取缓存中的缩略图
	public List<TPicDesc> getCachedThumbnail(String createTime);
	
	public void syncDBPictureToCache(TPicItem tpicItem);
	
	public void syncDBCommnetToCache(Comment comment);
	
	public void syncDBStoryToCache(Story story);
	
	public void syncDBVoteToCache(Vote vote);

}
