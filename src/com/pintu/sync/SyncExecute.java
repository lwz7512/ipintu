package com.pintu.sync;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import com.pintu.beans.Comment;
import com.pintu.beans.Story;
import com.pintu.beans.TPicItem;
import com.pintu.beans.Vote;
import com.pintu.dao.CacheAccessInterface;
import com.pintu.dao.DBAccessInterface;

public class SyncExecute implements Runnable {

	// 由Spring注入
	private DBAccessInterface dbVisitor;
	// 由Spring注入
	private CacheAccessInterface cacheVisitor;
		
	// 同步运行开关
	private Boolean syncFlag = true;

	private Logger log = Logger.getLogger(SyncExecute.class);

	// 未入库的对象
	private List<Object> unSavedObjList = new ArrayList<Object>();

	// 合法的可入库对象
	private List<Object> rightObjList = new ArrayList<Object>();

	// 缓存的需要入库的对象id
	private LinkedList<String> cachedObjIds = new LinkedList<String>();

	// 已入库的无用的数据对象id
	private List<String> needRemoveIds = new ArrayList<String>();

	public SyncExecute() {
		// TODO Auto-generated constructor stub
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

			// 批量同步故事
			// 并删除已同步的对象ID；
			syncStoryToDB();

			// 批量同步评论
			// 并删除已同步的对象ID；
			syncCommentToDB();

			// 批量同步投票
			// 并删除已同步的对象ID；

			syncVoteToDB();

//			cacheVisitor.traceCache();

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}


	private void syncVoteToDB() {
		unSavedObjList.clear();
		unSavedObjList = cacheVisitor
				.getUnSavedObj(CacheAccessInterface.VOTE_TYPE);
		cachedObjIds.clear();
		cachedObjIds = CacheAccessInterface.toSavedCacheIds
				.get(CacheAccessInterface.VOTE_TYPE);

		rightObjList.clear();
		rightObjList = getVaildObj(unSavedObjList,
				CacheAccessInterface.VOTE_TYPE);

		//对投票的入库做特殊处理，先查询是否对于某一故事的某一类投票的记录是否存在
		if (rightObjList != null && rightObjList.size() > 0) {
			for(int i = 0 ;i< rightObjList.size();i++){
				Vote vote = (Vote) rightObjList.get(i);
				//原则上，入库的投票，一个故事最多有是几个种类的有几条投票数据
				List<Vote> resList = dbVisitor.getVoteByFollowAndType(vote.getFollow(),vote.getType());
				int rows = 0;
				//存在对于某一故事的这一种类的评论，则更新数据库中此条记录，否则插入新的记录
				if(resList.size()>0){
					rows = dbVisitor.updateVote(rightObjList);
				}else{
					rows = dbVisitor.insertVote(rightObjList);
				}
				if (rows>0) {
					// 成功入库后，全部删除已入库的对象id
					needRemoveIds.clear();
					for (int j = 0; j < rightObjList.size(); j++) {
						needRemoveIds.add(vote.getId());
					}
					cachedObjIds.removeAll(needRemoveIds);
				}
			}
			
		} else {
			// log.info("当前没有需要入库的的投票！");
		}
		
	}

	private void syncCommentToDB() {
		unSavedObjList.clear();
		unSavedObjList = cacheVisitor
				.getUnSavedObj(CacheAccessInterface.COMMENT_TYPE);
		cachedObjIds.clear();
		cachedObjIds = CacheAccessInterface.toSavedCacheIds
				.get(CacheAccessInterface.COMMENT_TYPE);

		rightObjList.clear();
		rightObjList = getVaildObj(unSavedObjList,
				CacheAccessInterface.COMMENT_TYPE);

		if (rightObjList != null && rightObjList.size() > 0) {
			int m = dbVisitor.insertComment(rightObjList);
			if (m > 0) {
				// 成功入库后，全部删除已入库的对象id
				needRemoveIds.clear();
				for (int j = 0; j < rightObjList.size(); j++) {
					Comment cmt = (Comment) rightObjList.get(j);
					needRemoveIds.add(cmt.getId());
				}
				cachedObjIds.removeAll(needRemoveIds);
			}
		} else {
			// log.info("当前没有需要入库的评论！");
		}

	}

