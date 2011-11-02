package com.pintu.beans;

import java.io.Serializable;

/**
 * 品图缩略内容，包括原图的缩略图，指向一个真正的品图
 * 用在首页画廊中
 * @author lwz
 *
 */
public class TPicDesc implements Serializable{

	
	private static final long serialVersionUID = 5476127095951218320L;

	public static final String THUMBNIAL = "_Thumbnail";
	
	//品图ID
	private String tpId;
	
	//品图缩略图，缩略图文件与大图文件一起生成
	//缩略图的id就是tpId加一个_Thumbnail后缀
	//同时写文件时用这个当文件名
	private String thumbnailId;
	

	//品图状态：新发布、评论多、故事多、热图（两者都多）
	//图的状态，热图，经典等等
	//0: 默认状态
	//1: 有故事状态
	//2: 热图状态
	//3: 经典状态
	//TODO FIXME 这个状态来自于定时计算任务(高级应用)
	private String status;
	
	//生成时间，保存毫秒数 (图片文件的最后修改时间)
	private String creationTime;
	
	public TPicDesc() {
		
	}

	public String getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(String creationTime) {
		this.creationTime = creationTime;
	}

	public String getTpId() {
		return tpId;
	}

	public void setTpId(String tpId) {
		this.tpId = tpId;
	}

	public String getThumbnailId() {
		return thumbnailId;
	}

	public void setThumbnailId(String thumbnailId) {
		this.thumbnailId = thumbnailId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}


}
