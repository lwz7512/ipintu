package com.pintu.sync;

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
	//同时将该对象的saved值，置为true；	
	@Override
	public void run() {		
		while(syncFlag){	
			
			//TODO, synchronize...
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

}
