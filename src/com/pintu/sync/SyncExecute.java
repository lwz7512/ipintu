package com.pintu.sync;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.pintu.beans.Comment;
import com.pintu.beans.Message;
import com.pintu.beans.Story;
import com.pintu.beans.TPicItem;
import com.pintu.beans.Vote;
import com.pintu.dao.CacheAccessInterface;
import com.pintu.dao.DBAccessInterface;
import com.pintu.utils.PintuUtils;

public class SyncExecute implements Runnable {

	// 由Spring注入
	private DBAccessInterface dbVisitor;
	// 由Spring注入
	private CacheAccessInterface cacheVisitor;

	// 同步运行开关
	private Boolean syncFlag = true;
	
	//用于存储未通过校验的图片id和它尝试通过校验的次数
	private static Map<String,Integer> illegalCountMap = new HashMap<String,Integer>();

	private Logger log = Logger.getLogger(SyncExecute.class);

	public SyncExecute() {
	}

	public void setDbVisitor(DBAccessInterface dbVisitor) {
		this.dbVisitor = dbVisitor;
	}

	public void setCacheVisitor(CacheAccessInterface cacheVisitor) {
		this.cacheVisitor = cacheVisitor;
	}

	public void setSyncFlag(Boolean syncFlag) {
		this.syncFlag = syncFlag;
	}

	// TODO, 同步操作，遍历缓存查找未入库对象（saved属性为false）入库
	// 同步内容包括：TPicItem, Story, Vote, Comment
	// 对于TPicItem对象，要判断文件属性是否已经填充
	// 如果没有填充就先不入库，如果都已经有值就入库

