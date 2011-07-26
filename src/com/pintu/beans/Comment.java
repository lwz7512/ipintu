package com.pintu.beans;
/**
 * test git push, no origin and master by lwz7512
 */
import java.io.Serializable;
import java.util.Date;

public class Comment implements Serializable{

	private static final long serialVersionUID = 1L;
	
	
	//是否已经被同步入库，用于同步过程中遍历缓存时判断
	//如果为真表示已入库，如果为假表示为入库
	private Boolean saved;

	private int id;
	private int follow;
	private int owner;
	private Date publishTime;
	private String content;
	
	public Comment() {
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
	
	

}
