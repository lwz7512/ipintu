package com.pintu.dao;

import java.util.List;

import com.pintu.beans.Comment;

import com.pintu.beans.Story;
import com.pintu.beans.TPicItem;
import com.pintu.beans.User;
import com.pintu.beans.Vote;


public interface DBAccessInterface {

	//新用户注册，交用户信息入库
	public String insertOneUser(User user);
	
	//品图入库
	public int insertPicture(List<Object> objList);
	
	//根据userId取用户信息
	public User getUserById(String id);
	
//	//礼物入库
//	public String insertOneGift(Gift gift);
//	
//	//将计算好的财富值入库
//	public String insertOneWealth(Wealth wealth);
//	
//	//更新财富值
//	public String updateOneWealth(String id,Wealth wealth);
//	
//	//社区事件入库
//	public String insertOneEvent(Event event);
//	
//	//消息入库
//	public String insertOneMessage(Message message);
//
//	//添加收藏图片信息入库
//	public String insertOneFavorite(Favorite favorite);
	
	//获取一个时间段内的图片id信息
	public List<String> getPicIdsByTime(String startTime, String endTime);
	
	/**
	 * 取数据库中的图片数据到缓存
	 * @param today 格式"yyyy-MM-dd HH:mm:ss"
	 * @return
	 */

	public List<TPicItem> getPictureForCache(String today);
	
	public List<Story> getStoryForCache(String today);
	
	public List<Comment> getCommentForCache(String today);

	/**
	 * 根据故事的id来取评论到缓存
	 * @param storyIds
	 * @return
	 */
	public List<Vote> getVoteForCache(String storyIds);

	public int insertComment(List<Object> objList);

	public int insertStory(List<Object> objList);
	
	public int insertVote(Vote vote);
	
	public int updateVote(Vote vote);

	public List<Comment> getCommentsOfPic(String tpID);

	public List<Story> getStoriesOfPic(String tpID);
	
	public List<Vote> getVoteOfStory(String storyID);

	public List<Vote> getVoteByFollowAndType(String storyId, String type);

}
