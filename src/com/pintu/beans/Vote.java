package com.pintu.beans;

import java.io.Serializable;

public class Vote implements Serializable{

	private static final long serialVersionUID = -2728077033301616771L;
	
//	//投票分以下四种类型，分别代表爱心，喜欢，扔蛋和经典
//	public static String HEART_TYPE="heart";
//	public static String FLOWER_TYPE="flower";
//	public static String EGG_TYPE="egg";
//	public static String STAR_TYPE="star";
	
	public static String COOL_TYPE="cool";
	
	private String id;
	//是对哪个故事的投票
	private String follow;
	//投票的类型
	private String type;
	//数量
	private int amount;
	
	//投票作者
	private String voter;
	//所投故事的作者
	private String receiver;

	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	public String getVoter() {
		return voter;
	}

	public void setVoter(String voter) {
		this.voter = voter;
	}



	public Vote() {
		// TODO Auto-generated constructor stub
	}



	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public String getFollow() {
		return follow;
	}


	public void setFollow(String follow) {
		this.follow = follow;
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	public int getAmount() {
		return amount;
	}


	public void setAmount(int amount) {
		this.amount = amount;
	}
	
	public boolean isValid() {
		boolean flag = false;
		if (this.id != null && this.follow != null
				&& this.type != null && this.amount > -1 ){
//				&& this.receiver != null && this.voter != null
			flag = true;
		} 
		return flag;
	}
	

}
