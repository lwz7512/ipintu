package com.pintu.sync;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pintu.dao.CacheAccessInterface;
import com.pintu.dao.DBAccessInterface;

public class SyncExecute implements Runnable {

	Logger log = LoggerFactory.getLogger(SyncExecute.class);
	//由Spring注入
	private DBAccessInterface dbVisitor;
	//由Spring注入
	private CacheAccessInterface cacheVisitor;
	//同步运行开关
	private Boolean syncFlag = true;
	
	
	public SyncExecute() {
		// TODO Auto-generated constructor stub
	}
	
	
	public void setDbVisitor(DBAccessInterface dbVisitor) {
		this.dbVisitor = dbVisitor;
	}

	public void setCacheVisitor(CacheAccessInterface cacheVisitor) {
		this.cacheVisitor = cacheVisitor;
	}

	public void setSyncFlag(Boolean syncFlag){
		this.syncFlag = syncFlag;
	}


	
	//TODO, 同步操作，遍历缓存查找未入库对象（saved属性为false）入库
	//同步内容包括：TPicItem, Story, Vote, Comment
	//对于TPicItem对象，要判断文件属性是否已经填充
	//如果没有填充就先不入库，如果都已经有值就入库
	
	@Override
	public void run() {		
		while(syncFlag){	
			
			//TODO, 批量同步图片
			//并删除已同步的对象ID；
			List<Object> objList=cacheVisitor.getUnSavedObj(CacheAccessInterface.PICTURE_TYPE);
			int m = 0;
			if(objList != null && objList.size() != 0){				
				m = dbVisitor.insertPicture(objList);
				if(m > 0){
					//FIXME, 成功入库后，全部删除已入库的对象id
					LinkedList<String> cachedPicIDs = CacheAccessInterface.toSavedCacheIds.get(CacheAccessInterface.PICTURE_TYPE);
					while(cachedPicIDs.size()>0){
						cachedPicIDs.remove();
					}
				}
			}else{
				//log.info("当前没有需要入库的图片！");
			}
     	
			
			//TODO, 批量同步故事
			//并删除已同步的对象ID；
			
			//TODO, 批量同步评论
			//并删除已同步的对象ID；
			
			
			//FIXME, TEST CACHE AVAILABILITY...
			cacheVisitor.traceCache();
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

}
