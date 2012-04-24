package com.pintu.ads.dao;

import java.util.List;

import com.pintu.ads.beans.Ads;
import com.pintu.ads.beans.Vender;

public interface AdsDBInterface {

	public List<Ads> getTodayAds(String today, String userId);
	
	public List<Ads> serarchAds(String keys,String time, String userId);
	
	public int deleteAdsById(String adId);
	
	public int createAds(Ads ad);
	
	public Ads getAdsById(String adId);
	
	public int updateAdsById(String adId,Ads ad);

	public Vender getVenderById(String venderId);

	public Vender getExistVender(String email);

	public int changePwd(String newPwd, String venderId);

	public List<Vender> serarchVenders(String keys);

	public Vender getVendersById(String venderId);

	public int updateVendersById(String venderId, Vender vender);

	public int createVender(Vender vender);
	
}
