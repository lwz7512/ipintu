package com.pintu.beans;

public class Wealth {
	//财富分以下四种类型，分别代表海贝，铜贝，银贝和金贝
	public static String ONE_YUAN="seashell";
	public static String TEN_YUAN="coppershell";
	public static String FIFTY_YUAN="silvershell";
	public static String HUNDRED_YUAN="goldshell";
	//再增加一种用来标识不能换算的可用积分
	public static String REMAIN_SCORE="remainscore";
	
	private String id;
	private String owner;
	private String type;
	private int amount;
	

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
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

	public Wealth() {
		// TODO Auto-generated constructor stub
	}

}
