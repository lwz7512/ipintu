package com.pintu.sync;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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

		Map<String, LinkedList<String>> cachedMap = CacheAccessInterface.toSavedCacheIds
				.get(CacheAccessInterface.PICTURE_TYPE);

		// 分离出可入库的正确图片对象
		List<Object> rightObjList = getVaildPicture(unSavedObjList);

		if (rightObjList != null && rightObjList.size() > 0) {
			int m = dbVisitor.insertPicture(rightObjList);
			if (m == rightObjList.size()) {
				// 成功入库后，全部删除已入库的对象id
				for (int j = 0; j < rightObjList.size(); j++) {
					TPicItem tpic = (TPicItem) rightObjList.get(j);
					cachedMap.get(tpic.getOwner()).remove(tpic.getId());
					log.info("即将删除已入库图片为：" + tpic.getId());
				}
			} else {
				log.warn("图片入库数目与实际不符");
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
				if (tpic.isValid()) {
					resList.add(tpic);
					log.info("校验通过准备入库的故事：" + tpic.getId());
				} else {
					// 有属性字段为空时为不全法的入库对象
					log.warn("不能入库的图片对象，ID为：" + tpic.getId());
				}
			}
		}
		return resList;
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
				for (int j = 0; j < rightObjList.size(); j++) {
					Comment cmt = (Comment) rightObjList.get(j);
					cachedMap.get(cmt.getFollow()).remove(cmt.getId());
					log.info("即将删除已入库评论为：" + cmt.getId());
				}
			} else {
				log.warn("评论入库失败");
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
					log.info("校验通过准备入库的评论：" + cmt.getId());
				} else {
					// 有属性字段为空时为不合法的入库对象
					log.warn("不能入库的评论对象，ID为：" + cmt.getId());
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
				for (int j = 0; j < rightObjList.size(); j++) {
					Story story = (Story) rightObjList.get(j);
					cachedMap.get(story.getFollow()).remove(story.getId());
					log.info("即将删除已入库故事为：" + story.getId());
				}
			} else {
				log.warn("故事入库失败");
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
					log.info("校验通过准备入库的故事：" + story.getId());
				} else {
					log.warn("不能入库的故事对象，ID为：" + story.getId());
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
				// 成功入库后，全部删除已入库的对象id
				for (int j = 0; j < rightObjList.size(); j++) {
					Vote vote = (Vote) rightObjList.get(j);
					cachedMap.get(vote.getFollow()).remove(vote.getId());
					log.info("即将删除已入库投票为：" + vote.getId());
				}
			} else {
				log.warn("投票入库失败");
			}

		} else {
			// log.info("当前没有需要入库的的投票！");
		}

	}

	private List<Object> getVaildVote(List<Object> objList) {
		List<Object> resList = new ArrayList<Object>();
		if (objList != null && objList.size() > 0) {
			for (int i = 0; i < objList.size(); i++) {
				Vote vote = (Vote) objList.get(i);
				if (vote.isValid()) {
					resList.add(vote);
					log.info("校验通过准备入库的投票：" + vote.getId());
				} else {
					log.warn("不能入库的投票对象，ID为：" + vote.getId());
				}
			}
		}
		return resList;
	}

}
