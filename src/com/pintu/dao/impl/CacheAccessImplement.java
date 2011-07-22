package com.pintu.dao.impl;

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
	
}
