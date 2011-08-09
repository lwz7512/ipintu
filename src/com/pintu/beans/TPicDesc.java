package com.pintu.beans;

import java.awt.image.BufferedImage;
import java.io.Serializable;

/**
 * 品图缩略内容，包括原图的缩略图，指向一个真正的品图
 * 用在首页画廊中
 * @author lwz
 *
 */
public class TPicDesc implements Serializable{

	
	private static final long serialVersionUID = 5476127095951218320L;

	//品图ID
	private String tpId;
	
	//品图缩略图，缩略图与大图一起生成，但是缩略图只存放在内存中，不写到磁盘上
	private String thumbnailId;
	
	//存储的生成的缩略图
	private String thumbnailPath;
	
	public String getThumbnailPath() {
		return thumbnailPath;
	}



	public void setThumbnailPath(String thumbnailPath) {
		this.thumbnailPath = thumbnailPath;
	}

	//品图状态：新发布、评论多、故事多、热图（两者都多）
	//这个状态来自于定时计算任务
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
