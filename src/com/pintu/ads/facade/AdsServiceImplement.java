package com.pintu.ads.facade;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import com.pintu.ads.beans.Ads;
import com.pintu.ads.beans.Vender;
import com.pintu.ads.dao.AdsDBInterface;
import com.pintu.utils.Encrypt;
import com.pintu.utils.PintuUtils;

public class AdsServiceImplement implements AdsServiceInterface {

	// 由Spring注入
	private AdsDBInterface adDbVisitor;
	
	private Properties systemConfigurer;
	
	private Properties propertyConfigurer;
	
	public void setSystemConfigurer(Properties systemConfigurer) {
		this.systemConfigurer = systemConfigurer;
	}
	
	public void setPropertyConfigurer(Properties propertyConfigurer) {
		this.propertyConfigurer = propertyConfigurer;
	}

	public void setAdDbVisitor(AdsDBInterface adDbVisitor) {
		this.adDbVisitor = adDbVisitor;
	}

	@Override
	public List<Ads> getTodayAds(String today,String userId) {
		List<Ads> adList = new ArrayList<Ads>();
		if(userId.equals(propertyConfigurer.get("admin").toString())){
			adList = adDbVisitor.getTodayAds(today,null);
		}else{
			adList = adDbVisitor.getTodayAds(today,userId);
		}
		
		return adList;
	}

