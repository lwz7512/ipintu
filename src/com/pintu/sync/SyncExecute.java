package com.pintu.sync;

import com.pintu.dao.CacheAccessInterface;
import com.pintu.dao.DBAccessInterface;

public class SyncExecute implements Runnable {

	
	//��Springע��
	private DBAccessInterface dbVisitor;
	//��Springע��
	private CacheAccessInterface cacheVisitor;
	//ͬ�����п���
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


	
	//TODO, ͬ�������������������δ������saved����Ϊfalse�����
	//ͬ�����ݰ�����TPicItem, Story, Vote, Comment
	//����TPicItem����Ҫ�ж��ļ������Ƿ��Ѿ����
	//���û�������Ȳ���⣬������Ѿ���ֵ�����
	//ͬʱ���ö����savedֵ����Ϊtrue��	
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
