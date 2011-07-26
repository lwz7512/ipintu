package com.pintu.beans;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Story implements Serializable{

	private static final long serialVersionUID = 1L;
	
	//品图（故事）唯一标识
	private int id;
	//贴图ID，对应于TPicItem.id
	private int followId;
	//品图作者
	private int owner;
	//发表时间
	private Date publishTime;
	//品图内容
	private String content;
	//是否被投票为经典
	private int classical;
	//所有投票
	private List<Vote> votes;
	
	//是否已经被同步入库，用于同步过程中遍历缓存时判断
	//如果为真表示已入库，如果为假表示为入库
	private Boolean saved;

	
	
	public Story() {
		// TODO Auto-generated constructor stub
	}



	public int getId() {
		return id;
	}



	public void setId(int id) {
		this.id = id;
	}



	public int getFollowId() {
		return followId;
	}



	public void setFollowId(int followId) {
		this.followId = followId;
	}



	public int getOwner() {
		return owner;
	}



	public void setOwner(int owner) {
		this.owner = owner;
	}



	public Date getPublishTime() {
		return publishTime;
	}



	public void setPublishTime(Date publishTime) {
		this.publishTime = publishTime;
	}



	public String getContent() {
		return content;
	}



	public void setContent(String content) {
		this.content = content;
	}



	public int getClassical() {
		return classical;
	}



	public void setClassical(int classical) {
		this.classical = classical;
	}



	public List<Vote> getVotes() {
		return votes;
	}



	public void setVotes(List<Vote> votes) {
		this.votes = votes;
	}



	public Boolean getSaved() {
		return saved;
	}



	public void setSaved(Boolean saved) {
		this.saved = saved;
	}


	
}
