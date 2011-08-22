package com.pintu.beans;

import java.io.Serializable;

/**
 * 品图详情资料，包括品图作者、时间、手机浏览图位置、故事数目、评论数目等等 用在热图列表单元或者历史经典列表单元中展示
 * 
 * @author lwz
 * 
 */
public class TPicItem implements Serializable {

	private static final long serialVersionUID = -2683679418482593677L;
	// 贴图ID
	private String id;
	// 贴图名称，这个其实用不着，留着吧
	private String name;
	// 贴图作者
	private String owner;

	// 发布时间，可以保存为毫秒数
	// 到达客户端时，再格式化为xx分钟或者xx小时前
	private String publishTime;

	// 贴图描述
	private String description;
	// 贴图标签
	private String tags;
	// 是否允许品图
	private int allowStory;
	// 生成的移动图ID，这个ID是由pId+"_Mob"构成
	private String mobImgId;
	// 生成的移动图文件大小，单位是KB
	private String mobImgSize;
	// 生成的移动图磁盘保存路径
	private String mobImgPath;
	// 生成的原始图ID，这个ID是由pId+"_Raw"构成
	private String rawImgId;
	// 生成的原始图大小，单位是KB
	private String rawImgSize;
	// 生成的原始图磁盘保存路径
	private String rawImgPath;
	// 是否审核通过，默认都是1，即通过，不通过变为0
	private int pass;
	
	//为今日热点做累加点击数
	private int counter;

	public int getCounter() {
		return counter;
	}

	/**
	 * 这里给一个点击数，做累加
	 */
	public void setCounter(int counter) {
		this.counter += counter;
	}

	public TPicItem() {

	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
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

	public void setPublishTime(String string) {
		this.publishTime = string;
	}

	public int getAllowStory() {
		return allowStory;
	}

	public void setAllowStory(int allowStory) {
		this.allowStory = allowStory;
	}

	public String getMobImgId() {
		return mobImgId;
	}

	public void setMobImgId(String mobImgId) {
		this.mobImgId = mobImgId;
	}

	public String getMobImgSize() {
		return mobImgSize;
	}

	public void setMobImgSize(String mobImgSize) {
		this.mobImgSize = mobImgSize;
	}

	public String getMobImgPath() {
		return mobImgPath;
	}

	public void setMobImgPath(String mobImgPath) {
		this.mobImgPath = mobImgPath;
	}

	public String getRawImgId() {
		return rawImgId;
	}

	public void setRawImgId(String rawImgId) {
		this.rawImgId = rawImgId;
	}

	public String getRawImgSize() {
		return rawImgSize;
	}

	public void setRawImgSize(String rawImgSize) {
		this.rawImgSize = rawImgSize;
	}

	public String getRawImgPath() {
		return rawImgPath;
	}

	public void setRawImgPath(String rawImgPath) {
		this.rawImgPath = rawImgPath;
	}

	public int getPass() {
		return pass;
	}

	public void setPass(int pass) {
		this.pass = pass;
	}

	/**
	 * 校验类是否完全被赋值
	 * 
	 * @param vote
	 * @return
	 */
	public boolean isValid() {
		boolean flag = false;
		if (this.id != null && this.allowStory > -1 && this.description != null
				&& this.publishTime != null && this.mobImgId != null
				&& this.mobImgPath != null && this.mobImgSize != null
				&& this.rawImgSize != null && this.rawImgId != null
				&& this.rawImgPath != null && this.tags != null
				&& this.owner != null && this.name != null && this.pass > -1) {
			flag = true;
		} 
		
		return flag;
	}
}
