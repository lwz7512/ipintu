package com.pintu.beans;

/**
 * 品图详情，比品图条目TPicItem多用户的基本资料
 * 用在点击缩略图后的内容展示
 * @author lwz
 *
 */
public class TPicDetails extends TPicItem {

	private static final long serialVersionUID = -8555839266466344516L;
	
	//作者（账号）
	private String author;

	//用户头像，用户表存文件路径，从磁盘上找图
	private String avatarImgPath;
	
	//用户积分
	private int score;
	
	//用户等级
	private int level;
	
	//品图数目（故事）数目
	private int storiesNum;

	//为今日热点做累加点击数
	private int counter;
	
	//被称赞的数目
	private int coolCount;

	public int getCoolCount() {
		return coolCount;
	}


	public void setCoolCount(int coolCount) {
		this.coolCount = coolCount;
	}


	public int getCounter() {
		return counter;
	}

	
	public void setCounter(int counter) {
		this.counter = counter;
	}
	public int getStoriesNum() {
		return storiesNum;
	}


	public void setStoriesNum(int storiesNum) {
		this.storiesNum = storiesNum;
	}



	public TPicDetails() {
		
	}


	public String getAvatarImgPath() {
		return avatarImgPath;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}


	public void setAvatarImgPath(String avatarImgPath) {
		this.avatarImgPath = avatarImgPath;
	}


	public int getScore() {
		return score;
	}


	public void setScore(int score) {
		this.score = score;
	}


	public int getLevel() {
		return level;
	}


	public void setLevel(int level) {
		this.level = level;
	}

}
