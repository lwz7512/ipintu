package com.pintu.beans;
/**
 * 用于微博用户授权登录后返回给客户端
 * @author lml
 *
 */

public class AccessUser {

	private String userId;
	
	private String role = "weibo";
	//有效期限
	private String expireIn;
	
	private String uid;
	
	private String accessToken;
	
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getAccessToken() {
		return accessToken;
	}
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public String getExpireIn() {
		return expireIn;
	}
	public void setExpireIn(String expireIn) {
		this.expireIn = expireIn;
	}
	
}
