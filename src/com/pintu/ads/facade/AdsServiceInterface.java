package com.pintu.ads.facade;

import java.util.List;

import com.pintu.ads.beans.Ads;

public interface AdsServiceInterface {

	public  List<Ads>  getTodayAds(String today);

	public List<Ads> searchAds(String keys, String time);

	public String deleteAdsById(String adId);

	public Ads getAdsById(String adId);

}
