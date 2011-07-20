package com.pintu.beans;

import java.util.List;

public class Story {

	//品图（故事）唯一标识
	private String id;
	//贴图ID，对应于TPicItem.id
	private String followId;
	//品图作者
	private String owner;
	//发表时间
	private String publishTime;
	//品图内容
	private String content;
	//是否被投票为经典
	private String classical;
	//所有投票
	private List<Vote> votes;
	
	
	public Story() {
		// TODO Auto-generated constructor stub
	}


	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public String getFollowId() {
		return followId;
	}


	public void setFollowId(String followId) {
		this.followId = followId;
	}


	public String getOwner() {
		return owner;
	}


	public void setOwner(String owner) {
		this.owner = owner;
	}


	public String getPublishTime() {
		return publishTime;
	}


	public void setPublishTime(String publishTime) {
		this.publishTime = publishTime;
	}


	public String getContent() {
		return content;
	}


	public void setContent(String content) {
		this.content = content;
	}


	public String getClassical() {
		return classical;
	}


	public void setClassical(String classical) {
		this.classical = classical;
	}


	public List<Vote> getVotes() {
		return votes;
	}


	public void setVotes(List<Vote> votes) {
		this.votes = votes;
	}

	
	
}
