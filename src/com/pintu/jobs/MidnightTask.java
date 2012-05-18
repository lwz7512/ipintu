package com.pintu.jobs;

/**
 * 用于每天零点来清除系统中的某些数据
 */
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import com.pintu.beans.Note;
import com.pintu.beans.TPicDetails;
import com.pintu.beans.TPicItem;
import com.pintu.beans.User;
import com.pintu.dao.CacheAccessInterface;
import com.pintu.dao.DBAccessInterface;

public class MidnightTask extends TimerTask {

	private DBAccessInterface dbAccess;
	private CacheAccessInterface cacheAccess;
	private Properties propertyConfigurer;
	
	// 新更新classical的故事id（'','',''）
	public static StringBuffer newClassicalStoryIds = new StringBuffer();
	
	//经典列表
	public  static List<TPicDetails> classicalList = new ArrayList<TPicDetails>();
	
	//收藏列表
	public  static List<TPicDetails> collectList = new ArrayList<TPicDetails>();
	
	//贴图达人列表
	public static List<User> picDarenList = new ArrayList<User>();
	
	//发评论达人列表
	public static List<User> cmtDarenList = new ArrayList<User>();
	
	
	private Logger log = Logger.getLogger(MidnightTask.class);

	public MidnightTask(DBAccessInterface dbVisitor,
			CacheAccessInterface cacheVisitor, Properties propertyConfigurer) {
		this.dbAccess = dbVisitor;
		this.cacheAccess = cacheVisitor;
		this.propertyConfigurer = propertyConfigurer;
	}

	@Override
	public void run() {
		log.info(">>> midnight task executed...");
		
		//之前用投票数更新经典
//		findAndSetClassical();
		
		//更新图片的浏览数
		boolean flag = updatePicBrowseCount();
		if(flag){
			//如果p_browseCount更新成功则清除缓存计数
			clearCounter();
		}
		
		//将一天新的top经典和收藏查了放缓存
		calculateClassicalAndCollectTop();
		
		//将社区发图和发评论达人查询放缓存
		calculatePicAndCmtDaren();
		
		
		//FIXME 更新条子的关注数和浏览数
		boolean atte = updateNoteAttentionCount();
		if(atte){
			clearNoteAttentionCount();
		}
		boolean inte =updateNoteInterestCount();
		if(inte){
			clearNoteInterestCount();
		}
	}
	
	private void calculatePicAndCmtDaren() {
		picDarenList.clear();
		picDarenList=this.dbAccess.getPicDaren();
		cmtDarenList.clear();
		cmtDarenList=this.dbAccess.getCmtDaren();
	}

	private void calculateClassicalAndCollectTop() {
		int classicalNum =  Integer.parseInt(propertyConfigurer
				.getProperty("classicalBrowseCount"));
		classicalList.clear();
		classicalList=this.dbAccess.classicalStatistics(classicalNum);
		collectList.clear();
		collectList=this.dbAccess.collectStatistics();
	}

//	private void findAndSetClassical() {
//		List<Story> classicalList = this.dbAccess.getClassicalPintu();
//		// 根据故事id取得投票信息
//		List<Vote> voteList = this.dbAccess.getAllVote();
//		List<String> needUpdateStoryIds = new ArrayList<String>();
//		if (voteList.size() > 0) {
//			for (int i = 0; i < voteList.size(); i++) {
//				Vote vote = voteList.get(i);
//				// 查看并判断经典投票的数量
//				if (vote.getType().equals(Vote.STAR_TYPE)
//						&& vote.getAmount() > Integer
//								.parseInt(propertyConfigurer
//										.getProperty("classicalVoteNum"))) {
//
//					needUpdateStoryIds.add(vote.getFollow());
//				}
//			}
//		}
//
//		//对比所有经典和库中已置为经典的故事，留下新的需要更新的经典
//		for(int j=0;j<classicalList.size();j++){
//			Story classicalStory = classicalList.get(j);
//			if(needUpdateStoryIds.contains(classicalStory.getId())){
//				needUpdateStoryIds.remove(classicalStory.getId());
//			}
//		}
//
//		if (needUpdateStoryIds.size() > 0) {
//			//为新的经典故事id存储为（'','',''）的形式
//			for(int i=0;i<needUpdateStoryIds.size();i++){
//				
//				if(newClassicalStoryIds.length()>0){
//					newClassicalStoryIds.append(",");
//				}
//				
//				newClassicalStoryIds.append("'");
//				newClassicalStoryIds.append(needUpdateStoryIds.get(i));
//				newClassicalStoryIds.append("'");
//			}
//			
//			// 更新数据库中的经典字段
//			int res = this.dbAccess
//					.updateStoryClassical(needUpdateStoryIds);
//			if (res == needUpdateStoryIds.size()) {
//				log.info("Update story classical success!");
//			} else {
//				log.info("Update story classical failed!");
//			}
//		}
//	}

