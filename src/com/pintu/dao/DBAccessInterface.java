package com.pintu.dao;

import java.util.List;

import com.pintu.beans.Comment;
import com.pintu.beans.Event;
import com.pintu.beans.Favorite;
import com.pintu.beans.Gift;
import com.pintu.beans.Message;
import com.pintu.beans.Story;
import com.pintu.beans.User;
import com.pintu.beans.Wealth;


public interface DBAccessInterface {

	//新用户注册，交用户信息入库
	public String insertOneUser(User user);
	
	//品图入库
	public int insertPicture(List<Object> objList);
	
	//故事入库
	public String insertOneStory(Story story);
	
	//评论入库
	public String insertOneComment(Comment comment);
	
	//礼物入库
	public String insertOneGift(Gift gift);
	
	//将计算好的财富值入库
	public String insertOneWealth(Wealth wealth);
	
	//更新财富值
	public String updateOneWealth(String id,Wealth wealth);
	
	//社区事件入库
	public String insertOneEvent(Event event);
	
	//消息入库
	public String insertOneMessage(Message message);

	//添加收藏图片信息入库
	public String insertOneFavorite(Favorite favorite);
	
	
}
