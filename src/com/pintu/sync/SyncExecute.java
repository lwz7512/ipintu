package com.pintu.sync;

import java.util.List;

import com.pintu.beans.TPicItem;
import com.pintu.dao.CacheAccessInterface;
import com.pintu.dao.DBAccessInterface;

public class SyncExecute implements Runnable {

	
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
			List<Object> objList=cacheVisitor.getUnSavedObj(cacheVisitor.PICTURE_TYPE);
			if(objList != null && objList.size() != 0){
				System.out.println("要入库的对象："+(TPicItem)objList.get(0));
				dbVisitor.insertPicture(objList);
				cacheVisitor.toSavedCacheIds.get(cacheVisitor.PICTURE_TYPE).remove();
				System.out.println("#########删除已入库的id"+cacheVisitor.toSavedCacheIds.values());
			}
     	
			
			//TODO, 批量同步故事
			//并删除已同步的对象ID；
			
			//TODO, 批量同步评论
			//并删除已同步的对象ID；
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

}
