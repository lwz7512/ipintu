package com.pintu.dao.impl;

import java.util.List;

import com.pintu.beans.Comment;
import com.pintu.beans.Story;
import com.pintu.beans.TPicItem;
import com.pintu.beans.User;
import com.pintu.cache.PintuCache;
import com.pintu.dao.CacheAccessInterface;

public class CacheAccessImplement implements CacheAccessInterface {

	//Inject by Spring
	private PintuCache pintuCache;
	
	//Inject by Spring
	public CacheAccessImplement(){
		
	}
		
	public void setPintuCache(PintuCache pintuCache){
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
		// TODO Auto-generated method stub
		
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
	
}
