package com.pintu.ads.dao;

import java.util.List;

import com.pintu.ads.beans.Ads;

public interface AdsDBInterface {

	public List<Ads> getTodayAds(String today);
	
	public List<Ads> serarchAds(String keys,String time);
	
	public int deleteAdsById(String adId);
	
	public int createAds(Ads ad);
	
	public Ads getAdsById(String adId);
	
	public int updateAdsById(String adId,Ads ad);
	
}