	@Override
	public void run() {

		while (syncFlag) {

			// 批量同步图片
			// 并删除已同步的对象ID；
			syncPictureToDB();
			
			//处理错误图片
			processErrorPicture();

			// 批量同步故事
			// 并删除已同步的对象ID；
			syncStoryToDB();

			// 批量同步评论
			// 并删除已同步的对象ID；
			syncCommentToDB();

			// 批量同步投票
			// 并删除已同步的对象ID；

			syncVoteToDB();

			// cacheVisitor.traceCache();

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	private void syncPictureToDB() {
		List<Object> unSavedObjList = cacheVisitor
				.getUnSavedObj(CacheAccessInterface.PICTURE_TYPE);

		List<String> cachedObjIds = CacheAccessInterface.toSavedUserPicIds
				.get(CacheAccessInterface.PICTURE_TYPE);

		// 分离出可入库的正确图片对象
		List<Object> rightObjList = getVaildPicture(unSavedObjList);
		List<String> needRemoveIds = new ArrayList<String>();
		
		if (rightObjList != null && rightObjList.size() > 0) {
			int m = dbVisitor.insertPicture(rightObjList);
			if (m == rightObjList.size()) {
				// 成功入库后，全部删除已入库的对象id
				for (int j = 0; j < rightObjList.size(); j++) {
					TPicItem tpic = (TPicItem) rightObjList.get(j);
					needRemoveIds.add(tpic.getId());
					log.info(">>>Will delete already put in db for pictures:"+tpic.getId());
				}
				cachedObjIds.removeAll(needRemoveIds);
				
			} else {
				log.warn(">>>Picture does not match with the actual number of db");
			}
		} else {
			// log.info("当前没有需要入库的图片！");
		}
	}

	/**
	 * 校验取出的对象，返回可入库的对象
	 * 
	 * @param objList
	 * @return
	 */
	private List<Object> getVaildPicture(List<Object> objList) {
		List<Object> resList = new ArrayList<Object>();
		if (objList != null && objList.size() > 0) {
			for (int i = 0; i < objList.size(); i++) {
				TPicItem tpic = (TPicItem) objList.get(i);
				String picId = tpic.getId();
				if (tpic.isValid()) {
					resList.add(tpic);
					log.info(">>>Check the picture by preparing to db:" + tpic.getId());
				} else {
					//将不合法的图片统计
					if(illegalCountMap.size() > 0){
						for(String id:illegalCountMap.keySet()){
							if(picId.equals(id)){
								int count = illegalCountMap.get(id);
								illegalCountMap.put(id, count+1);
								//这里因为如果是第二次累加，那就是要删除的问题图片了
								log.warn(">>>Question picture detail:" +tpic.toString());
							}
						}
					}else{
						illegalCountMap.put(picId, 1);
					}
							
					// 有属性字段为空时为不全法的入库对象
					log.warn(">>>Can not insert picture object,ID:" + tpic.getId());
				}
			}
		}
		return resList;
	}
	

	private void processErrorPicture() {
		if(illegalCountMap.size() > 0){
			for(String id:illegalCountMap.keySet()){
				if(illegalCountMap.get(id) >=2){
					//删除缓存的图片id
					CacheAccessInterface.toSavedUserPicIds.get(CacheAccessInterface.PICTURE_TYPE).remove(id);
					//删除缓存中的图片对象
					boolean flag = cacheVisitor.removeTPic(id);
					if(flag){
						illegalCountMap.remove(id);
						//输出图片问题信息
						log.warn(">>>Deleted question picture is:" +id );
					}
				}
			}
		}
	}

	private void syncCommentToDB() {
		List<Object> unSavedObjList = cacheVisitor
				.getUnSavedObj(CacheAccessInterface.COMMENT_TYPE);

		Map<String, LinkedList<String>> cachedMap = CacheAccessInterface.toSavedCacheIds
				.get(CacheAccessInterface.COMMENT_TYPE);

		List<Object> rightObjList = getVaildComment(unSavedObjList);

		if (rightObjList != null && rightObjList.size() > 0) {
			int m = dbVisitor.insertComment(rightObjList);
			if (m == rightObjList.size()) {
				// 成功入库后，全部删除已入库的对象id
				cachedMap.clear();
			} else {
				log.warn(">>>Comments to db failed!");
			}
		} else {
			// log.info("当前没有需要入库的评论！");
		}

	}

	private List<Object> getVaildComment(List<Object> objList) {
		List<Object> resList = new ArrayList<Object>();
		if (objList != null && objList.size() > 0) {
			for (int i = 0; i < objList.size(); i++) {
				Comment cmt = (Comment) objList.get(i);
				if (cmt.isValid()) {
					resList.add(cmt);
					log.info(">>>Check the comment by preparing to db:" + cmt.getId());
				} else {
					// 有属性字段为空时为不合法的入库对象
					log.warn(">>>Can not insert comment object,ID:" + cmt.getId());
				}
			}
		}
		return resList;
	}

	private void syncStoryToDB() {
		List<Object> unSavedObjList = cacheVisitor
				.getUnSavedObj(CacheAccessInterface.STORY_TYPE);

		Map<String, LinkedList<String>> cachedMap = CacheAccessInterface.toSavedCacheIds
				.get(CacheAccessInterface.STORY_TYPE);

		List<Object> rightObjList = getVaildStory(unSavedObjList);

		if (rightObjList != null && rightObjList.size() > 0) {
			int m = dbVisitor.insertStory(rightObjList);
			if (m == rightObjList.size()) {
				// 成功入库后，全部删除已入库的对象id
				cachedMap.clear();
			} else {
				log.warn(">>>Stroies to db failed!");
			}
		} else {
			// log.info("当前没有需要入库的故事！");
		}

	}

	private List<Object> getVaildStory(List<Object> objList) {
		List<Object> resList = new ArrayList<Object>();
		if (objList != null && objList.size() > 0) {
			for (int i = 0; i < objList.size(); i++) {
				Story story = (Story) objList.get(i);
				if (story.isValid()) {
					resList.add(story);
					log.info(">>>Can not insert story object:" + story.getId());
				} else {
					log.warn(">>>Can not insert story object,ID:" + story.getId());
				}
			}
		}
		return resList;
	}

	private void syncVoteToDB() {
		List<Object> unSavedObjList = cacheVisitor
				.getUnSavedObj(CacheAccessInterface.VOTE_TYPE);

		Map<String, LinkedList<String>> cachedMap = CacheAccessInterface.toSavedCacheIds
				.get(CacheAccessInterface.VOTE_TYPE);

		List<Object> rightObjList = getVaildVote(unSavedObjList);

		// 对投票的入库做特殊处理，先查询是否对于某一故事的某一类投票的记录是否存在
		if (rightObjList != null && rightObjList.size() > 0) {
			int rows = 0;
			for (int i = 0; i < rightObjList.size(); i++) {
				Vote vote = (Vote) rightObjList.get(i);
				// 原则上，入库的投票，一个故事最多有是几个种类的有几条投票数据
				List<Vote> resList = dbVisitor.getVoteByFollowAndType(
						vote.getFollow(), vote.getType());

				// 存在对于某一故事的这一种类的评论，则更新数据库中此条记录，否则插入新的记录
				if (resList.size() > 0) {
					rows += dbVisitor.updateVote(vote);
				} else {
					rows += dbVisitor.insertVote(vote);
				}
			}

			if (rows == rightObjList.size()) {
				//FIXME 在投票入库成功时根据投票者的投票发一条消息给所投品图的故事作者
				addVoteMsg(rightObjList);
				// 成功入库后，全部删除已入库的对象id
				for (int j = 0; j < rightObjList.size(); j++) {
					Vote vote = (Vote) rightObjList.get(j);
					cachedMap.get(vote.getFollow()).remove(vote.getId());
					log.info("Will delete already put in db for votes:" + vote.getId());
					
				}
			} else {
				log.warn(">>>Votes to db failed!");
			}

		} else {
			// log.info("当前没有需要入库的的投票！");
		}

	}
	
	//投票附加消息
	private void addVoteMsg(List<Object> rightObjList){
		if(rightObjList.size() > 0){
			for(int i=0;i<rightObjList.size();i++){
				Vote vote = (Vote) rightObjList.get(i);
				Message msg = new Message();
				msg.setId(PintuUtils.generateUID());
				msg.setSender(vote.getVoter());
				msg.setReceiver(vote.getReceiver());
				String type = vote.getType();
				if(type.equals(Vote.EGG_TYPE)){
					msg.setContent("so bad story.");
				}else{
					msg.setContent("I like your story and I am on your side.");
				}
				msg.setWriteTime(PintuUtils.getFormatNowTime());
				msg.setRead(0);
				
				int res = dbVisitor.insertMessage(msg);
				if(res == 1){
					log.info(">>>Successful vote additional message to db!");
				}else{
					log.info(">>>Failure to vote additional message to db!");
				}
			}
		}
	}

	private List<Object> getVaildVote(List<Object> objList) {
		List<Object> resList = new ArrayList<Object>();
		if (objList != null && objList.size() > 0) {
			for (int i = 0; i < objList.size(); i++) {
				Vote vote = (Vote) objList.get(i);
				if (vote.isValid()) {
					resList.add(vote);
					log.info(">>>Check the vote by preparing to db:" + vote.getId());
				} else {
					log.warn(">>>Can not insert vote object,ID:" + vote.getId());
				}
			}
		}
		return resList;
	}

}
