package com.pintu.beans;

public class UserExtend {

	private String id;
	private String gender;
	private String location;
	private String contract;
	private String descripttion;
	private String personalUrl;
	private String uid;
	private String token;
	//token的到期时间
	private String tokenExpiration;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getContract() {
		return contract;
	}
	public void setContract(String contract) {
		this.contract = contract;
	}
	public String getDescripttion() {
		return descripttion;
	}
	public void setDescripttion(String descripttion) {
		this.descripttion = descripttion;
	}
	
	public String getPersonalUrl() {
		return personalUrl;
	}
	public void setPersonalUrl(String personalUrl) {
		this.personalUrl = personalUrl;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getTokenExpiration() {
		return tokenExpiration;
	}
	public void setTokenExpiration(String tokenExpiration) {
		this.tokenExpiration = tokenExpiration;
	}

}
