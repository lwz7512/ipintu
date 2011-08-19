package com.pintu.beans;

import java.io.Serializable;

public class Story implements Serializable {

	private static final long serialVersionUID = 8714518954157101889L;
	// 品图（故事）唯一标识
	private String id;
	// 贴图ID，对应于TPicItem.id
	private String follow;
	// 品图作者
	private String owner;
	// 发表时间
	private String publishTime;
	// 品图内容
	private String content;
	// 是否被投票为经典
	private int classical;

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

	public int getClassical() {
		return classical;
	}

	public void setClassical(int classical) {
		this.classical = classical;
	}

	/**
	 * 校验类是否完全被赋值
	 * 
	 * @param vote
	 * @return
	 */
	public boolean isValid() {
		boolean flag = false;
		if (this.id != null && this.follow != null
				&& this.owner != null && this.publishTime != null
				&& this.content != null && this.classical >-1) {
			flag = true;
		}
		return flag;
	}
}
