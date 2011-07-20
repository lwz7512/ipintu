package com.pintu.dao.impl;

import com.pintu.cache.PintuCache;
import com.pintu.dao.CacheAccessInterface;

public class CacheAccessImplement implements CacheAccessInterface {

	//ÓÉSpring×¢Èë
	private PintuCache pintuCache;
	
	//Constructor
	public CacheAccessImplement(){
		
	}
		
	public void setPintuCache(PintuCache pintuCache){
		this.pintuCache = pintuCache;
	}
	
}
