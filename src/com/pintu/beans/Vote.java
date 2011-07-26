package com.pintu.beans;

import java.io.Serializable;

public class Vote implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private Boolean saved;

	private int id;
	//是对哪个故事的投票
	private int follow;
	//投票的类型
	private String type;
	//数量
	private int amount;
	
	
	public Vote() {
		// TODO Auto-generated constructor stub
	}


	public Boolean getSaved() {
		return saved;
	}


	public void setSaved(Boolean saved) {
		this.saved = saved;
	}


	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public int getFollow() {
		return follow;
	}


	public void setFollow(int follow) {
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
