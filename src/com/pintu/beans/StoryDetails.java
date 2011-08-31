package com.pintu.beans;

public class StoryDetails extends Story{

	private static final long serialVersionUID = 8075956658153137206L;

	private String author;
	
	//用户头像，用户表存文件路径，从磁盘上找图
	private String avatarImgPath;
	
	private int flower;
	
	private int heart;
	
	private int egg;
	
	private int star;

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public int getFlower() {
		return flower;
	}

	public void setFlower(int flower) {
		this.flower = flower;
	}

	public int getHeart() {
		return heart;
	}

	public void setHeart(int heart) {
		this.heart = heart;
	}

	public int getEgg() {
		return egg;
	}

	public void setEgg(int egg) {
		this.egg = egg;
	}

	public int getStar() {
		return star;
	}

	public void setStar(int star) {
		this.star = star;
	}

	public String getAvatarImgPath() {
		return avatarImgPath;
	}

	public void setAvatarImgPath(String avatarImgPath) {
		this.avatarImgPath = avatarImgPath;
	}

	
}
