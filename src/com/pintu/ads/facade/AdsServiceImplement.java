package com.pintu.ads.facade;

import java.util.List;
import java.util.Properties;

import com.pintu.ads.beans.Ads;
import com.pintu.ads.dao.AdsDBInterface;

public class AdsServiceImplement implements AdsServiceInterface {

	// 由Spring注入
	private AdsDBInterface adDbVisitor;
	
	private Properties systemConfigurer;

	public void setSystemConfigurer(Properties systemConfigurer) {
		this.systemConfigurer = systemConfigurer;
	}
	
	public void setAdDbVisitor(AdsDBInterface adDbVisitor) {
		this.adDbVisitor = adDbVisitor;
	}

	@Override
	public List<Ads> getTodayAds(String today) {
		List<Ads> adList = adDbVisitor.getTodayAds(today);
		return adList;
	}

	@Override
	public List<Ads> searchAds(String keys, String time) {
		List<Ads> adList = adDbVisitor.serarchAds(keys, time);
		return adList;
	}

	@Override
	public String deleteAdsById(String adId) {
		int rows = adDbVisitor.deleteAdsById(adId);
		if(rows == 1){
			return systemConfigurer.getProperty("rightPrompt");
		}else{
			return systemConfigurer.getProperty("wrongPrompt");
		}
	}

	@Override
	public Ads getAdsById(String adId) {
		Ads ad = adDbVisitor.getAdsById(adId);
		return ad;
	}

}
