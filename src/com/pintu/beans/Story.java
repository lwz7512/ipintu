package com.pintu.beans;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Story implements Serializable{

	private static final long serialVersionUID = 1L;
	
	//品图（故事）唯一标识
	private String id;
	//贴图ID，对应于TPicItem.id
	private String follow;
	//品图作者
	private String owner;
	//发表时间
	private Date publishTime;
	//品图内容
	private String content;
	//是否被投票为经典
	private int classical;
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



	public String getFollow() {
		return follow;
	}



	public void setFollow(String follow) {
		this.follow = follow;
	}



	public String getOwner() {
		return owner;
	}



	public void setOwner(String owner) {
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


<<<<<<< HEAD

	public Boolean getSaved() {
		return saved;
	}



	public void setSaved(Boolean saved) {
		this.saved = saved;
	}


=======
>>>>>>> upstream/master
	
}
