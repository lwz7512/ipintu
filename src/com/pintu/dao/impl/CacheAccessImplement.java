package com.pintu.dao.impl;

import java.util.ArrayList;
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
import com.pintu.cache.PintuCache;
import com.pintu.dao.CacheAccessInterface;

public class CacheAccessImplement implements CacheAccessInterface {

	// Inject by Spring
	private PintuCache pintuCache;

	// Inject by Spring
	public CacheAccessImplement() {
		// 初始化要缓存对象ID的类型及容器，单个缓存，批量入库，批量删除
		toSavedUserPicIds.put(PICTURE_TYPE, new LinkedList<String>());
		toSavedCacheIds.put(STORY_TYPE, new HashMap<String,LinkedList<String>>());
		toSavedCacheIds.put(COMMENT_TYPE, new HashMap<String,LinkedList<String>>());
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
	public void cachePicture(TPicItem pic) {
		System.out.println("4 图片信息放缓存");
		// 1. 把对象放到pintuCache中
		// 2. 把ID放到toSavedCacheIds中的LinkedList中
		pintuCache.cachePicture( pic.getId(), pic);
		
		//注意：要把新建立的对象ID存起来，以便入库线程处理
		toSavedUserPicIds.get(PICTURE_TYPE).add(pic.getId());
	}
	
	@Override
	public void cacheComment(Comment comment) {
			String picId = comment.getFollow();
			String cmtId = comment.getId();
			pintuCache.cacheComment(picId,cmtId, comment);
			
			LinkedList<String> cmtIdList = toSavedCacheIds.get(COMMENT_TYPE).get(picId);
			if(cmtIdList == null){
				LinkedList<String> idList = new LinkedList<String>();
				idList.add(cmtId);
				toSavedCacheIds.get(COMMENT_TYPE).put(picId, idList);
			}else{
				toSavedCacheIds.get(COMMENT_TYPE).get(picId).add(cmtId);
			}
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
			String storyId = vote.getFollow();
			String voteId = vote.getId();
			pintuCache.cacheVote(vote.getFollow(),vote.getId(), vote);
			LinkedList<String> voteIdList = toSavedCacheIds.get(VOTE_TYPE).get(storyId);
			if(voteIdList == null){
				LinkedList<String> idList = new LinkedList<String>();
				idList.add(voteId);
				toSavedCacheIds.get(VOTE_TYPE).put(storyId, idList);
			}else{
				toSavedCacheIds.get(VOTE_TYPE).get(storyId).add(voteId);
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
		List<Object> resList = pintuCache.getCachedPicture(list);
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
		List<Object> resList = pintuCache.getCachedPicture(list);
		if(resList != null && resList.size() > 0){
			user = (User) resList.get(0);
		}
		return user;
	}
	
//	@Override
//	public Comment getSpecificComment(String cid) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//	
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
		//FIXME TODO 改
		if (type.equals(PICTURE_TYPE)) {
			ids = toSavedUserPicIds.get(PICTURE_TYPE);
			list = pintuCache.getCachedPicture(ids);
		} else if (type.equals(COMMENT_TYPE)) {
			map = toSavedCacheIds.get(COMMENT_TYPE);
			list = pintuCache.getCachedComment(map);
		} else if (type.equals(STORY_TYPE)) {
			map = toSavedCacheIds.get(STORY_TYPE);
			list = pintuCache.getCachedStory(map);
		} else if (type.equals(VOTE_TYPE)) {
			map = toSavedCacheIds.get(VOTE_TYPE);
			list = pintuCache.getCachedVote(map);
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
	public void syncDBCommnetToCache(Comment comment) {
		pintuCache.cacheComment(comment.getFollow(),comment.getId(), comment);
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
	public List<Comment> getCommentsOfPic(String tpId) {
		List<String> ids = new ArrayList<String>();
		ids.add(tpId);
		return pintuCache.getCachedCommentByPid(ids);
	}

	@Override
	public List<Story> getStoriesOfPic(String tpId) {
		List<String> ids = new ArrayList<String>();
		ids.add(tpId);
		return pintuCache.getCachedStoryByPid(ids);
	}

	@Override
	public User getUserById(String userId) {
		return pintuCache.getCachedUser(userId);
	}


} // end of class