	private boolean updatePicBrowseCount() {
		boolean flag = true;
		Map<String,Integer> hotPicMap = CacheAccessInterface.hotPicCacheIds;
		log.info("HotPicMap size is:"+hotPicMap.size());
		if(hotPicMap.size() > 0){
			List<TPicItem> browseCountList = new ArrayList<TPicItem>();
			for(String id:hotPicMap.keySet()){
				TPicItem tpic = new TPicItem();
				tpic.setId(id);
				tpic.setBrowseCount(hotPicMap.get(id));
				browseCountList.add(tpic);
			}
			log.info("Need to update the broseCount picturesize is:"+browseCountList.size());
			if(browseCountList != null && browseCountList.size() > 0){
				int res = this.dbAccess.updatePicBrowseCount(browseCountList);
				if(res == hotPicMap.size()){
					log.info(">>>Update pic browseCount success");
				}else{
					log.info(">>>Incorrect number of updates");
					flag = false;
				}
			}
		}
		return flag;
	}

	
	private void clearCounter() {
		// 将一天内查看过详情的图片点击量清零
		this.cacheAccess.clearHotPicCacheIds();
	}
	

	private boolean updateNoteInterestCount() {
		boolean flag = true;
		Map<String,Integer> interestMap =CacheAccessInterface.noteInterestMap;
		log.info("NoteInterestMap size is:"+interestMap.size());
		if(interestMap.size() > 0){
			List<Note> interestCountList = new ArrayList<Note>();
			for(String id:interestMap.keySet()){
				Note note = new Note();
				note.setId(id);
				note.setInterest(interestMap.get(id));
				interestCountList.add(note);
			}
			log.info("Need to update the interest count is:"+interestCountList.size());
	
			if(interestCountList != null && interestCountList.size() > 0){
				int res = this.dbAccess.updateNoteInterest(interestCountList);
				if(res == interestMap.size()){
					log.info(">>>Update note interest count success");
				}else{
					log.info(">>>Incorrect number of updates");
					flag = false;
				}
			}
		}
		return flag;
	}

	private boolean updateNoteAttentionCount() {
		boolean flag = true;
		Map<String,Integer> attentionMap =CacheAccessInterface.noteAttentionMap;
		log.info("NoteAttentionMap size is:"+attentionMap.size());
		if(attentionMap.size() > 0){
			List<Note> attentionCountList = new ArrayList<Note>();
			for(String id:attentionMap.keySet()){
				Note note = new Note();
				note.setId(id);
				note.setAttention(attentionMap.get(id));
				attentionCountList.add(note);
			}
			log.info("Need to update the attention count is:"+attentionCountList.size());
	
			if(attentionCountList != null && attentionCountList.size() > 0){
				int res = this.dbAccess.updateNoteAttention(attentionCountList);
				if(res == attentionCountList.size()){
					log.info(">>>Update pic attention count success");
				}else{
					log.info(">>>Incorrect number of updates");
					flag = false;
				}
			}
		}
		return flag;
	}
	
	private void clearNoteInterestCount() {
		this.cacheAccess.clearCacheNoteInterest();
	}

	private void clearNoteAttentionCount() {
		this.cacheAccess.clearCacheNoteAttention();
	}
	
}
