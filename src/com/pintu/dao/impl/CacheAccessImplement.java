package com.pintu.dao.impl;

import java.awt.Image;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

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
	

	private Logger log = Logger.getLogger(CacheAccessImplement.class);

	public CacheAccessImplement() {
		// 初始化要缓存对象ID的类型及容器，单个缓存，批量入库，批量删除
		toSavedUserPicIds.put(PICTURE_TYPE, new LinkedList<String>());
		
		toSavedCacheIds.put(STORY_TYPE, new HashMap<String,LinkedList<String>>());
		toSavedCacheIds.put(VOTE_TYPE, new HashMap<String,LinkedList<String>>());
		
	}

	public void setPintuCache(PintuCache pintuCache) {
		this.pintuCache = pintuCache;
	}

	@Override
	public void clearHotPicCacheIds() {
		CacheAccessInterface.hotPicCacheIds.clear();
	}
	
	@Override
	public void cacheImage(String picId, Image img) {
		pintuCache.cacheImage(picId, img);
	}

	@Override
	public void cachePicture(TPicItem pic) {
		log.debug("4 Add TPicItem info to cache");
		// 1. 把对象放到pintuCache中
		// 2. 把ID放到toSavedCacheIds中的LinkedList中
		pintuCache.cachePicture( pic.getId(), pic);
		
		//注意：要把新建立的对象ID存起来，以便入库线程处理
		toSavedUserPicIds.get(PICTURE_TYPE).add(pic.getId());
	}
	

	@Override
	public void cacheStory(Story story) {
			String picId = story.getFollow();
			String storyId = story.getId();
			pintuCache.cacheStory(story.getFollow(),story.getId(), story);
			
			LinkedList<String> storyIdList = toSavedCacheIds.get(STORY_TYPE).get(picId);
			if(storyIdList == null){
				LinkedList<String> idList = new LinkedList<String>();
				idList.add(storyId);
				toSavedCacheIds.get(STORY_TYPE).put(picId, idList);
			}else{
				toSavedCacheIds.get(STORY_TYPE).get(picId).add(storyId);
			}
	}


	@Override
	public void cacheVote(Vote vote) {
			String picId = vote.getFollow();
			String voteId = vote.getId();
			pintuCache.cacheVote(vote.getFollow(),vote.getId(), vote);
			LinkedList<String> voteIdList = toSavedCacheIds.get(VOTE_TYPE).get(picId);
			if(voteIdList == null){
				LinkedList<String> idList = new LinkedList<String>();
				idList.add(voteId);
				toSavedCacheIds.get(VOTE_TYPE).put(picId, idList);
			}else{
				toSavedCacheIds.get(VOTE_TYPE).get(picId).add(voteId);
			}
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
	public TPicItem getSpecificPic(String pid) {
		TPicItem tpicItem = new TPicItem();
		List<String> list = new ArrayList<String>();
		list.add(pid);
		List<Object> resList = pintuCache.getToSavedCachedPicture(list);
		if(resList != null && resList.size() > 0){
			tpicItem = (TPicItem) resList.get(0);
		}
		return tpicItem;
	}

	@Override
	public User getSpecificUser(String userId) {
		User user = new User();
		List<String> list = new ArrayList<String>();
		list.add(userId);
		List<Object> resList = pintuCache.getToSavedCachedPicture(list);
		if(resList != null && resList.size() > 0){
			user = (User) resList.get(0);
		}
		return user;
	}
	
//	@Override
//	public Story getSpecificStory(String sid) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//	@Override
//	public Vote getSpecificVote(String vid) {
//		// TODO Auto-generated method stub
//		return null;
//	}

	@Override
	public List<Object> getUnSavedObj(String type) {
		List<Object> list = new ArrayList<Object>();
		List<String> ids = new ArrayList<String>();
		Map<String,LinkedList<String>> map = new HashMap<String,LinkedList<String>>();
		
		if (type.equals(PICTURE_TYPE)) {
			ids = toSavedUserPicIds.get(PICTURE_TYPE);
			list = pintuCache.getToSavedCachedPicture(ids);
		} else if (type.equals(STORY_TYPE)) {
			map = toSavedCacheIds.get(STORY_TYPE);
			list = pintuCache.getToSavedCachedStory(map);
		} else if (type.equals(VOTE_TYPE)) {
			map = toSavedCacheIds.get(VOTE_TYPE);
			list = pintuCache.getToSavedCachedVote(map);
		}

		return list;
	}

	@Override
	public void traceCache() {
		pintuCache.traceAll();		
	}

	@Override
	public void syncDBPictureToCache(TPicItem tpicItem) {
		pintuCache.cachePicture(tpicItem.getId(), tpicItem);
	}


	@Override
	public void syncDBStoryToCache(Story story) {
		pintuCache.cacheStory(story.getFollow(),story.getId(), story);
	}

	@Override
	public void syncDBVoteToCache(Vote vote) {
		pintuCache.cacheVote(vote.getFollow(),vote.getId(), vote);
	}

	
	@Override
	public void cacheUser(User user) {
		pintuCache.cacheUser(user.getId(), user);
	}

	@Override
	public List<User> getActiveUser(Long startTime,Long endTime) {
		return pintuCache.getActiveUser(startTime, endTime);
	}

	@Override
	public void updateCachedUser(String userId, Long updateTime) {
		pintuCache.updateCachedUser(userId, updateTime);
	}
	
	@Override
	public void updateUserInfo(String userId, String avatarPath, String nickName) {
		pintuCache.updateUserInfo(userId,avatarPath,nickName);
	}
	
	@Override
	public List<Story> getStoriesOfPic(String tpId) {
		return pintuCache.getCachedStoryByPid(tpId);
	}

	@Override
	public User getUserById(String userId) {
		return pintuCache.getCachedUser(userId);
	}

	@Override
	public boolean removeTPic(String id) {
		return pintuCache.removeTPicById(id);
	}

	@Override
	public boolean removeThumbnail(long longTime,String thumbnailId) {
		return pintuCache.removeThumbnailById(longTime,thumbnailId);
	}

	@Override
	public Image getCachedImage(String id) {
		return pintuCache.getCacheImageById(id);
	}


} // end of class
