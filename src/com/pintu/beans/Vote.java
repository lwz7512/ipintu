package com.pintu.beans;

import java.io.Serializable;

public class Vote implements Serializable{

	private static final long serialVersionUID = 1L;
	
<<<<<<< HEAD
	private Boolean saved;
=======
>>>>>>> upstream/master

	private String id;
	//是对哪个故事的投票
	private String follow;
	//投票的类型
	private String type;
	//数量
	private int amount;
	
	
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
	
	

}
