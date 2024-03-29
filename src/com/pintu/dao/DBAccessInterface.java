package com.pintu.dao;

import java.util.List;
import java.util.Map;

import com.pintu.beans.Applicant;
import com.pintu.beans.Event;
import com.pintu.beans.Favorite;
import com.pintu.beans.Gift;
import com.pintu.beans.Message;
import com.pintu.beans.Note;
import com.pintu.beans.Story;
import com.pintu.beans.TPicDesc;
import com.pintu.beans.TPicDetails;
import com.pintu.beans.TPicItem;
import com.pintu.beans.Tag;
import com.pintu.beans.User;
import com.pintu.beans.UserExtend;
import com.pintu.beans.Vote;
import com.pintu.beans.Wealth;


public interface DBAccessInterface {

		//新用户注册，交用户信息入库
		public int insertUser(User user);
		//更新用户积分包括可用积分
		public int updateUserScore(List<User> userList);
		
		//品图入库
		public int insertPicture(List<Object> objList);
		
		//根据userId取用户信息
		public User getUserById(String id);
		
		//更新故事表的classical字段
		public int updateStoryClassical(List<String> storyIds);
		
		//更新可用积分
		public int updateUserExchageScore(String userId,int remainScore);
	
		//更新用户等级状态
		public int updateUserLevel(String userId,int level);
		
		public int updateUserLevel(List<Map<String, Integer>> idLevelList);
	
		//将计算好的财富值入库
		public int  insertOnesWealth( List<Wealth> wList);
		
		//更新财富值
		public int updateOnesWealth( List<Wealth> wList);
		
		//删除amount为0的财产对象
		public int deleteOnesWealth(String type, String userId);
		
		//得到某一用户财产信息
		public List<Wealth> getOnesWealth(String userId);
		
		//更改密码
		public int updatePassword(String password,String userId);
		
		//上传头像
		public int updateAvatarAndNickname(String avatarPath, String nickName, String userId);
		
	//	//社区事件入库
	//	public String insertOneEvent(Event event);
	//	
	//	//消息入库
	//	public String insertOneMessage(Message message);
	//
	//	//添加收藏图片信息入库
	//	public String insertOneFavorite(Favorite favorite);
	//		
	//	//礼物入库
	//	public String insertOneGift(Gift gift);
	
		
		
		/**
		 * 取数据库中的图片数据到缓存
		 * @param today 格式"yyyy-MM-dd HH:mm:ss"
		 * @return
		 */
	
		public List<TPicItem> getPictureForCache(String startTime,String endTime);
		
		public List<Story> getStoryForCache(String picIds);
		
		/**
		 * 根据故事的id来取评论到缓存
		 * @param storyIds
		 * @return
		 */
		public List<Vote> getVoteForCache(String picIds);
	
		public int insertStory(List<Object> objList);
		
		public int insertVote(Vote vote);
		
		public int updateVote(Vote vote);
	
		public List<Story> getStoriesOfPic(String tpID);
		
		public List<Vote> getVoteOfPic(String picId);
	
		public List<Vote> getVoteByFollowAndType(String picId, String type);
	
		public TPicItem getPictureById(String tpId);
	
		public int insertMessage(Message msg);
	
		public List<Message> getUserMessages(String userId);
	
		public int updateMsg(List<String> msgIds);
	
		public List<Story> getClassicalPintu();
		
		public List<Story> getClassicalPintuByIds(String ids);
		
		public Map<String,Integer> getOnesPicCountByTime(String startTime, String endTime);
		
		public Map<String,Integer> getOnesStoryCountByTime(String startTime, String endTime);
		
		public Map<String, Integer> getUserExchangeInfo(String userIds);
		
		public Map<String, Integer> getUserScoreInfo(String userIds);
		
		public List<Wealth> getUsersWealthInfo(	String userId);
		
	
		public List<Vote> getAllVote();
		
		public int insertFavorite(Favorite fav);
		
		public int deleteFavoriteById(String fId);
		
		public int checkExistFavorite(String userId, String picId);
		
		
		public List<Story> getStoriesByUser(String userId, int pageNum, int pageSize);
		
		public List<TPicItem> getTpicsByUser(String userId, int pageNum, int pageSize);
		
		public List<TPicItem> getFavoriteTpics(String userId, int pageNum, int pageSize);
		
		public List<Gift> getExchangeableGifts();
		
		public List<Event> getCommunityEvents(String today);
		
		public int insertGift(Gift gift);
		
		public int insertEvent(Event event);
		
		public int getTPicCountByUser(String userId);
		
		public int getStoryCountByUser(String userId);
		
		public User getExistUser(String account);
		
		public int getExistApplicant(String account);
		
		public int insertApplicant(Applicant tempUser);
		
		public List<Applicant> getApplicant();
		
		public int deleteTempUser(String account);
		
		public int deleteUsedCode(String inviteCode);
		
		public int updateApplicant(String inviteCode,String account);
		
		public String getExistApplicant(String account, String inviteCode);
		
		public int updatePicBrowseCount(List<TPicItem> browseCountList);
		
		public List<TPicDetails> classicalStatistics(int classicalNum);
		
		public List<TPicDetails> collectStatistics();
		
		public List<TPicDetails> getGalleryForWeb(int pageNum,int pageSize);
		
		public List<TPicDetails> searchByTagOr(String tagOr);
		
		public List<TPicDetails> searchByTagAnd(String[] tagArr);
		
		public String searchTags(String string);
		
		public String insertTag(Tag tag);
		
		public int updateTagBrowse(String tagId);
		
		public int insertCategory(String id,String picId, String tagId);
		
		public List<Tag> getHotTags(int topNum);
		
		public List<Tag> geSystemTags();
		
		public int deleteCmtById(String sId);
		
		public int deletePictureById(String pId);
		
		public List<TPicDesc> getThumbnailByTag(String tagId, int pageNum,
				int pageSize);
		
		public List<User> getPicDaren();
		
		public List<User> getCmtDaren();
		
		public int getPicCoolCount(String picId);
		
		public int getExistNickname(String nickName);
		
		public List<Tag> getPicTagsById(String picId);
		
		public int confirmPassword(String userId, String md5Pwd);
		
		public List<TPicDesc> getRandGallery(int size);
		
		public int appendUserscoreByVote(String picId, int score);
		
		public List<User> getActiveUserRandking(int size);
		
		public int reviewPictureById(String picId);
		
		public int getAcceptedApplicant(String account);
		
		public int addExtendUser(UserExtend userExtend);
		
		public String getExtendUser(String uid);
		
		public int updateExtendUser(UserExtend userExtend, String uid);
		
		public String getTokenById(String userId);
		
		public int addNote(Note note);
		
		public int deleteNoteById(String noteId);
		
		public int updateNoteById(String noteId, String type, String title,
				String content);
		
		public List<Note> getUserNotes(String userId);
		
		public List<Note> getCommunityNotes(int pageNum);
		
		public int updateNoteAttention(List<Note> attentionCountList);
		
		public int updateNoteInterest(List<Note> interestCountList);
		
		public Note getNoteById(String noteId);
		
		public int updateWeiboUesr(String userId, String account,
				String encryptPwd);
		
		
}