	@Override
	public List<Ads> searchAds(String keys, String time,String venderId) {
		List<Ads> adList = new ArrayList<Ads>();
		if(venderId.equals(propertyConfigurer.get("admin").toString())){
			adList = adDbVisitor.serarchAds(keys, time,null);
		}else{
			adList = adDbVisitor.serarchAds(keys, time, venderId);
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
	public String createAds(String publisher, String type,String imgPath, String priority,
			String startTime, String endTime, String content, String link) {
		Ads ad = generateAd(null,publisher,type,imgPath,priority,startTime,endTime,content,link);
		int rows = adDbVisitor.createAds(ad);
		if(rows == 1){
			return systemConfigurer.getProperty("rightPrompt");
		}else{
			return systemConfigurer.getProperty("wrongPrompt");
		}
	}

	@Override
	public String updateAdsById(String adId, String publisher, String type, String imgPath,
			String priority, String startTime, String endTime, String content,
			String link) {
		Ads ad = generateAd(adId,publisher,type,imgPath,priority,startTime,endTime,content,link);
		int rows = adDbVisitor.updateAdsById(adId, ad);
		if(rows == 1){
			return systemConfigurer.getProperty("rightPrompt");
		}else{
			return systemConfigurer.getProperty("wrongPrompt");
		}
	}

	private Ads generateAd(String adId, String publisher, String type, String imgPath,
			String priority, String startTime, String endTime, String content,
			String link){
		Ads ad = new Ads();
		if(adId != null){
			ad.setId(adId);
		}else{
			ad.setId(PintuUtils.generateUID());
		}
		ad.setImgPath(imgPath);
		ad.setPublisher(publisher);
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
		ad.setEnable(1);
		return ad;
	}

	@Override
	public String getExistVender(String email, String pwd) {
		String md5Pwd = Encrypt.encrypt(pwd);
		Vender vender = adDbVisitor.getExistVender(email);
		if (vender != null && vender.getId() != null) {
			if (vender.getPwd().equals(md5Pwd)) {
				String rold = vender.getRole();
				//判断用户角色，若不是管理员则需要返回是否到期的状态字段
				if(!"admin".equals(rold)){
					Date deadTime = PintuUtils.parseToDate(vender.getDeadTime());
					Date nowTime =  PintuUtils.parseToDate(PintuUtils.getToday());
					String state = "effective";
					if(deadTime.getTime() < nowTime.getTime()){
						state = "dead";
					}
					return vender.getRole() + "@" + vender.getId() +"@" +vender.getName()+"@"+ state;
				}else{
					return vender.getRole() + "@" + vender.getId()+"@" +vender.getName();
				}
			} else {
				return "0";
			}
		}
		return "-1";
	}

	@Override
	public int changePwd(String newPwd, String userId) {
		String md5Pwd = Encrypt.encrypt(newPwd);
		int rows = adDbVisitor.changePwd(md5Pwd,userId);
		return rows;
	}

	@Override
	public List<Vender> searchVenders(String keys) {
		List<Vender> venderList = adDbVisitor.serarchVenders(keys);
		return venderList;
	}

	@Override
	public Vender getVendersById(String venderId) {
		Vender vender = adDbVisitor.getVendersById(venderId);
		return vender;
	}

	@Override
	public String updateVendersById(String venderId, String name, String email,
			String serviceLevel, String effectiveTime, String deadTime,
			String deployDNS, String enable) {
		Vender vender = generateVender(venderId,name,email,serviceLevel,effectiveTime,deadTime,deployDNS,enable);
		int rows = adDbVisitor.updateVendersById(venderId, vender);
		if(rows == 1){
			return systemConfigurer.getProperty("rightPrompt");
		}else{
			return systemConfigurer.getProperty("wrongPrompt");
		}
	}
	
	@Override
	public String createVenders(String name, String email, String serviceLevel,
			String effectiveTime, String deadTime, String deployDNS, String enable) {
		Vender vender = generateVender(null,name,email,serviceLevel,effectiveTime,deadTime,deployDNS,enable);
		int rows = adDbVisitor.createVender(vender);
		if(rows == 1){
			return systemConfigurer.getProperty("rightPrompt");
		}else{
			return systemConfigurer.getProperty("wrongPrompt");
		}
	}

	private Vender generateVender(String venderId, String name, String email,
			String serviceLevel, String effectiveTime, String deadTime,
			String deployDNS, String enable) {
		Vender vender = new Vender();
		if(venderId != null){
			vender.setId(venderId);
		}else{
			vender.setId(PintuUtils.generateUID());
		}
		vender.setPwd( Encrypt.encrypt("123456"));
		vender.setName(name);
		vender.setEmail(email);
		vender.setServiceLevel(serviceLevel);
		vender.setEnable(Integer.parseInt(enable));
		vender.setCreateTime(PintuUtils.getFormatNowTime());
		vender.setEffectiveTime(effectiveTime);
		vender.setDeadTime(deadTime);
		vender.setDeployDNS(deployDNS);
		vender.setRole("vender");
		return vender;
	}
	
	@Override
	public String validateVender(String venderId) {
		Vender vender = adDbVisitor.getVenderById(venderId);
		Date deadTime = PintuUtils.parseToDate(vender.getDeadTime());
		Date nowTime =  PintuUtils.parseToDate(PintuUtils.getToday());
		if(deadTime.getTime() < nowTime.getTime()){
			//判断当前用户是否已过期
			return "false";
		}else{
			return "true";
		}
	}

	@Override
	public String registVenders(String name, String email, String pwd,
			String deployDNS) {
		String venderId = PintuUtils.generateUID();
		String serviceLevel = "free";
		String effectiveTime = PintuUtils.getToday();
		String deadTime = PintuUtils.getAmonthAgo();
		String enable = "1";
		Vender vender = this.generateRegVender(venderId, name, email, pwd, serviceLevel, effectiveTime, deadTime, deployDNS, enable);
		int rows = adDbVisitor.createVender(vender);
		if(rows == 1){
			return systemConfigurer.getProperty("rightPrompt");
		}else{
			return systemConfigurer.getProperty("wrongPrompt");
		}
	}
	
	private Vender generateRegVender(String venderId, String name, String email,String pwd,
			String serviceLevel, String effectiveTime, String deadTime,
			String deployDNS, String enable) {
		Vender vender = new Vender();
		if(venderId != null){
			vender.setId(venderId);
		}else{
			vender.setId(PintuUtils.generateUID());
		}
		vender.setPwd(Encrypt.encrypt(pwd));
		vender.setName(name);
		vender.setEmail(email);
		vender.setServiceLevel(serviceLevel);
		vender.setEnable(Integer.parseInt(enable));
		vender.setCreateTime(PintuUtils.getFormatNowTime());
		vender.setEffectiveTime(effectiveTime);
		vender.setDeadTime(deadTime);
		vender.setDeployDNS(deployDNS);
		vender.setRole("vender");
		return vender;
	}

	@Override
	public int checkoutRegiser(String email) {
		Vender vender = adDbVisitor.getExistVender(email);
		if (vender != null && vender.getId() != null) {
			return 1;
		}
		return 0;
	}
}
