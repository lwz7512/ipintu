package com.pintu.beans;

import java.util.List;

public class Story {

	//Ʒͼ�����£�Ψһ��ʶ
	private String id;
	//��ͼID����Ӧ��TPicItem.id
	private String followId;
	//Ʒͼ����
	private String owner;
	//����ʱ��
	private String publishTime;
	//Ʒͼ����
	private String content;
	//�Ƿ�ͶƱΪ����
	private String classical;
	//����ͶƱ
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
