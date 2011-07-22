package com.pintu.beans;

/**
 * 品图详情，比品图条目TPicItem多用户的基本资料
 * 用在点击缩略图后的内容展示
 * @author lwz
 *
 */
public class TPicDetails extends TPicItem{

	//用户头像，用户表存文件路径，从磁盘上找图
	private String avatarImgPath;
	
	//用户积分
	private String score;
	
	//用户等级
	private String level;
	
	public TPicDetails() {
		
	}

}
