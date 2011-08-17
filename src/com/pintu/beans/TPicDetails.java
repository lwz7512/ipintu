package com.pintu.beans;

/**
 * 品图详情，比品图条目TPicItem多用户的基本资料
 * 用在点击缩略图后的内容展示
 * @author lwz
 *
 */
public class TPicDetails extends TPicItem {

	private static final long serialVersionUID = -8555839266466344516L;
	
	//用户名
	private String userName;

	//用户头像，用户表存文件路径，从磁盘上找图
	private String avatarImgPath;
	
	//用户积分
	private int score;
	
	//用户等级
	private int level;
	
	//品图数目（故事）数目
	public String storiesNum;
	//评论数目
	public String commentsNum;

	
	public String getstoriesNum() {
		return storiesNum;
	}


	public void setStoriesNum(String storiesNum) {
		this.storiesNum = storiesNum;
	}


	public String getCommentsNum() {
		return commentsNum;
	}


	public void setCommentsNum(String commentsNum) {
		this.commentsNum = commentsNum;
	}


	public TPicDetails() {
		
	}


	public String getAvatarImgPath() {
		return avatarImgPath;
	}


	public String getUserName() {
		return userName;
	}


	public void setUserName(String userName) {
		this.userName = userName;
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
