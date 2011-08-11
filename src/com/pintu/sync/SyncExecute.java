package com.pintu.sync;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import com.pintu.beans.TPicItem;
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
			
			
			// TODO, 批量同步图片
			// 并删除已同步的对象ID；
			List<Object> objList = cacheVisitor
					.getUnSavedObj(CacheAccessInterface.PICTURE_TYPE);

			LinkedList<String> cachedPicIDs = CacheAccessInterface.toSavedCacheIds
					.get(CacheAccessInterface.PICTURE_TYPE);

			List<String> overIds = new ArrayList<String>();

			//分离出可入库的正确图片对象
			List<TPicItem> picItemList = getVaildObj(objList);
			
			if(picItemList != null && picItemList.size() > 0){
				int m = dbVisitor.insertPicture(picItemList);
				if (m > 0) {
					// FIXME, 成功入库后，全部删除已入库的对象id
					for (int j = 0; j < picItemList.size(); j++) {
						TPicItem tpic = (TPicItem) picItemList.get(j);
						overIds.add(tpic.getId());
					}
					cachedPicIDs.removeAll(overIds);
				}
			} else {
				// log.info("当前没有需要入库的图片！");
			}

			// TODO, 批量同步故事
			// 并删除已同步的对象ID；

			// TODO, 批量同步评论
			// 并删除已同步的对象ID；

			// FIXME, TEST CACHE AVAILABILITY...
			cacheVisitor.traceCache();

			
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	/**
	 * 校验取出的图片对象，返回可入库的对象
	 * @param objList
	 * @return
	 */
	public List<TPicItem> getVaildObj(List<Object> objList) {
		List<TPicItem> resList = new ArrayList<TPicItem>();
		if (objList != null && objList.size() > 0) {
			for (int i = 0; i < objList.size(); i++) {
				TPicItem tpic = (TPicItem) objList.get(i);
				if (tpic.getMobImgId() != null && tpic.getMobImgSize() != null
						&& tpic.getMobImgPath() != null
						&& tpic.getRawImgId() != null
						&& tpic.getRawImgSize() != null
						&& tpic.getRawImgPath() != null) {

					resList.add(tpic);
				}else{
					//有属性字段为空时为不全法的入库对象
					log.warn("不能入库的图片对象，ID为："+tpic.getId());
				}
			}
		}
		return resList;
	}

}
