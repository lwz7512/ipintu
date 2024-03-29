package com.pintu.beans;

import java.io.Serializable;
import java.util.HashMap;


public class User implements Serializable{
	
	private static final long serialVersionUID = 2083131853845725364L;
	
	public static HashMap<Integer, Integer> levelScoreMap = new HashMap<Integer, Integer>();

	private String id;
	//用户名
	private String account;
	//昵称
	private String nickName;
	//密码
	private String pwd;
	//肖像
	private String avatar="default";
	//角色
	private String role="general";
	//级别
	private int level;
	//总积分
	private int score;
	//可兑换积分
	private int exchangeScore;
	//注册时间
	private String registerTime;
	
	//最后动作时间（即最后更新时间）存毫秒数
	private Long lastUpdateTime;
	
	private int storyNum;
	
	private int tpicNum;

	public int getStoryNum() {
		return storyNum;
	}

	public void setStoryNum(int storyNum) {
		this.storyNum = storyNum;
	}

	public int getTpicNum() {
		return tpicNum;
	}

	public void setTpicNum(int tpicNum) {
		this.tpicNum = tpicNum;
	}

	public Long getLastUpdateTime() {
		return lastUpdateTime;
	}


	public void setLastUpdateTime(Long lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}


	public String getAccount() {
		return account;
	}


	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public void setAccount(String account) {
		this.account = account;
	}


	public String getPwd() {
		return pwd;
	}


	public void setPwd(String pwd) {
		this.pwd = pwd;
	}


	public String getAvatar() {
		return avatar;
	}


	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}


	public String getRole() {
		return role;
	}


	public void setRole(String role) {
		this.role = role;
	}


	public int getLevel() {
		return level;
	}


	public void setLevel(int level) {
		this.level = level;
	}


	public int getScore() {
		return score;
	}


	public void setScore(int score) {
		this.score = score;
	}


	public int getExchangeScore() {
		return exchangeScore;
	}


	public void setExchangeScore(int exchangeScore) {
		this.exchangeScore = exchangeScore;
	}


	public String getRegisterTime() {
		return registerTime;
	}

	public void setRegisterTime(String registerTime) {
		this.registerTime = registerTime;
	}
	
		
	public User() {			
		//等级与积分的对应关系。。。
		User.levelScoreMap.put(1, 10);
		User.levelScoreMap.put(2, 20);
		User.levelScoreMap.put(3, 50);
		User.levelScoreMap.put(4, 100);
	}

}
