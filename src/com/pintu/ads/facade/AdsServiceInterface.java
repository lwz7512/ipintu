package com.pintu.ads.facade;

import java.util.List;

import com.pintu.ads.beans.Ads;
import com.pintu.ads.beans.Vender;

public interface AdsServiceInterface {

	public  List<Ads>  getTodayAds(String today, String venderId);

	public List<Ads> searchAds(String keys, String time, String venderId);

	public String deleteAdsById(String adId);

	public Ads getAdsById(String adId);
	
	public String createAds(String publisher, String type, String imgPath, String priority,
			String startTime, String endTime, String content, String link);

	public String updateAdsById(String adId, String publisher, String type,String imgPath, 
			String priority, String startTime, String endTime, String content,
			String link);

	public String getExistVender(String email, String pwd);

	public int changePwd(String newPwd, String venderId);

	public List<Vender> searchVenders(String keys);

	public Vender getVendersById(String venderId);

	public String updateVendersById(String venderId, String name, String email,
			String serviceLevel, String effectiveTime, String deadTime,
			String deployDNS, String enable);

	public String createVenders(String name, String email, String serviceLevel,
			String effectiveTime, String deadTime, String deployDNS, String enable);

	public String validateVender(String venderId);

	public String registVenders(String name, String email, String pwd,
			String deployDNS);

	public int checkoutRegiser(String email);
}
