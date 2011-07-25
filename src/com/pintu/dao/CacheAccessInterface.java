package com.pintu.dao;

import java.util.LinkedList;
import java.util.List;

import com.pintu.beans.Comment;
import com.pintu.beans.Story;
import com.pintu.beans.TPicItem;
import com.pintu.beans.User;

public interface CacheAccessInterface {

	//存放要同步入库的对象ID，该对象在缓冲中以存放；
	//同步入库后要从该列表中清除；
	public static LinkedList<String> toSavedCacheIds = new LinkedList<String>();
	
	//缓存登录用户	
	public void cacheLoggedInUser();
	
	//读取活动用户
	public List<User> getActiveUser();
	
	//获取特定用户
	public User getSpecificUser(String userAccount);
	
	//缓存图片信息
	public void cachePicture(TPicItem pic);
	
	//读取图片信息	
	public TPicItem getSpecificPic(String pid);
	
	//缓存故事
	public void cacheStory(Story story);

	//读取故事
	public Story getSpecificStory(String sid);	
	
	//缓存评论
	public void cacheComment(Comment comment);

	//读取评论
	public Comment getSpecificComment(String cid);
	
	
	
}
