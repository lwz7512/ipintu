package com.pintu.beans;
/**
 * test git push, no origin and master by lwz7512
 */
import java.io.Serializable;

public class Comment implements Serializable{

	private static final long serialVersionUID = 9200624460999505366L;
	
	private String id;
	private String follow;
	private String owner;
	private String publishTime;
	private String content;
	
	//作者，其实为ower对应的用户账号
	private String author;
	
	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public Comment() {
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

	public String getPublishTime() {
		return publishTime;
	}

	public void setPublishTime(String string) {
		this.publishTime = string;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}


	public boolean isValid() {
		boolean flag = false;
		if (this.id != null && this.follow != null
				&& this.owner != null && this.publishTime != null
				&& this.content != null) {
			flag = true;
		} 
		return flag;
	}

}
