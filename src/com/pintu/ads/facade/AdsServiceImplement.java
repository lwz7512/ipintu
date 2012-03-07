package com.pintu.ads.facade;

import java.util.List;
import java.util.Properties;

import com.pintu.ads.beans.Ads;
import com.pintu.ads.dao.AdsDBInterface;
import com.pintu.utils.PintuUtils;

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
		if(adList != null){
			for(int i=0;i<adList.size();i++){
				
			}
		}
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

	@Override
	public String createAds(String vender, String type,String imgPath, String priority,
			String startTime, String endTime, String content, String link) {
		Ads ad = generateAd(null,vender,type,imgPath,priority,startTime,endTime,content,link);
		int rows = adDbVisitor.createAds(ad);
		if(rows == 1){
			return systemConfigurer.getProperty("rightPrompt");
		}else{
			return systemConfigurer.getProperty("wrongPrompt");
		}
	}

	@Override
	public String updateAdsById(String adId, String vender, String type, String imgPath,
			String priority, String startTime, String endTime, String content,
			String link) {
		Ads ad = generateAd(adId,vender,type,imgPath,priority,startTime,endTime,content,link);
		int rows = adDbVisitor.updateAdsById(adId, ad);
		if(rows == 1){
			return systemConfigurer.getProperty("rightPrompt");
		}else{
			return systemConfigurer.getProperty("wrongPrompt");
		}
	}

	private Ads generateAd(String adId, String vender, String type, String imgPath,
			String priority, String startTime, String endTime, String content,
			String link){
		Ads ad = new Ads();
		if(adId != null){
			ad.setId(adId);
		}else{
			ad.setId(PintuUtils.generateUID());
		}
		ad.setImgPath(imgPath);
		ad.setVender(vender);
		ad.setType(type);
		if(type.equals("image")){
			ad.setContent("");
		}else{
			ad.setContent(content);
		}
		ad.setLink(link);
		ad.setPriority(Integer.parseInt(priority));
		ad.setCreateTime(PintuUtils.getFormatNowTime());
		ad.setStartTime(startTime);
		ad.setEndTime(endTime);
		ad.setDisabled(1);
		return ad;
	}
}
