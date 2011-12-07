package com.pintu.beans;
/**
 * 用于检测图片
 * @author lml
 *
 */

public class TPicReview extends TPicDesc{
	
	private static final long serialVersionUID = 5468051525567281206L;
	
	//作者id
	private String userId;
	// 贴图作者昵称
	private String author;
	// 贴图描述
	private String description; 
	// 标签
	private String tags;
	//发布时间
	private String publishTime;
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getTags() {
		return tags;
	}
	public void setTags(String tags) {
		this.tags = tags;
	}
	public String getPublishTime() {
		return publishTime;
	}
	public void setPublishTime(String publishTime) {
		this.publishTime = publishTime;
	}
	

}
