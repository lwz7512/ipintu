package com.pintu.dao.impl;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.pintu.beans.Comment;
import com.pintu.beans.Story;
import com.pintu.beans.TPicDesc;
import com.pintu.beans.TPicItem;
import com.pintu.beans.User;
import com.pintu.beans.Vote;
import com.pintu.cache.PintuCache;
import com.pintu.dao.CacheAccessInterface;

public class CacheAccessImplement implements CacheAccessInterface {

	// Inject by Spring
	private PintuCache pintuCache;

	// Inject by Spring
	public CacheAccessImplement() {
		// 初始化要缓存对象ID的类型及容器，单个缓存，批量入库，批量删除
		toSavedCacheIds.put(PICTURE_TYPE, new LinkedList<String>());
		toSavedCacheIds.put(STORY_TYPE, new LinkedList<String>());
		toSavedCacheIds.put(COMMENT_TYPE, new LinkedList<String>());
		toSavedCacheIds.put(VOTE_TYPE, new LinkedList<String>());
	}

	public void setPintuCache(PintuCache pintuCache) {
		this.pintuCache = pintuCache;
	}

	@Override
	public void clearHotPicCacheIds() {
		CacheAccessInterface.hotPicCacheIds.clear();
	}

	@Override
	public void cacheLoggedInUser() {
		// TODO Auto-generated method stub
	}
	
	@Override
	public List<User> getActiveUser() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void cachePicture(TPicItem pic) {
		System.out.println("4 图片信息放缓存");
		// 1. 把对象放到pintuCache中
		// 2. 把ID放到toSavedCacheIds中的LinkedList中
		pintuCache.cachePicture(pic.getId(), pic);
		//注意：要把新建立的对象ID存起来，以便入库线程处理
		toSavedCacheIds.get(PICTURE_TYPE).add(pic.getId());
	}
	
	
	
	@Override
	public void cacheComment(Comment comment) {
			pintuCache.cacheComment(comment.getId(), comment);
			toSavedCacheIds.get(COMMENT_TYPE).add(comment.getId());
	}

	@Override
	public void cacheStory(Story story) {
			pintuCache.cacheStory(story.getId(), story);
			toSavedCacheIds.get(STORY_TYPE).add(story.getId());
	}


	@Override
	public void cacheVote(Vote vote) {
			pintuCache.cacheVote(vote.getId(), vote);
			toSavedCacheIds.get(VOTE_TYPE).add(vote.getId());
	}

	// 在ImageFileCreationTask中生成缩略图后，调用此方法
	@Override
	public void cacheThumbnail(TPicDesc tpicDesc) {
		// 缓存缩略图对象
		pintuCache.cacheThumbnail(tpicDesc);

	}
	

	@Override
	public List<TPicDesc> getCachedThumbnail(String createTime) {
		return pintuCache.getCachedThumbnail(createTime);
	}

	@Override
	public Comment getSpecificComment(String cid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TPicItem getSpecificPic(String pid) {
		TPicItem tpicItem = new TPicItem();
		List<String> list = new ArrayList<String>();
		list.add(pid);
		List<Object> resList = pintuCache.getCachedPicture(list);
		if(resList != null && resList.size() > 0){
			tpicItem = (TPicItem) resList.get(0);
		}
		return tpicItem;
	}

	@Override
	public Story getSpecificStory(String sid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public User getSpecificUser(String userAccount) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Vote getSpecificVote(String vid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean deleteSavedObjIDs(String type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Object> getUnSavedObj(String type) {
		List<Object> list = new ArrayList<Object>();
		List<String> ids = new ArrayList<String>();

		if (type.equals(PICTURE_TYPE)) {
			ids = toSavedCacheIds.get(PICTURE_TYPE);
			list = pintuCache.getCachedPicture(ids);
		} else if (type.equals(COMMENT_TYPE)) {
			ids = toSavedCacheIds.get(COMMENT_TYPE);
			list = pintuCache.getCachedComment(ids);
		} else if (type.equals(STORY_TYPE)) {
			ids = toSavedCacheIds.get(STORY_TYPE);
			list = pintuCache.getCachedStory(ids);
		} else if (type.equals(VOTE_TYPE)) {
			ids = toSavedCacheIds.get(VOTE_TYPE);
			list = pintuCache.getCachedVote(ids);
		}

		return list;
	}


	@Override
	public void traceCache() {
		pintuCache.traceAll();		
	}

	@Override
	public void syncDBPictureToCache(TPicItem tpicItem) {
		System.out.println("将数据库图片同步到缓存"+tpicItem.getId());
		pintuCache.cachePicture(tpicItem.getId(), tpicItem);
	}

	@Override
	public void syncDBCommnetToCache(Comment comment) {
		pintuCache.cacheComment(comment.getId(), comment);
	}

	@Override
	public void syncDBStoryToCache(Story story) {
		pintuCache.cacheStory(story.getId(), story);
	}

	@Override
	public void syncDBVoteToCache(Vote vote) {
		pintuCache.cacheVote(vote.getId(), vote);
	}

	@Override
	public void cacheUser(User user) {
		pintuCache.cacheUser(user);
	}

	@Override
	public void updateCachedUser(String userId, String updateTime) {
		pintuCache.updateCachedUser(userId, updateTime);
	}

	@Override
	public List<User> getLiveUser(String updateTime) {
		return pintuCache.getLiveUser(updateTime);
	}

	


} // end of class