	private void syncStoryToDB() {
		unSavedObjList.clear();
		unSavedObjList = cacheVisitor
				.getUnSavedObj(CacheAccessInterface.STORY_TYPE);
		cachedObjIds.clear();
		cachedObjIds = CacheAccessInterface.toSavedCacheIds
				.get(CacheAccessInterface.STORY_TYPE);

		rightObjList.clear();
		rightObjList = getVaildObj(unSavedObjList,
				CacheAccessInterface.STORY_TYPE);

		if (rightObjList != null && rightObjList.size() > 0) {
			int m = dbVisitor.insertStory(rightObjList);
			if (m > 0) {
				// 成功入库后，全部删除已入库的对象id
				needRemoveIds.clear();
				for (int j = 0; j < rightObjList.size(); j++) {
					Story story = (Story) rightObjList.get(j);
					needRemoveIds.add(story.getId());
				}
				cachedObjIds.removeAll(needRemoveIds);
			}
		} else {
			// log.info("当前没有需要入库的故事！");
		}

	}

	private void syncPictureToDB() {
		unSavedObjList.clear();
		unSavedObjList = cacheVisitor
				.getUnSavedObj(CacheAccessInterface.PICTURE_TYPE);
		cachedObjIds.clear();
		cachedObjIds = CacheAccessInterface.toSavedCacheIds
				.get(CacheAccessInterface.PICTURE_TYPE);

		// 分离出可入库的正确图片对象
		rightObjList = getVaildObj(unSavedObjList,
				CacheAccessInterface.PICTURE_TYPE);

		if (rightObjList != null && rightObjList.size() > 0) {
			int m = dbVisitor.insertPicture(rightObjList);
			if (m > 0) {
				// 成功入库后，全部删除已入库的对象id
				for (int j = 0; j < rightObjList.size(); j++) {
					TPicItem tpic = (TPicItem) rightObjList.get(j);
					needRemoveIds.add(tpic.getId());
				}
				cachedObjIds.removeAll(needRemoveIds);
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
	private List<Object> getVaildObj(List<Object> objList, String type) {
		List<Object> resList = new ArrayList<Object>();
		if (objList != null && objList.size() > 0) {
			for (int i = 0; i < objList.size(); i++) {
				if (type.equals(CacheAccessInterface.PICTURE_TYPE)) {
					TPicItem tpic = (TPicItem) objList.get(i);
					if (tpic.getMobImgId() != null
							&& tpic.getMobImgSize() != null
							&& tpic.getMobImgPath() != null
							&& tpic.getRawImgId() != null
							&& tpic.getRawImgSize() != null
							&& tpic.getRawImgPath() != null) {
						resList.add(tpic);
					} else {
						// 有属性字段为空时为不全法的入库对象
						log.warn("不能入库的图片对象，ID为：" + tpic.getId());
					}
				} else if (type.equals(CacheAccessInterface.STORY_TYPE)) {
					Story story = (Story) objList.get(i);
					if (story.getFollow() != null && story.getOwner() != null &&
							story.getPublishTime() != null && story.getContent() != null) {
						resList.add(story);
					} else {
						// 有属性字段为空时为不全法的入库对象
						log.warn("不能入库的故事对象，ID为：" + story.getId());
					}
				} else if (type.equals(CacheAccessInterface.COMMENT_TYPE)) {
					Comment cmt = (Comment) objList.get(i);
					if (cmt.getFollow() != null && cmt.getOwner() != null
				&&cmt.getPublishTime() != null && cmt.getContent() != null) {
						resList.add(cmt);
					} else {
						// 有属性字段为空时为不全法的入库对象
						log.warn("不能入库的评论对象，ID为：" + cmt.getId());
					}
				}else if (type.equals(CacheAccessInterface.VOTE_TYPE)) {
					Vote vote = (Vote) objList.get(i);
					if (vote.getFollow() != null && vote.getType() != null
							&& vote.getAmount() >0) {
						resList.add(vote);
					} else {
						// 有属性字段为空时为不全法的入库对象
						log.warn("不能入库的投票对象，ID为：" + vote.getId());
					}
				}
			}
		}
		return resList;
	}

}
