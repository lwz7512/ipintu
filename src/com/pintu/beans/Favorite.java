package com.pintu.beans;

import java.util.Date;

public class Favorite {

	private int id;
	private int owner;
	private int picture;
	private Date collectTime;
	
	public Favorite() {
		// TODO Auto-generated constructor stub
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getOwner() {
		return owner;
	}

	public void setOwner(int owner) {
		this.owner = owner;
	}

	public int getPicture() {
		return picture;
	}

	public void setPicture(int picture) {
		this.picture = picture;
	}

	public Date getCollectTime() {
		return collectTime;
	}

	public void setCollectTime(Date collectTime) {
		this.collectTime = collectTime;
	}

}
