package com.pintu.dao.impl;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.pintu.beans.Comment;
import com.pintu.beans.Story;
import com.pintu.beans.TPicDesc;
import com.pintu.beans.TPicItem;
import com.pintu.beans.User;
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
		//缩略图不入库，所以不用存ID
//		toSavedCacheIds.put(THUMBNAIL_TYPE, new LinkedList<String>());
	}

	public void setPintuCache(PintuCache pintuCache) {
		this.pintuCache = pintuCache;
	}

	@Override
	public void cacheComment(Comment comment) {
		// TODO Auto-generated method stub

	}

	@Override
	public void cacheLoggedInUser() {
		// TODO Auto-generated method stub

	}

	@Override
	public void cachePicture(TPicItem pic) {
		System.out.println("4 图片信息放缓存");
		// 1. 把对象放到pintuCache中
		// 2. 把ID放到toSavedCacheIds中的LinkedList中
		pintuCache.cachePicture(pic.getId(), pic);
		//注意：要把新建立的对象ID存起来，以便入库线程处理
		toSavedCacheIds.get(PICTURE_TYPE).add(pic.getId());
		
		//FIXME, 小明，怎么这么添加元素呢，不啰嗦吗？
//		LinkedList<String> ids = new LinkedList<String>();
//		ids.addLast(pic.getId());		
//		toSavedCacheIds.get(PICTURE_TYPE).addAll(ids);
	}

	@Override
	public void cacheStory(Story story) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<User> getActiveUser() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Comment getSpecificComment(String cid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TPicItem getSpecificPic(String pid) {
		// TODO Auto-generated method stub
		return null;
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
	public Boolean deleteSavedObjIDs(String type) {
		// TODO Auto-generated method stub
		return null;
	}

	// FIXME, MODIFIED BY LWZ7512, GET CACHEED OBJS BY TYPE
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

	// 在ImageFileCreationTask中生成缩略图后，调用此方法
	@Override
	public void cacheThumbnail(TPicDesc tpicDesc) {
		// 缓存缩略图对象
		pintuCache.cacheThumbnail(tpicDesc.getThumbnailId(), tpicDesc);

		// FIXME, 小明，缩略图ID不用缓存吧，又不入库？
		// LinkedList<String> ids = new LinkedList<String>();
		// ids.addLast(tpicDesc.getThumbnailId());
		// CacheAccessInterface.toSavedCacheIds.get(CacheAccessInterface.THUMBNAIL_TYPE).addAll(ids);

	}

} // end of class
